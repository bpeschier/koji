package koji.pack;

import com.intellij.util.Range;

import java.io.InputStream;

public interface AudioFile {
    InputStream getInputStream();

    Range<Float> getRepeatRange();

    boolean isRepeatable();
}
