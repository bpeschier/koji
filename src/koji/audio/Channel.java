package koji.audio;

import koji.pack.AudioFile;

import javax.sound.sampled.*;

public class Channel implements Player.Listener {

    public final static int FADE_DURATION = 1000;
    private final static int FADE_STEP_DURATION = 10;

    private String name;
    private SourceDataLine mainLine;
    private SourceDataLine bufferLine;
    private boolean isMuted;

    private Player currentPlayer;
    private AudioFile currentAudioFile;


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
        currentAudioFile = null;
        if (currentPlayer != null) {
            doFade(mainLine, 0);
            currentPlayer.stopAfter(FADE_DURATION);
            currentPlayer = null;
        }
    }

    public synchronized void play(AudioFile audioFile) {
        if (audioFile.equals(currentAudioFile)) {
            return;
        }

        stop();
        currentAudioFile = audioFile;

        currentPlayer = new Player(bufferLine, new RepeatableBitstream(audioFile));
        SourceDataLine newBuffer = mainLine;
        mainLine = bufferLine;
        bufferLine = newBuffer;

        setVolume(1f);
        setMuted(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                currentPlayer.play();
            }
        }).start();
    }

    private float getDb(double value) {
        return (float) (Math.log(value) / Math.log(10.0) * 20.0);
    }

    private void setVolume(double value) {
        for (SourceDataLine line : new SourceDataLine[]{mainLine, bufferLine}) {
            FloatControl gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(getDb(value));
        }
    }

    public void setMuted(boolean muted) {
        if (muted && !isMuted) {
            isMuted = true;
            doFade(mainLine, 0);
        } else if (!muted && isMuted) {
            isMuted = false;
            doFade(mainLine, 1);
        }
    }

    public void fadeIn() {
        setMuted(false);
    }

    public void fadeOut() {
        setMuted(true);
    }

    private void fade(FloatControl control, double to) {
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (control) {
            int steps = FADE_DURATION / FADE_STEP_DURATION;

            to = (to <= 0.0) ? 0.0001 : ((to > 1.0) ? 1.0 : to);

            float currDB = control.getValue();
            float toDB = getDb(to);

            float diffStep = (toDB - currDB) / (float) steps;
            for (int i = 0; i < steps; i++) {
                currDB += diffStep;
                currDB = (Math.abs(currDB - toDB) < Math.abs(diffStep * 1.5)) ? toDB : currDB;
                control.setValue(currDB);
                try {
                    Thread.sleep(FADE_STEP_DURATION);
                } catch (Exception ignored) {
                    ignored.printStackTrace();
                }
            }
        }
    }

    public void doFade(SourceDataLine line, final double to) {
        final FloatControl gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
        new Thread(new Runnable() {
            @Override
            public void run() {
                fade(gainControl, to);
            }
        }).start();
    }

    @Override
    public void playbackStarted(Player player) {

    }

    @Override
    public void playbackFinished(Player player) {

    }

}
