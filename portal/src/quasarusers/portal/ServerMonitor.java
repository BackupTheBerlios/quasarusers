package quasarusers.portal;

import com.sdm.quasar.component.Command;
import com.sdm.quasar.component.Component;
import com.sdm.quasar.modelview.ObjectView;
import com.sdm.quasar.modelview.model.FieldModel;
import com.sdm.quasar.modelview.proxy.ObjectProxy;
import com.sdm.quasar.runtime.Loadable;
import com.sdm.quasar.session.Session;
import com.sdm.quasar.session.implementation.LocalSessionManager;
import com.sdm.quasar.view.View;
import com.sdm.quasar.view.ViewContainer;
import quasarusers.portal.menue.MenueView;
import quasarusers.portal.menue.MenueVisualizer;
import org.wings.SButton;
import org.wings.SComponent;
import org.wings.SLabel;
import org.wings.STextComponent;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Dies ist eine Klasse für Entwicklertests. Sie stellt Informationen
 * zu den offenen Views dar.
 *
 * Der ServerMonitor wird durcheinen Eintrag in der Datei
 * <code>configuration.xml</code> aktiviert.
 *
 * @author Marco Schmickler
 */

//todo dpk 28.01.03 -> MC kommentieren

public class ServerMonitor implements Loadable {
  private JFrame statusFrame;
  private JPanel statusPanel;
  private JPanel detailPanel;
  private JComponent currentPanel;

  public static class MonitorNode extends DefaultMutableTreeNode {
    private Object object;
    private JComponent panel;

    public MonitorNode(String label, Object object, JComponent panel) {
      super(label);

      this.object = object;
      this.panel = panel;
    }

    public Object getObject() {
      return object;
    }

    public JComponent getPanel() {
      return panel;
    }

    public String getLabel() {
      return (String) getUserObject();
    }
  }

  public static class ViewMonitor extends JPanel {
    public ViewMonitor(View view) {
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

      Object[] objects = view.getCommands().toArray();

      add(new JLabel(view.toString()));

      add(new JLabel("isLocked " + view.isLocked() + " " + view.getLockTransaction()));
      add(new JLabel("View Commands"));
      addCommands(objects);

      if (view.getViewServer() != null) {
        objects = view.getViewServer().getCommands().toArray();

        add(new JLabel("ViewServer Commands"));
        addCommands(objects);
      }

      if (view.getViewVisualizer() != null) {
        objects = view.getViewVisualizer().getUserInterfaceElements().toArray();

        add(new JLabel("Userinterface Elements"));
        addWidgets(objects);
      }

      if (view instanceof ObjectView) {
        objects = view.getViewVisualizer().getUserInterfaceElements().toArray();

        add(new JLabel("Attribute Values"));
        addFields((ObjectView) view);
      }
    }

    private void addCommands(Object[] objects) {
      String[] strings = new String[objects.length];

      for (int i = 0; i < objects.length; i++) {
        Command command = (Command) objects[i];
        strings[i] = command.getName() + (command.isEnabled() ? " (enabled)" : " (disabled)");
      }

      JScrollPane scrollPane = new JScrollPane();
      scrollPane.getViewport().add(new JList(strings));

      add(scrollPane);
    }

    private void addFields(ObjectView view) {
      FieldModel[] fieldModels = view.getViewModel().getFieldModels();

      String[] strings = new String[fieldModels.length];
      ObjectProxy proxy = (ObjectProxy) view.getObject(true);

      for (int i = 0; i < fieldModels.length; i++) {
        if (proxy == null)
          strings[i] = fieldModels[i].getName() + ": <null>";
        else
          strings[i] = fieldModels[i].getName() + ": " + fieldModels[i].getValue(proxy);
      }

      JScrollPane scrollPane = new JScrollPane();
      scrollPane.getViewport().add(new JList(strings));

      add(scrollPane);
    }

    private void addWidgets(Object[] objects) {
      String[] strings = new String[objects.length];

      for (int i = 0; i < objects.length; i++) {
        SComponent component = (SComponent) objects[i];
        strings[i] = component.getClass().getName() + " " + component.getName() + ((component instanceof STextComponent) ? " Text: " + ((STextComponent) component).getText() : "")
                + ((component instanceof SLabel) ? " Label: " + ((SLabel) component).getText() : "")
                + ((component instanceof SButton) ? " Button: " + ((SButton) component).getText() : "");
      }

      JScrollPane scrollPane = new JScrollPane();
      scrollPane.getViewport().add(new JList(strings));

      add(scrollPane);
    }
  }

  public void load(String arguments) throws Exception {
    statusFrame = new JFrame();
    statusPanel = new JPanel();
    statusFrame.getContentPane().add(statusPanel);
    statusFrame.setSize(800, 600);

    final JTree tree = new JTree();

    tree.setRootVisible(false);

    tree.addTreeSelectionListener(new TreeSelectionListener() {
      public void valueChanged(TreeSelectionEvent e) {
        TreePath path = e.getPath();
        Object[] components = path.getPath();
        MonitorNode selectedNode = (MonitorNode) components[components.length - 1];

        detailPanel.removeAll();

        if (selectedNode.getPanel() != null) {
          currentPanel = selectedNode.getPanel();
          detailPanel.add(currentPanel, BorderLayout.CENTER);
        }

        detailPanel.repaint();
        statusPanel.revalidate();
      }
    });

    statusPanel.setLayout(new BorderLayout());
    JButton refresh = new JButton("refresh");

    refresh.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
          buildRootNode(root);

          tree.setModel(new DefaultTreeModel(root));
        } catch (Exception e1) {
          e1.printStackTrace();
        }
      }
    });

    statusPanel.add(refresh, BorderLayout.NORTH);
    statusPanel.add(tree, BorderLayout.WEST);

    statusPanel.add(detailPanel = new JPanel(), BorderLayout.CENTER);

    detailPanel.setLayout(new BorderLayout());

    statusFrame.show();
  }

  public void buildRootNode(DefaultMutableTreeNode node) throws Exception {
    com.sdm.quasar.session.Session[] sessions = LocalSessionManager.getSessionManager().getSessions();

    for (int i = 0; i < sessions.length; i++) {
      com.sdm.quasar.session.Session session = sessions[i];
      SessionBenutzer benutzer = ((SessionBenutzer) session.getLocal("siaUser", null));

      if (benutzer != null) {
        MonitorNode newChild = new MonitorNode(benutzer.getKennung(), session, new JLabel("Session"));
        node.add(newChild);

        buildSessionNode(newChild);
      }
    }
  }

  public void buildViewNode(MonitorNode node) throws Exception {
    View view = (View) node.getObject();

    Component[] childComponents = view.getChildComponents();

    for (int i = 0; i < childComponents.length; i++) {
      Component childComponent = childComponents[i];

      MonitorNode newChild = new MonitorNode(childComponent.getName(), childComponent, new ViewMonitor((View) childComponent));
      node.add(newChild);

      buildViewNode(newChild);
    }
  }

  public void buildSessionNode(MonitorNode node) throws Exception {
    Session session = (Session) node.getObject();

    MenueView menueView = (MenueView) session.getLocal("MenueView", null);
    ViewContainer container = ((MenueVisualizer) menueView.getViewVisualizer()).getPortalContainer();
    Object[] openViews = container.getComponents().toArray();

    for (int i = 0; i < openViews.length; i++) {
      View view = (View) openViews[i];

      MonitorNode newChild = new MonitorNode(view.getName(), view, new ViewMonitor(view));
      node.add(newChild);

      buildViewNode(newChild);
    }
  }

  public static void main(String[] args) throws Exception {
    new ServerMonitor().load("");
  }
}
