package quasarusers.util.swing;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;

import com.sdm.quasar.modelview.implementation.swing.SwingObjectViewVisualizer;
import com.sdm.quasar.modelview.ObjectView;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.lang.Symbol;
import com.sdm.quasar.component.Command;
import com.sdm.quasar.view.implementation.swing.CommandTrigger;
import com.sdm.quasar.util.Debug;
import com.sdm.quasar.util.TraceLevel;
import com.sdm.util.ui.SwingXMLBuilder;
import org.wings.SComponent;

/**
 * Created by IntelliJ IDEA.
 * User: schmickl
 * Date: Feb 5, 2003
 * Time: 10:56:50 PM
 * To change this template use Options | File Templates.
 */
public class XMLSwingObjectViewVisualizer extends SwingObjectViewVisualizer {
  private SwingXMLBuilder builder;
  private Map nonvisual;

  public XMLSwingObjectViewVisualizer(Keywords arguments) {
    super(arguments);
  }

  public SwingXMLBuilder getBuilder() {
    if (builder == null) {
      InputStream     input   = ClassLoader.getSystemResourceAsStream("swing/view.xml");
      builder  = SwingXMLBuilder.create(input);
    }

    return builder;
  }

  public Object makeVisualRepresentation() {
    return getBuilder().getComponentByName("ObjectView");
  }

  public List buildMenus() {
    List menus = super.buildMenus();

    JMenuBar bar = (JMenuBar) getBuilder().getComponentByName("MenuBar");

    if (bar != null)
      for (int i=0; i<bar.getMenuCount(); i++) {
        menus.add(bar.getMenu(i));
        buildMenuCommandTriggers(bar.getMenu(i));
      }

    Iterator iterator = getView().getCommands().iterator();

    while (iterator.hasNext()) {
      Command command = (Command) iterator.next();
      JComponent component = getBuilder().getComponentByName(command.getName().toString()+"MenuCommand");

      if (component != null) {
        new CommandTrigger(command.getName(), true, false).associateElement(component);
      }
    }

    return menus;
  }

  private void buildMenuCommandTriggers(JMenu item) {

    for (int i=0; i<item.getMenuComponentCount(); i++) {
      Component component = item.getMenuComponent(i);
      String name = component.getName();

      if (name != null) {
        int index = name.lastIndexOf("MenuCommand");

        if (index > 0) {
          Symbol command = Symbol.forName(name.substring(0, index));

          new CommandTrigger(command, true, false).associateElement(component);

          if (Debug.isTraced("com.sdm.quasar.view", TraceLevel.MEDIUM)) {
              if (getView().hasCommand(command))
                  Debug.trace("com.sdm.quasar.view", "Mapped Menu to " + name);
              else
                  Debug.trace("com.sdm.quasar.view", "No command found for control " + name);
          }
        }
      }

      if (component instanceof JMenu)
        buildMenuCommandTriggers((JMenu)item.getMenuComponent(i));
    }
  }

      public List buildLocalControls() {
        List controls = super.buildLocalControls();

        Iterator userInterfaceElements = getUserInterfaceElements().iterator();

        while (userInterfaceElements.hasNext()) {
            JComponent component = (JComponent)userInterfaceElements.next();

            if (component == null) {
                if (Debug.isTraced("com.sdm.quasar.view", TraceLevel.MEDIUM))
                    Debug.trace("com.sdm.quasar.view", "A View has no UserInterfaceElement.");

                continue;
            }

            String name = component.getName();

            if (name == null)
                continue;

            int index = name.lastIndexOf("Command");

            if (index < 0)
                continue;

            Symbol command = Symbol.forName(name.substring(0, index));

            new CommandTrigger(command, true, false).associateElement(component);
            controls.add(component);

            if (Debug.isTraced("com.sdm.quasar.view", TraceLevel.MEDIUM)) {
                if (getView().hasCommand(command))
                    Debug.trace("com.sdm.quasar.view", "Mapped " + name);
                else
                    Debug.trace("com.sdm.quasar.view", "No command found for control " + name);
            }
        }

        return controls;
    }

  public static void main(String[] args) {
    InputStream     input   = ClassLoader.getSystemResourceAsStream("swing/view.xml");
    JComponent component = SwingXMLBuilder.create(input).getComponentByName("ObjectView");

    JFrame borderFrame= new JFrame();

    borderFrame.setSize(800, 600);
    borderFrame.getContentPane().add(component);
    borderFrame.pack();
    borderFrame.show();
  }
}
