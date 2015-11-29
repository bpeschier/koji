package koji.audio;

import javazoom.jl.decoder.Decoder;
import koji.pack.AudioFile;

import javax.sound.sampled.*;

public class Channel implements Player.Listener {

    public final static int FADE_DURATION = 1000;
    private final static int FADE_STEP_DURATION = 10;

    private String name;
    private SourceDataLine mainLine;
    private SourceDataLine bufferLine;

    private Player currentPlayer;


    public Channel(String name) {
        this.name = name;

        init();
    }

    private void init() {

        AudioFormat format = new AudioFormat((float) 44100, 16, 2, true, false);
        try {
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            mainLine = (SourceDataLine) AudioSystem.getLine(info);
            mainLine.open(format);
            mainLine.start();

            bufferLine = (SourceDataLine) AudioSystem.getLine(info);
            bufferLine.open(format);
            bufferLine.start();


        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public void stop() {
        if (currentPlayer != null) {
            currentPlayer.stop();
        }
    }

    public synchronized void play(AudioFile audioFile) {
        stop();

        currentPlayer = new Player(bufferLine, new RepeatableBitstream(audioFile, new Decoder()));
        SourceDataLine newBuffer = mainLine;
        mainLine = bufferLine;
        bufferLine = newBuffer;

        resetVolume();

        new Thread(new Runnable() {
            @Override
            public void run() {
                currentPlayer.play();
            }
        }).start();
    }

    private void fade(FloatControl control, double to) {
        synchronized (control) {
            int steps = FADE_DURATION / FADE_STEP_DURATION;

            to = (to <= 0.0) ? 0.0001 : ((to > 1.0) ? 1.0 : to);

            float currDB = control.getValue();
            float toDB = getDb(to);

            float diffStep = (toDB - currDB) / (float) steps;
            for (int i = 0; i < steps; i++) {
                currDB += diffStep;
                currDB = (Math.abs(currDB - toDB) < Math.abs(diffStep * 1.5)) ? toDB : currDB;
//                System.out.println("? " + currDB + ":" + toDB + ":" + diffStep + ":" + Math.abs(currDB - toDB));
                control.setValue(currDB);
                try {
                    Thread.sleep(FADE_STEP_DURATION);
                } catch (Exception ignored) {
                    ignored.printStackTrace();
                }
            }
        }
    }

    private float getDb(double value) {
        return (float) (Math.log(value) / Math.log(10.0) * 20.0);
    }

    private void resetVolume() {
        for (SourceDataLine line : new SourceDataLine[]{mainLine, bufferLine}) {
            FloatControl gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(getDb(1f));
        }
    }

    public void fadeOut() {
        System.out.println("FADEOUT " + name);
        for (SourceDataLine line : new SourceDataLine[]{mainLine, bufferLine}) {
            FloatControl gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    fade(gainControl, 0);
                }
            }).start();
        }
    }

    public void fadeIn() {
        System.out.println("FADEIN " + name);
        for (SourceDataLine line : new SourceDataLine[]{mainLine, bufferLine}) {
            FloatControl gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    fade(gainControl, 1);
                }
            }).start();
        }
    }


    @Override
    public void playbackStarted(Player player) {

    }

    @Override
    public void playbackFinished(Player player) {

    }

}
