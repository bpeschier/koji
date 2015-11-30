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
import koji.actions.SelectPackAction;
import koji.listeners.KojiChangeListener;
import koji.pack.Pack;
import koji.pack.Theme;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.MouseEvent;


public class PackStatusbarWidget extends EditorBasedWidget implements StatusBarWidget.MultipleTextValuesPresentation,
        StatusBarWidget.Multiframe, KojiChangeListener {

    KojiManager manager;
    DefaultActionGroup popupGroup;
    MessageBusConnection messageBusConnection;

    public PackStatusbarWidget(@NotNull Project project) {
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
        return PackStatusbarWidget.class.getName();
    }

    @Nullable
    @Override
    public WidgetPresentation getPresentation(@NotNull PlatformType platformType) {
        return this;
    }

    @Override
    public StatusBarWidget copy() {
        return new PackStatusbarWidget(getProject());
    }

    @Nullable
    @Override
    public ListPopup getPopupStep() {
        Project project = getProject();
        if (project == null) {
            return null;
        }
        Pack pack = manager.getProjectPack(project);
        if (pack == null) {
            return null;
        }

        updatePopupGroup(project, pack);

        return new PopupFactoryImpl.ActionGroupPopup("Packs", popupGroup, SimpleDataContext.getProjectContext(project), false, false, false, true, null, -1,
                null, null);

    }

    private void updatePopupGroup(Project project, Pack pack) {

        popupGroup = new DefaultActionGroup(null, false);

        for (Pack p : manager.getPacks()) {
            if (!p.getId().equals(pack.getId())) {
                popupGroup.add(new SelectPackAction(p));
            }
        }

    }

    @Nullable
    @Override
    public String getSelectedValue() {
        return manager.getProjectPack(getProject()).getName();
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
        updatePopupGroup(getProject(), manager.getProjectPack(getProject()));
        myStatusBar.updateWidget(ID());
    }

    @Override
    public void isKojiEnabled(boolean isEnabled) {

    }

    @Override
    public void packChanged(Pack pack) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                update();
            }
        });
    }

    @Override
    public void themeChanged(Theme theme) {

    }


    @NotNull
    private static String getWidgetID() {
        return PackStatusbarWidget.class.getName();
    }

    @Nullable
    public static PackStatusbarWidget findWidgetInstance(@Nullable Project project) {
        if (project != null) {
            StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);

            if (statusBar != null) {
                StatusBarWidget possibleWidget = statusBar.getWidget(getWidgetID());
                if (possibleWidget instanceof PackStatusbarWidget)
                    return (PackStatusbarWidget) possibleWidget;
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

}
