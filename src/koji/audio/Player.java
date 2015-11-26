package koji.audio;

import javazoom.jl.decoder.*;

import java.io.InputStream;

public class Player {

    private Bitstream bitstream;
    private Decoder decoder;
    private AudioDevice audio;

    private Listener listener;

    public Player(InputStream stream) throws JavaLayerException {
        this(stream, null);
    }

    public Player(InputStream stream, AudioDevice device) throws JavaLayerException {
        bitstream = new Bitstream(stream);

        if (device != null) {
            audio = device;
        } else {
            audio = new AudioDevice();
        }
        audio.open(decoder = new Decoder());
    }

    public boolean setGain(float newGain) {
        return audio != null && audio.setLineGain(newGain);
    }

    public void play() throws JavaLayerException {
        play(Float.MAX_VALUE);
    }

    public void play(float ms) throws JavaLayerException {

        Header h;
        boolean playing = true;

        // report to listener
        if (listener != null) {
            listener.playbackStarted(this);
        }

        while (ms > 0 && playing) {
            h = decodeFrame();
            if (h != null) {
                ms -= h.ms_per_frame();
            }
            playing = h != null && ms > 0;
        }

        if (!playing) {
            // last frame, ensure all data flushed to the audio device.
            AudioDevice out = audio;
            if (out != null) {
                out.flush();
                synchronized (this) {
                    close();
                }

                // report to listener
                if (listener != null) {
                    listener.playbackFinished(this);
                }
            }
        }
    }

    public synchronized void close() {
        AudioDevice out = audio;
        if (out != null) {
            audio = null;
            // this may fail, so ensure object state is set up before
            // calling this method.
            out.close();
            try {
                bitstream.close();
            } catch (BitstreamException ex) {
                ex.printStackTrace();
            }
        }
    }

    protected Header decodeFrame() throws JavaLayerException {
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

    protected Header skipFrame() throws JavaLayerException {
        Header h = bitstream.readFrame();
        bitstream.closeFrame();
        return h;
    }

    public void skipTo(float ms) throws JavaLayerException {
        Header h;
        boolean done = false;
        float offset = ms;
        while (offset > 0 && !done) {
            h = skipFrame();
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
        listener.playbackFinished(this);
        close();
    }

    public void fadeOut() {
        stop();
    }

    public interface Listener {
        void playbackStarted(Player player);

        void playbackFinished(Player player);

    }

}
