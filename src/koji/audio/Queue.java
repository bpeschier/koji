package koji.audio;

import javazoom.jl.decoder.JavaLayerException;
import koji.pack.AudioFile;

import java.util.LinkedList;

public class Queue implements Player.Listener {

    private final LinkedList<AudioFile> queue = new LinkedList<>();

    private Player player;
    private AudioFile current;


    public Queue() {
    }

    public void queue(AudioFile file) {
        synchronized (queue) {
            queue.add(file);
        }
        if (queue.size() == 1 && player == null) {
            System.out.println("QUEUE");
            next(false);
        }
    }

    public void play(AudioFile file) {
        synchronized (queue) {
            queue.add(file);
        }
        if (queue.size() == 1 && player == null) {
            System.out.println("PLAY");
            start();
        } else if (player != null) {
            System.out.println("FADE");
            player.fadeOut();
        }
    }

    private void start() {
        next(false);
    }

    private void next(boolean continuing) {
        AudioFile nextFile = current;

        System.out.println("Current: " + current + ", queue: " + queue);

        if (queue.size() > 0) {
            synchronized (queue) {
                nextFile = queue.pop();
            }
        } else if (nextFile != null && !nextFile.isRepeatable()) {
            current = null;
            player = null;
            nextFile = null;
        }

        System.out.println("Next: " + nextFile + ", current: " + current + ", cont: " + continuing);

        if (nextFile != null) {
            boolean same = current == nextFile;
            current = nextFile;
            try {
                player = new Player(current.getInputStream());
            } catch (JavaLayerException e) {
                e.printStackTrace();
            }
            player.setListener(Queue.this);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        if (continuing && same && current.isRepeatable() && current.getRepeatRange() != null) {
                            player.skipTo(current.getRepeatRange().getFrom());
                            player.play(current.getRepeatRange().getTo() - current.getRepeatRange().getFrom());
                        } else if (!same && current.isRepeatable() && current.getRepeatRange() != null) {
                            player.play(current.getRepeatRange().getTo());
                        } else {
                            player.play();
                        }
                    } catch (JavaLayerException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

    }

    @Override
    public void playbackStarted(Player player) {

    }

    @Override
    public void playbackFinished(Player player) {
        System.out.println("FINISHED");
        next(true);
    }
}
