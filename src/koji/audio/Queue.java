package koji.audio;

import javazoom.jl.decoder.JavaLayerException;
import koji.pack.AudioFile;
import koji.pack.Theme;

public class Queue implements Player.Listener {

    private Player backgroundPlayer;
    private Player foregroundPlayer;
    private Player instantPlayer;
    private Player blockingPlayer;

    public Queue() {
        backgroundPlayer = new Player();
        foregroundPlayer = new Player();
        instantPlayer = new Player();
        blockingPlayer = new Player(true);
    }

    /**
     * Play audio file now:
     * - Fade out if playing
     * - Start file, checking repeatability
     *
     * @param player
     * @param audioFile
     */
    private void play(Player player, AudioFile audioFile) {

        try {
            player.play(audioFile.getInputStream(), 0, Float.MAX_VALUE);
        } catch (JavaLayerException e) {
            e.printStackTrace();
        }

//        checkQueue();
    }

    /**
     * @param player
     * @param audioFile
     */
    private void queue(Player player, AudioFile audioFile) {
        try {
            player.queue(audioFile.getInputStream(), 0, Float.MAX_VALUE);
        } catch (JavaLayerException e) {
            e.printStackTrace();
        }
//        checkQueue();
    }

    public void playTheme(Theme theme) {
        if (theme.getIntro() != null) {
            play(backgroundPlayer, theme.getIntro());
            queue(backgroundPlayer, theme.getMain());
        } else {
            play(backgroundPlayer, theme.getMain());
        }
    }

    public void playBackground(AudioFile audioFile) {
        play(backgroundPlayer, audioFile);
    }

    public void playForeground(AudioFile audioFile) {
        play(foregroundPlayer, audioFile);
    }

    public void playBlocking(AudioFile audioFile) {
        if (!audioFile.isRepeatable()) { // Repeating in a blocking player would be a bad idea™.
            try {
                backgroundPlayer.stop();
                foregroundPlayer.stop();
                blockingPlayer.stop();
                blockingPlayer.play(audioFile.getInputStream(), 0, Float.MAX_VALUE);
            } catch (JavaLayerException e) {
                e.printStackTrace();
            }
        } else {
            // TODO: log it
        }
    }


    @Override
    public void playbackStarted(Player player) {

    }

    @Override
    public void playbackFinished(Player player) {
        System.out.println("FINISHED");
        checkQueue();
    }

    private void checkQueue() {


    }
}
