package koji.audio;

import javazoom.jl.decoder.JavaLayerException;
import koji.pack.AudioFile;

import java.util.LinkedList;

public class Queue implements Player.Listener {

    private final LinkedList<AudioFile> queue = new LinkedList<>();

    private Player currentPlayer;
    private Player nextPlayer;
    private AudioFile current;


    public Queue() {
    }

    public void queue(AudioFile file) {
        synchronized (queue) {
            queue.add(file);
        }
        if (queue.size() == 1 && currentPlayer == null) {
            try {
                next(false);
            } catch (JavaLayerException e) {
                e.printStackTrace();
            }
        }
    }

    public void play(AudioFile file) {
        synchronized (queue) {
            queue.add(file);
        }
        if (queue.size() == 1 && currentPlayer == null) {
            try {
                start();
            } catch (JavaLayerException e) {
                e.printStackTrace();
            }
        } else if (currentPlayer != null) {
            currentPlayer.fadeOut();
        }
    }

    private void start() throws JavaLayerException {
        next(false);
    }

    private void next(boolean continuing) throws JavaLayerException {
        AudioFile nextFile = current;

        System.out.println("Current: " + current + ", queue: " + queue);

        if (queue.size() > 0) {
            synchronized (queue) {
                nextFile = queue.pop();
            }
        } else if (nextFile != null && !nextFile.isRepeatable()) {
            current = null;
            currentPlayer = null;
            nextFile = null;
        }

        System.out.println("Next: " + nextFile + ", current: " + current + ", cont: " + continuing);

        if (nextFile != null) {
            boolean same = current == nextFile;
            current = nextFile;
            currentPlayer = new Player(current.getInputStream());
            currentPlayer.setListener(this);

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        currentPlayer.play();
                    } catch (JavaLayerException e) {
                        e.printStackTrace();
                    }
                }
            };
            if (continuing && same && current.isRepeatable()) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            currentPlayer.play(current.getRepeatRange().getFrom(), current.getRepeatRange().getTo());
                        } catch (JavaLayerException e) {
                            e.printStackTrace();
                        }
                    }
                };
            } else if (continuing && !same && current.isRepeatable()) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            currentPlayer.play(0, current.getRepeatRange().getTo());
                        } catch (JavaLayerException e) {
                            e.printStackTrace();
                        }
                    }
                };
            }
            new Thread(runnable).start();
        }

    }

    @Override
    public void playbackStarted(Player player) {

    }

    @Override
    public void playbackFinished(Player player) {
        try {
            next(true);
        } catch (JavaLayerException e) {
            e.printStackTrace();
        }
    }
}
