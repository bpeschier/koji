package koji.audio;

import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Header;

import javax.sound.sampled.SourceDataLine;
import java.io.IOException;

public class Player {

    private RepeatableBitstream bitstream;
    private SourceDataLine line;

    private boolean playing = false;
    private float left = 0;

    private Listener listener;

    public Player(SourceDataLine line, RepeatableBitstream bitstream) {
        this.line = line;
        this.bitstream = bitstream;
        left = Float.MAX_VALUE;
    }

    private synchronized void setPlaying(boolean isPlaying) {
        if (isPlaying && !playing) {
            playing = true;
            if (listener != null) {
                listener.playbackStarted(this);
            }
        } else if (!isPlaying && playing) {
            playing = false;
            if (listener != null) {
                listener.playbackFinished(this);
            }
        }
    }

    public void stop() {
        setPlaying(false);
    }

    public void play() {

        setPlaying(true);

        Header h;
        while (left > 0 && playing) {
            h = playFrame();
            if (h != null) {
                left -= h.ms_per_frame();
            }
            setPlaying(playing && h != null);
        }

        closeStream();

        setPlaying(false);
    }

    protected void closeStream() {
        try {
            bitstream.close();
        } catch (BitstreamException e) {
            e.printStackTrace();
        }
    }

    protected Header playFrame() {
        Header h = null;
        try {
            h = bitstream.readHeader();
        } catch (BitstreamException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (h == null) {
            return null;
        }

        byte[] data = bitstream.readFrame(h);
        line.write(data, 0, data.length);

        bitstream.closeFrame(h);
        return h;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void playbackStarted(Player player);

        void playbackFinished(Player player);
    }

}
