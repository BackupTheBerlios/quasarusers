package quasarusers.portal.menue;

import com.sdm.quasar.component.ComponentException;
import com.sdm.quasar.component.NoSuchCommandException;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.view.AbstractView;
import com.sdm.quasar.view.CommandActivator;
import com.sdm.quasar.view.View;
import com.sdm.quasar.view.ViewContainer;
import com.sdm.quasar.view.ViewContainerAdapter;
import com.sdm.quasar.view.ViewContainerEvent;
import com.sdm.quasar.view.ViewManager;
import com.sdm.quasar.view.implementation.wings.CommandTrigger;
import com.sdm.quasar.view.implementation.wings.StackingContainer;
import com.sdm.quasar.view.quickstart.PortalView;
import com.sdm.quasar.view.quickstart.wings.WingsPortalViewVisualizer;
import org.wings.SButton;
import org.wings.SLabel;
import org.wings.STree;
import org.wings.session.PropertyService;
import org.wings.session.SessionManager;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.LinkedList;

/**
 * Dies ist der Visualizer für das Menü.
 *
 * @author Marco Schmickler
 */
public final class MenueVisualizer extends WingsPortalViewVisualizer {
  private SLabel viewLabel;

  public MenueVisualizer(Keywords arguments) {
    super(arguments);
  }

  protected void initialize(Keywords arguments) {
    arguments.addValue("portalTemplateName", "de/sdm/sia/remis/portal/portal.html");

    super.initialize(arguments);
  }

  protected void doStartup(Keywords arguments) {
    super.doStartup(arguments);

    getPortalContainer().addViewContainerListener(new ViewContainerAdapter() {
      public void viewActivated(ViewContainerEvent event) {
        showNodeStack(((PortalView) getView()).getNodeStack(event.getView()));
      }

      public void viewDeactivated(ViewContainerEvent event) {
        viewLabel.setText("");
      }
    });
  }

  public Object makeVisualRepresentation() {
    PortalViewPanel panel = (PortalViewPanel) super.makeVisualRepresentation();

    SButton button = new SButton("anmelden");
    button.setName(MenueView.C_ANMELDEN + "Command");
    button.setShowAsFormComponent(false);
    panel.add(button, MenueView.C_ANMELDEN + "Command");

//    button = new SButton("abmelden");
//    button.setName(MenueView.C_ABMELDEN + "Command");
//    button.setShowAsFormComponent(false);
//    panel.add(button, MenueView.C_ABMELDEN + "Command");

    button = new SButton("Passwort ändern");
    button.setName(MenueView.C_AENDERN_PASSWORT + "Command");
    button.setShowAsFormComponent(false);
    panel.add(button, MenueView.C_AENDERN_PASSWORT + "Command");

    viewLabel = new SLabel();
    panel.add(viewLabel, "views");

    try {
      button = new SButton("<");
      CommandActivator activator = new CommandTrigger(StackingContainer.ACTIVATE_PREVIOUS_VIEW);
      activator.associateElement(button);
      getPortalContainer().addCommandActivator(activator);
      panel.add(button, "backward");

      button = new SButton(">");
      activator = new CommandTrigger(StackingContainer.ACTIVATE_NEXT_VIEW);
      activator.associateElement(button);
      getPortalContainer().addCommandActivator(activator);
      panel.add(button, "forward");
    } catch (NoSuchCommandException e) {
      e.printStackTrace();
    }

    ((PropertyService) SessionManager.getSession()).setProperty("container", getPortalContainer());

    ViewManager.ContainerGenerator containerGenerator = new ViewManager.ContainerGenerator() {
      public ViewContainer makeContainer(Keywords arguments) throws ComponentException {
        return (ViewContainer) ((PropertyService) SessionManager.getSession()).getProperty("container");
      }
    };

    ViewManager.getViewManager().setDefaultContainer(AbstractView.class, new Keywords(), containerGenerator);

    // --------------------------------------------------------------------
//    final STree tree = panel.getPortalTree();
//    TreeSelectionListener listener =
//            new TreeSelectionListener() {
//              public void valueChanged(TreeSelectionEvent event) {
//                TreePath path = event.getPath();
//                Object[] components = path.getPath();
//                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) components[components.length - 1];
//                TransferPortalNode node = (TransferPortalNode) selectedNode.getUserObject();
//
//                if (components.length == 4) {
//                  if (node == null)
//                    return;
//
//                  if (node.isActive())
//                    performOpenView(node, false);
//                }
//              }
//            };
//
//    tree.addTreeSelectionListener(listener);


    return panel;
  }

  private void showNodeStack(LinkedList nodeStack) {
    StringBuffer buffer = new StringBuffer();
    if (nodeStack != null) {
      View[] views = (View[]) nodeStack.toArray(new View[nodeStack.size()]);

      for (int i = views.length - 1; i >= 0; i--) {
        View view = views[i];

        buffer.append(view.getName());

        if (i > 1) ;
        buffer.append("->");
      }
    }

    viewLabel.setText(buffer.toString());
  }

//  protected void performOpenView(final PortalNode node, boolean newWindow) {
//        try {
//          getView().performCommand(ServerPortalView.OPEN_VIEW,
//                                   new Keywords("portalNode", new Integer(node.getId()),
//                                                "container", newWindow ? null : getPortalContainer().getID()));
//        } catch (ComponentException e) {
//          e.printStackTrace();
//        }
//    }

  public void bindViewObject(Object object, Keywords keywords) {
    super.bindViewObject(object, keywords);

    PortalViewPanel panel = (PortalViewPanel) getVisualRepresentation();
    STree portalTree = panel.getPortalTree();
    Object node = portalTree.getModel().getRoot();

    expandTree(portalTree, (DefaultMutableTreeNode) node, 3);
  }

  /**
   * Expands the subnodes of a given node for a tree up to n levels.
   *
   * @param portalTree  the tree to expand
   * @param node        the starting node (this may be the root node)
   * @param level       the number of levels to be expanded
   */
  private void expandTree(STree portalTree, DefaultMutableTreeNode node, int level) {
    if (level <= 0)
      return;

    portalTree.expandRow(new TreePath(node.getPath()));

    int childCount = portalTree.getModel().getChildCount(node);

    for (int i = 0; i < childCount; i++) {
      DefaultMutableTreeNode child = (DefaultMutableTreeNode) portalTree.getModel().getChild(node, i);

      expandTree(portalTree, child, level - 1);
    }
  }
}
