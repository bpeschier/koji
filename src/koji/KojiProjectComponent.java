package koji;

import com.intellij.openapi.compiler.CompilationStatusAdapter;
import com.intellij.openapi.compiler.CompilationStatusListener;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.problems.WolfTheProblemSolver;
import koji.ui.ThemeStatusbarWidget;
import org.jetbrains.annotations.NotNull;

public class KojiProjectComponent extends WolfTheProblemSolver.ProblemListener implements ProjectComponent, CompilationStatusListener {

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
            ThemeStatusbarWidget widget = new ThemeStatusbarWidget(project);
            statusBar.addWidget(widget, "after " + ThemeStatusbarWidget.class.getName(), project);
            statusBar.updateWidget(widget.getClass().getName());
        }

        manager.projectOpened(project);

        CompilerManager.getInstance(project).addCompilationStatusListener(this);

        problemSolver = WolfTheProblemSolver.getInstance(project);
        problemSolver.addProblemListener(this);
    }

    @Override
    public void projectClosed() {
        manager.projectClosed(project);

        CompilerManager.getInstance(project).removeCompilationStatusListener(this);
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
        manager.problemsAppeared(project);
    }

    @Override
    public void problemsDisappeared(@NotNull VirtualFile file) {
        boolean errors = false;
        for (Module m : ModuleManager.getInstance(project).getModules()) {
            errors = errors || problemSolver.hasProblemFilesBeneath(m);
        }
        if (!errors) {
            manager.problemsDisappeared(project);
        }
    }

    @Override
    public void compilationFinished(boolean aborted, int errors, int warnings, CompileContext compileContext) {
        manager.compilationDone(compileContext.getProject(), errors, warnings);
    }

    @Override
    public void fileGenerated(String s, String s1) {

    }
}
