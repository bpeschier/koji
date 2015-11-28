package koji;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

public interface KojiListener {

    void projectOpened(Project project);

    void projectClosed(Project project);

    void fileOpened(Project project, VirtualFile file);

    void fileClosed(Project project, VirtualFile file);

    void currentFileChanged(Project project, VirtualFile newFile, VirtualFile oldFile);

    void windowFocused(KojiManager.Window window);

    void compilationDone(Project project, int errors, int warnings);

    void problemsAppeared(Project project, VirtualFile file);

    void problemsDisappeared(Project project, VirtualFile file);

    void applicationExiting();
}
