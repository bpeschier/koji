package koji.pack.json;

import com.intellij.util.Range;
import koji.pack.AudioFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class JsonAudioFile implements AudioFile {

    private URL path;
    private Range<Float> repeat;
    private boolean repeatable;

    @Override
    public InputStream getInputStream() {
        try {
            return path.openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Range<Float> getRepeatRange() {
        return repeat;
    }

    @Override
    public boolean isRepeatable() {
        return repeatable;
    }

    @Override
    public String toString() {
        return "<" + new File(path.getFile()).getName() + " / " + repeatable + ">";
    }
}
