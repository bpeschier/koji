package koji.listeners;

import koji.pack.Pack;
import koji.pack.Theme;

public interface KojiChangeListener {
    void isKojiEnabled(boolean isEnabled);

    void packChanged(Pack pack);

    void themeChanged(Theme theme);
}
