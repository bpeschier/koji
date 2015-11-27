package koji.audio;

import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDeviceBase;

import javax.sound.sampled.*;

public class AudioDevice extends AudioDeviceBase implements Runnable {
    private SourceDataLine source = null;
    private AudioFormat fmt = null;
    private byte[] byteBuf = new byte[4096];

    float currDB = 0F;
    float targetDB = 0F;
    float fadePerStep = 0.2F;   // .1 works for applets, 1 is okay for apps
    boolean fading = false;

    public void run() {
        FloatControl gainControl = (FloatControl) source.getControl(FloatControl.Type.MASTER_GAIN);
        fading = true;   // prevent running twice on same sound
        if (currDB > targetDB) {
            while (currDB > targetDB) {
                currDB -= fadePerStep;
                gainControl.setValue(currDB);
                try {
                    Thread.sleep(10);
                } catch (Exception ignored) {
                }
            }
        } else if (currDB < targetDB) {
            while (currDB < targetDB) {
                currDB += fadePerStep;
                gainControl.setValue(currDB);
                try {
                    Thread.sleep(10);
                } catch (Exception ignored) {
                }
            }
        }
        fading = false;
        currDB = targetDB;  // now sound is at this volume level
    }

    public void shiftVolumeTo(double value) {
        // value is between 0 and 1
        value = (value <= 0.0) ? 0.0001 : ((value > 1.0) ? 1.0 : value);
        targetDB = (float) (Math.log(value) / Math.log(10.0) * 20.0);
        if (!fading) {
            Thread t = new Thread(this);  // start a thread to fade volume
            t.start();  // calls run() below
        }
    }

    protected void setAudioFormat(AudioFormat fmt0) {
        this.fmt = fmt0;
    }

    protected AudioFormat getAudioFormat() {
        if (this.fmt == null) {
            Decoder decoder = this.getDecoder();
            this.fmt = new AudioFormat((float) decoder.getOutputFrequency(), 16, decoder.getOutputChannels(), true, false);
        }

        return this.fmt;
    }

    protected DataLine.Info getSourceLineInfo() {
        AudioFormat fmt = this.getAudioFormat();
        return new DataLine.Info(SourceDataLine.class, fmt);
    }

    public void open(AudioFormat fmt) throws JavaLayerException {
        if (!this.isOpen()) {
            this.setAudioFormat(fmt);
            this.openImpl();
            this.setOpen(true);
        }
    }

    protected void openImpl() throws JavaLayerException {
    }

    protected void createSource() throws JavaLayerException {
        Object t = null;

        try {
            Line ex = AudioSystem.getLine(this.getSourceLineInfo());
            if (ex instanceof SourceDataLine) {
                this.source = (SourceDataLine) ex;
                this.source.open(this.fmt);
                this.source.start();
            }
        } catch (RuntimeException re) {
            t = re;
        } catch (LinkageError le) {
            t = le;
        } catch (LineUnavailableException var3) {
            t = var3;
        }

        if (this.source == null) {
            throw new JavaLayerException("cannot obtain source audio line", (Throwable) t);
        }
    }

    protected void closeImpl() {
        if (this.source != null) {
            this.source.close();
        }

    }

    protected void writeImpl(short[] samples, int offs, int len) throws JavaLayerException {
        if (this.source == null) {
            this.createSource();
        }

        byte[] b = this.toByteArray(samples, offs, len);
        this.source.write(b, 0, len * 2);
    }

    protected byte[] getByteArray(int length) {
        if (this.byteBuf.length < length) {
            this.byteBuf = new byte[length + 1024];
        }

        return this.byteBuf;
    }

    protected byte[] toByteArray(short[] samples, int offs, int len) {
        byte[] b = this.getByteArray(len * 2);

        short s;
        for (int idx = 0; len-- > 0; b[idx++] = (byte) (s >>> 8)) {
            s = samples[offs++];
            b[idx++] = (byte) s;
        }

        return b;
    }

    protected void flushImpl() {
        if (this.source != null) {
            this.source.drain();
        }

    }

    public int getPosition() {
        int pos = 0;
        if (this.source != null) {
            pos = (int) (this.source.getMicrosecondPosition() / 1000L);
        }

        return pos;
    }

}
