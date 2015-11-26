package koji.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.impl.status.EditorBasedWidget;
import com.intellij.util.Consumer;
import koji.pack.Theme;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.event.MouseEvent;

public class ThemeStatusbarWidget extends EditorBasedWidget implements StatusBarWidget.MultipleTextValuesPresentation,
        StatusBarWidget.Multiframe {

    protected ThemeStatusbarWidget(@NotNull Project project) {
        super(project);
    }

    @NotNull
    @Override
    public String ID() {
        return ThemeStatusbarWidget.class.getName();
    }

    @Nullable
    @Override
    public WidgetPresentation getPresentation(@NotNull PlatformType platformType) {
        return this;
    }

    @Override
    public StatusBarWidget copy() {
        return new ThemeStatusbarWidget(getProject());
    }

    @Nullable
    @Override
    public ListPopup getPopupStep() {
        Project project = getProject();
        if (project == null) {
            return null;
        }
        Theme theme = null;
        if (theme == null) {
            return null;
        }
        return ThemePopup.getInstance(project, theme).asListPopup();
    }

    @Nullable
    @Override
    public String getSelectedValue() {
        return null;
    }

    @NotNull
    @Override
    public String getMaxValue() {
        return null;
    }

    @Nullable
    @Override
    public String getTooltipText() {
        return null;
    }

    @Nullable
    @Override
    public Consumer<MouseEvent> getClickConsumer() {
        return new Consumer<MouseEvent>() {
            public void consume(MouseEvent mouseEvent) {
                // TODO:
            }
        };
    }
}
