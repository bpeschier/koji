package koji.audio;

import javazoom.jl.decoder.*;

import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * TODO:
 * - blocking
 */
public class Player {

    private static final Logger logger = Logger.getLogger(Player.class.getName());


    private final Map<Bitstream, Float> queue = new LinkedHashMap<Bitstream, Float>();

    private Decoder decoder;
    private AudioDevice audio;
    private boolean blocking = false;

    private boolean playing = false;
    private float left = 0;
    private boolean mute = false;


    private String name;
    private Listener listener;

    public Player(String name) {
        this(name, false);
    }

    public Player(String name, boolean blocking) {
        this.name = name;
        this.blocking = blocking;

        if (!blocking) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        play();
                    } catch (JavaLayerException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

    }

    public boolean isPlaying() {
        return playing;
    }

    public void queue(InputStream stream, float from, float to) throws JavaLayerException {
        Bitstream bitstream = new Bitstream(stream);
        if (from > 0) {
            skipStreamTo(bitstream, from);
        }
        queue(bitstream, to - from);
    }

    public void play(InputStream stream, float from, float to) throws JavaLayerException {
        mute = false;
        Bitstream bitstream = new Bitstream(stream);
        if (from > 0) {
            skipStreamTo(bitstream, from);
        }
        synchronized (queue) {
            queue.clear();
            queue(bitstream, to - from);
        }
    }

    private void queue(Bitstream bitstream, float to) {
        synchronized (queue) {
            queue.put(bitstream, to);
        }

        if (!isPlaying()) {
            try {
                resume();
            } catch (JavaLayerException e) {
                e.printStackTrace();
            }
        }
    }

    private void resume() throws JavaLayerException {
        if (playing) {
            return;
        }

        if (blocking) {
            play();
        } else {
            synchronized (queue) {
                logger.fine("Resuming playback");
                queue.notify();
            }
        }
    }

    private void play() throws JavaLayerException {
        if (!blocking && queue.size() == 0) {
            this.waitForQueue();
        }

        while (queue.size() > 0) {
            Header h;
            audio = new AudioDevice(name);
            audio.open(decoder = new Decoder());

            audio.setVolume((mute) ? 0 : 1);

            Iterator<Map.Entry<Bitstream, Float>> entryIterator = queue.entrySet().iterator();
            Map.Entry<Bitstream, Float> entry = entryIterator.next();

            synchronized (queue) {
                entryIterator.remove();
            }

            logger.fine("Starting playback");
            playing = true;

            Bitstream bitstream = entry.getKey();
            left = entry.getValue();

            // report to listener
            if (listener != null) {
                listener.playbackStarted(this);
            }

            while (left > 0 && playing) {
                h = playFrame(bitstream);
                if (h != null) {
                    left -= h.ms_per_frame();
                }
                playing = playing && h != null;
            }

            playing = false;

            // last frame, ensure all data flushed to the audio device.
            AudioDevice out = audio;
            if (out != null) {
                out.flush();
                synchronized (this) {
                    closeStream(bitstream);
                    audio.close();
                }

                // report to listener
                if (listener != null) {
                    listener.playbackFinished(this);
                }
            }

            if (!blocking && queue.size() == 0) {
                listener.queueDone(this);
                this.waitForQueue();
            }
        }
        if (listener != null) {
            listener.queueDone(this);
        }
    }

    private void waitForQueue() {
        try {
            synchronized (queue) {
                queue.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void closeStream(Bitstream bitstream) {
        try {
            bitstream.close();
        } catch (BitstreamException ex) {
            ex.printStackTrace();
        }
    }

    public synchronized void close() {
        if (audio != null) {
            audio.close();
            audio = null;
        }
    }

    protected Header playFrame(Bitstream bitstream) throws JavaLayerException {
        try {
            AudioDevice out = audio;
            if (out == null) {
                return null;
            }

            Header h = bitstream.readFrame();
            if (h == null) {
                return null;
            }

            // sample buffer set when decoder constructed
            SampleBuffer output = (SampleBuffer) decoder.decodeFrame(h, bitstream);

            synchronized (this) {
                out = audio;
                if (out != null) {
                    out.write(output.getBuffer(), 0, output.getBufferLength());
                }
            }

            bitstream.closeFrame();
            return h;
        } catch (RuntimeException ex) {
            throw new JavaLayerException("Exception decoding audio frame", ex);
        }
    }

    protected Header skipFrame(Bitstream bitstream) throws JavaLayerException {
        Header h = bitstream.readFrame();
        bitstream.closeFrame();
        return h;
    }

    public void skipStreamTo(Bitstream stream, float ms) throws JavaLayerException {
        Header h;
        boolean done = false;
        float offset = ms;
        while (offset > 0 && !done) {
            h = skipFrame(stream);
            done = h == null;
            if (!done) {
                offset -= h.ms_per_frame();
            }
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void stop() {
        synchronized (queue) {
            queue.clear();
        }
        playing = false;
    }

    public void mute() {
        mute = true;
        if (audio != null) {
            audio.shiftVolumeTo(0);
        }
    }

    public void fadeOut() {
        mute = true;
        synchronized (queue) {
            queue.clear();
        }
        left = 1000;
        if (audio != null) {
            audio.shiftVolumeTo(0);
        }
    }

    public void fadeIn() {
        mute = false;
        if (audio != null) {
            audio.shiftVolumeTo(1);
        }
    }


    public interface Listener {
        void playbackStarted(Player player);

        void playbackFinished(Player player);

        void queueDone(Player player);
    }

}
