package koji;

import com.intellij.openapi.project.Project;

public interface KojiListener {

    void projectOpened(Project project);

    void projectClosed(Project project);

    void projectSwitched(Project from, Project to);

    void windowFocused(KojiManager.Window window);


    void applicationExiting();
}
