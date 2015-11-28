package koji;

import com.intellij.openapi.project.Project;

public interface KojiListener {

    void projectOpened(Project project);

    void projectClosed(Project project);

    void windowFocused(KojiManager.Window window);

    void compilationDone(Project project, int errors, int warnings);

    void problemsAppeared(Project project);

    void problemsDisappeared(Project project);

    void applicationExiting();
}
