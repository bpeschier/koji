package koji.audio;

import com.intellij.util.Range;
import javazoom.jl.decoder.JavaLayerException;
import koji.pack.AudioFile;
import koji.pack.Theme;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

public class Queue implements Player.Listener {

    private static final Logger logger = Logger.getLogger(Queue.class.getName());

    private Player backgroundPlayer;
    private Player foregroundPlayer;
    private Player instantPlayer;
    private Player blockingPlayer;

    private final Map<Player, LinkedList<AudioFile>> playerQueues = new HashMap<Player, LinkedList<AudioFile>>();

    public Queue() {
        backgroundPlayer = new Player("background");
        foregroundPlayer = new Player("foreground");
        instantPlayer = new Player("instant");
        blockingPlayer = new Player("blocking", true);

        backgroundPlayer.setListener(this);
        foregroundPlayer.setListener(this);

        playerQueues.put(backgroundPlayer, new LinkedList<AudioFile>());
        playerQueues.put(foregroundPlayer, new LinkedList<AudioFile>());
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
        // Manage queue
        LinkedList<AudioFile> queue = playerQueues.get(player);
        synchronized (playerQueues) {
            queue.clear();
            queue.add(audioFile);
        }

        try {
            if (audioFile.isRepeatable()) {
                logger.fine("Queueing direct playback of repeatable audio");
                player.play(audioFile.getInputStream(), 0, audioFile.getRepeatRange().getTo());
            } else {
                logger.fine("Queueing direct playback of non-repeatable audio");
                player.play(audioFile.getInputStream(), 0, Float.MAX_VALUE);
            }
        } catch (JavaLayerException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param player
     * @param audioFile
     */
    private void queue(Player player, AudioFile audioFile) {
        queue(player, audioFile, false);
    }

    private void queue(Player player, AudioFile audioFile, boolean repeat) {
        // Manage queue
        LinkedList<AudioFile> queue = playerQueues.get(player);
        synchronized (playerQueues) {
            queue.add(audioFile);
        }

        try {
            if (!audioFile.isRepeatable()) {
                logger.fine("Queueing playback of non-repeatable audio");
                player.queue(audioFile.getInputStream(), 0, Float.MAX_VALUE);
            } else if (repeat && audioFile.isRepeatable()) {
                logger.fine("Queueing playback of repeating audio (start)");
                Range<Float> range = audioFile.getRepeatRange();
                player.queue(audioFile.getInputStream(), range.getFrom(), range.getTo());
            } else if (audioFile.isRepeatable()) {
                logger.fine("Queueing playback of repeating audio (repeat)");
                player.queue(audioFile.getInputStream(), 0, audioFile.getRepeatRange().getTo());
            }
        } catch (JavaLayerException e) {
            e.printStackTrace();
        }
    }

    public void playTheme(Theme theme) {
        stop(backgroundPlayer);
        if (theme.getIntro() != null) {
            play(backgroundPlayer, theme.getIntro());
            queue(backgroundPlayer, theme.getMain());
        } else {
            play(backgroundPlayer, theme.getMain());
        }
    }

    public void playBackground(AudioFile audioFile) {
        if (playerQueues.get(backgroundPlayer).peek() != audioFile) {
            stop(backgroundPlayer);
            play(backgroundPlayer, audioFile);
        }
    }

    public void playForeground(AudioFile audioFile) {
        if (playerQueues.get(foregroundPlayer).peek() != audioFile) {
            foregroundPlayer.fadeIn();
            backgroundPlayer.mute();
            play(foregroundPlayer, audioFile);
        }
    }

    public void playInstant(AudioFile audioFile) {
        try {
            instantPlayer.play(audioFile.getInputStream(), 0, Float.MAX_VALUE);
        } catch (JavaLayerException e) {
            e.printStackTrace();
        }
    }

    public void pauseBackground() {
        backgroundPlayer.mute();
    }

    public void resumeBackground() {
        backgroundPlayer.fadeIn();
    }

    public void stopForeground() {
        LinkedList<AudioFile> queue = playerQueues.get(foregroundPlayer);
        if (queue != null) {
            synchronized (playerQueues) {
                queue.clear();
            }
        }
        foregroundPlayer.fadeOut();
    }

    private void stop(Player player) {
        LinkedList<AudioFile> queue = playerQueues.get(player);
        if (queue != null) {
            synchronized (playerQueues) {
                queue.clear();
            }
        }
        player.fadeOut();
    }

    public void playBlocking(AudioFile audioFile) {
        stop(backgroundPlayer);
        stop(foregroundPlayer);
        stop(instantPlayer);
        try {
            blockingPlayer.play(audioFile.getInputStream(), 0, Float.MAX_VALUE);
        } catch (JavaLayerException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        stop(backgroundPlayer);
        stop(foregroundPlayer);
        stop(instantPlayer);
    }


    @Override
    public void playbackStarted(Player player) {

    }

    @Override
    public void playbackFinished(Player player) {
        checkQueue(player);
    }

    @Override
    public void queueDone(Player player) {
        if (player == foregroundPlayer) {
            backgroundPlayer.fadeIn();
        }
    }

    /**
     * Check if we need to repeat stuff
     */
    private void checkQueue(Player player) {
        LinkedList<AudioFile> queue = playerQueues.get(player);

        AudioFile audioFile;

        synchronized (playerQueues) {
            try {
                audioFile = queue.getLast();
            } catch (NoSuchElementException nsee) {
                audioFile = null;
            }
        }

        if (audioFile != null && audioFile.isRepeatable()) {
            queue(player, audioFile, true);
        }
    }
}
