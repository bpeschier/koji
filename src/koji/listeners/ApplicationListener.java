package koji.listeners;

import com.intellij.ide.plugins.PluginTable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.ex.AnActionListener;
import com.intellij.openapi.application.ApplicationAdapter;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.wm.impl.IdeFrameImpl;
import com.intellij.openapi.wm.impl.welcomeScreen.FlatWelcomeFrame;
import koji.KojiListener;
import koji.KojiManager;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ApplicationListener {
    private static ApplicationListener instance;

    private KojiListener listener;

    private AnActionListener.Adapter anActionAdapter;
    private ApplicationAdapter applicationAdapter;
    private PropertyChangeListener focusListener;

    public static void uninstall() {
        if (instance != null) {
            instance.unregister();
        }
    }

    public static void install(KojiListener listener) {
        if (instance == null) {
            instance = new ApplicationListener(listener);
        }
    }


    private ApplicationListener(KojiListener listener) {
        this.listener = listener;
        setup();
        register();

    }

    private void setup() {
        final ActionManager actionManager = ActionManager.getInstance();
        anActionAdapter = new AnActionListener.Adapter() {
            @Override
            public void beforeActionPerformed(AnAction action, DataContext dataContext, AnActionEvent event) {
                super.beforeActionPerformed(action, dataContext, event);
                System.out.println(action.toString());
                //noinspection unused
                String actionString = actionManager.getId(action);
            }
        };

        applicationAdapter = new ApplicationAdapter() {
            @Override
            public void applicationExiting() {
                listener.applicationExiting();
            }
        };

        //noinspection Convert2Lambda
        focusListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                if (("focusOwner".equals(e.getPropertyName())) && e.getNewValue() != null) {
                    Component comp = (Component) e.getNewValue();
//                    System.out.println("Comp " + comp);
                    if (comp instanceof PluginTable) {
                        listener.windowFocused(KojiManager.Window.PLUGINS);
                        return;
                    }
                    Container parent = comp.getParent();
                    while (parent != null && parent.getParent() != null) {
                        parent = parent.getParent();
                        if (parent.getClass().getName().equals("com.intellij.openapi.options.newEditor.SettingsEditor")) {
                            listener.windowFocused(KojiManager.Window.SETTINGS);
                            return;
                        }
                    }
//                    System.out.println("Parent " + parent);

                    if (parent instanceof FlatWelcomeFrame) {
                        listener.windowFocused(KojiManager.Window.PROJECT_SELECT);
                    } else if (parent instanceof IdeFrameImpl) {
                        listener.windowFocused(KojiManager.Window.EDITOR);
                    }
                }
            }
        };
    }

    private void unregister() {
        ActionManager.getInstance().removeAnActionListener(anActionAdapter);
        ApplicationManager.getApplication().removeApplicationListener(applicationAdapter);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener(focusListener);
    }

    private void register() {
        ActionManager.getInstance().addAnActionListener(anActionAdapter);
        ApplicationManager.getApplication().addApplicationListener(applicationAdapter);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener(focusListener);
    }
}
