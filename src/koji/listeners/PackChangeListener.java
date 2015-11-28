package koji.listeners;

import koji.pack.Pack;

public interface PackChangeListener {
    void packChanged(Pack pack);
}
