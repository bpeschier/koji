package koji.pack;

import com.intellij.ide.util.PropertiesComponent;
import koji.KojiComponent;
import koji.pack.json.JsonPack;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PacksManager {

    private KojiComponent component;

    public PacksManager(KojiComponent component) {
        this.component = component;
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
}
