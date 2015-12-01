package koji;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.problems.WolfTheProblemSolver;
import koji.ui.PackStatusbarWidget;
import koji.ui.ThemeStatusbarWidget;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class KojiProjectComponent extends WolfTheProblemSolver.ProblemListener implements ProjectComponent, FileEditorManagerListener {

    private Project project;
    private KojiManager manager;
    private WolfTheProblemSolver problemSolver;

    public KojiProjectComponent(Project project) {
        this.project = project;
        manager = KojiManager.getInstance();
    }

    @Override
    public void projectOpened() {

        StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);

        if (statusBar != null) {
            if (manager.getPacks().size() > 1) {
                PackStatusbarWidget packStatusbarWidget = new PackStatusbarWidget(project);
                statusBar.addWidget(packStatusbarWidget, "after " + PackStatusbarWidget.class.getName(), project);
                statusBar.updateWidget(packStatusbarWidget.getClass().getName());
            }

            ThemeStatusbarWidget themeStatusbarWidget = new ThemeStatusbarWidget(project);
            statusBar.addWidget(themeStatusbarWidget, "after " + ThemeStatusbarWidget.class.getName(), project);
            statusBar.updateWidget(themeStatusbarWidget.getClass().getName());
        }

        manager.projectOpened(project);

        problemSolver = WolfTheProblemSolver.getInstance(project);
        problemSolver.addProblemListener(this);

        project.getMessageBus().connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, this);


        JFrame frame = WindowManager.getInstance().getFrame(project);
        frame.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                manager.projectOpened(project);
            }
        });
    }

    @Override
    public void projectClosed() {
        manager.projectClosed(project);

        problemSolver.removeProblemListener(this);
        problemSolver = null;
    }

    @Override
    public void initComponent() {
    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return getClass().getName();
    }

    @Override
    public void problemsAppeared(@NotNull VirtualFile file) {
        manager.problemsAppeared(project, file);
    }

    @Override
    public void problemsDisappeared(@NotNull VirtualFile file) {
        manager.problemsDisappeared(project, file);
    }

    @Override
    public void fileOpened(@NotNull FileEditorManager fileEditorManager, @NotNull VirtualFile virtualFile) {
        manager.fileOpened(fileEditorManager.getProject(), virtualFile);
    }

    @Override
    public void fileClosed(@NotNull FileEditorManager fileEditorManager, @NotNull VirtualFile virtualFile) {
        manager.fileClosed(fileEditorManager.getProject(), virtualFile);
    }

    @Override
    public void selectionChanged(@NotNull FileEditorManagerEvent event) {
        manager.currentFileChanged(event.getManager().getProject(), event.getNewFile(), event.getOldFile());
    }
}
