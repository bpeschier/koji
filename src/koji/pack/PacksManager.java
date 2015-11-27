package koji.pack;

import com.intellij.ide.util.PropertiesComponent;
import koji.KojiComponent;
import koji.pack.json.JsonPack;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PacksManager {

    private static PacksManager instance;

    private PacksManager() {
    }

    public static PacksManager getInstance() {
        if (instance == null) {
            instance = new PacksManager();
        }
        return instance;
    }

    public List<Pack> getPacks() {
        String[] paths = PropertiesComponent.getInstance().getValues("kojiPackPaths");
        List<Pack> packs = new ArrayList<Pack>(paths.length);
        for (String packLocation : paths) {
            try {
                packs.add(JsonPack.load(new URL(packLocation)));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        return packs;
    }

    public Pack getPack(String packId) {
        List<Pack> packs = getPacks();
        for (Pack p : packs) {
            if (p.getId().equals(packId)) {
                return p;
            }
        }
        // TODO: 0? Really?
        return packs.get(0);
    }
}
