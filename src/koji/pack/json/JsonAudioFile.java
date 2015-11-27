package koji.pack.json;

import com.intellij.util.Range;
import koji.pack.AudioFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class JsonAudioFile implements AudioFile {

    @SuppressWarnings("unused")
    private URL path;
    @SuppressWarnings("unused")
    private Range<Float> repeat;
    @SuppressWarnings("unused")
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
        if (repeat == null && repeatable) {
            return new Range<Float>(0f, Float.MAX_VALUE);
        }
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
