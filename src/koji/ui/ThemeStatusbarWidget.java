package koji.ui;

import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.impl.SimpleDataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.openapi.wm.impl.status.EditorBasedWidget;
import com.intellij.ui.popup.PopupFactoryImpl;
import com.intellij.util.Consumer;
import com.intellij.util.messages.MessageBusConnection;
import koji.KojiManager;
import koji.actions.SelectThemeAction;
import koji.listeners.KojiChangeListener;
import koji.pack.Pack;
import koji.pack.Theme;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.MouseEvent;


public class ThemeStatusbarWidget extends EditorBasedWidget implements StatusBarWidget.MultipleTextValuesPresentation,
        StatusBarWidget.Multiframe, KojiChangeListener {

    KojiManager manager;
    DefaultActionGroup popupGroup;
    MessageBusConnection messageBusConnection;

    public ThemeStatusbarWidget(@NotNull Project project) {
        super(project);
        manager = KojiManager.getInstance();
        messageBusConnection = project.getMessageBus().connect();
        messageBusConnection.subscribe(KojiManager.CHANGES, this);
    }

    @Override
    public void dispose() {
        messageBusConnection.disconnect();
        super.dispose();
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
        Theme theme = manager.getCurrentProjectTheme(project);
        if (theme == null) {
            return null;
        }

        updatePopupGroup(project, theme);

        return new PopupFactoryImpl.ActionGroupPopup("Themes", popupGroup, SimpleDataContext.getProjectContext(project), false, false, false, true, null, -1,
                null, null);

    }

    private void updatePopupGroup(Project project, Theme theme) {

        popupGroup = new DefaultActionGroup(null, false);

        for (Theme t : manager.getProjectPack(project).getThemes()) {
            if (!t.getId().equals(theme.getId())) {
                popupGroup.add(new SelectThemeAction(t));
            }
        }

    }

    @Nullable
    @Override
    public String getSelectedValue() {
        String theme = manager.getCurrentProjectTheme(getProject()).getName();
        return (manager.isPaused()) ? theme + " (Paused)" : theme;
    }

    @NotNull
    @Override
    public String getMaxValue() {
        return "???";
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

    private void update() {
        updatePopupGroup(getProject(), manager.getCurrentProjectTheme(getProject()));
        myStatusBar.updateWidget(ID());
    }

    @Override
    public void themeChanged(Theme theme) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                update();
            }
        });
    }


    @NotNull
    private static String getWidgetID() {
        return ThemeStatusbarWidget.class.getName();
    }

    @Nullable
    public static ThemeStatusbarWidget findWidgetInstance(@Nullable Project project) {
        if (project != null) {
            StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);

            if (statusBar != null) {
                StatusBarWidget possibleWidget = statusBar.getWidget(getWidgetID());
                if (possibleWidget instanceof ThemeStatusbarWidget)
                    return (ThemeStatusbarWidget) possibleWidget;
            }
        }

        return null;
    }

    /**
     * Shows the action popup of this widget in the center of the provided frame. If there are no
     * actions available for this widget, the popup will not be shown.
     *
     * @param frame The frame that will be used for display
     */
    public void showPopupInCenterOf(@NotNull JFrame frame) {
        update();
        ListPopup popupStep = getPopupStep();
        if (popupStep != null)
            popupStep.showInCenterOf(frame);
    }

    @Override
    public void isKojiEnabled(boolean isEnabled) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                update();
            }
        });
    }

    @Override
    public void packChanged(Pack pack) {

    }
}
