/*
 * Created by IntelliJ IDEA.
 * User: pannier
 * Date: Mar 5, 2002
 * Time: 3:07:20 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package quasarusers.portal;

import com.sdm.quasar.businessobject.BusinessObject;
import com.sdm.quasar.component.ComponentException;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.view.AbstractView;
import com.sdm.quasar.view.ViewManager;
import com.sdm.quasar.view.implementation.wings.StackingContainer;
import com.sdm.quasar.view.implementation.wings.WingsApplication;
import quasarusers.util.SimpleContinuation;
import quasarusers.util.businessobject.BOHelper;
import quasarusers.util.businessobject.ViewProperty;
import org.wings.ResourceImageIcon;
import org.wings.SBorderLayout;
import org.wings.SContainer;
import org.wings.SDesktopPane;
import org.wings.SFlowLayout;
import org.wings.SForm;
import org.wings.SFrame;
import org.wings.SIcon;
import org.wings.SLabel;
import org.wings.SPanel;
import org.wings.SToolbar;
import org.wings.STree;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;

/**
 * Dies ist der Frame für die REMIS-Anwendung.
 *
 * @author Marco Schmickler
 */

//todo dpk 28.01.03 -> MC kommentieren

public final class SimpleSiaFrame extends SFrame {
  private final static String PICS = "de/sdm/sia/remis/portal/";
  private final static ResourceImageIcon ICON_LOGO = new ResourceImageIcon(PICS + "sdmlogo.gif");

  private SPanel menuPanel;
  private SPanel contentPanel;
  private SPanel reloadPanel;
  private StackingContainer container;

  public SimpleSiaFrame() {
    getContentPane().setLayout(new SBorderLayout());

    contentPanel = buildContentPanel();
    menuPanel = buildMenuPanel();
    reloadPanel = new SPanel();
    reloadPanel.add(new SLabel("Mitarbeiter-Informationssystem"));

    getContentPane().add(menuPanel, SBorderLayout.WEST);
    getContentPane().add(contentPanel, SBorderLayout.CENTER);
    getContentPane().add(reloadPanel, SBorderLayout.NORTH);
  }

  public StackingContainer getContainer() {
    return container;
  }

  public SPanel getContentPanel() {
    return contentPanel;
  }

  public SPanel getMenuPanel() {
    return menuPanel;
  }

  public SPanel getReloadPanel() {
    return reloadPanel;
  }

  protected SPanel buildMenuPanel() {
    SPanel contentPane = new SPanel();

    contentPane.setBackground(Color.white);
    DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
    BOHelper.getBOHelper().registerBusinessSystems(root);

    DefaultTreeModel model = new DefaultTreeModel(root);
    STree tree = new STree(model);

    tree.setRootVisible(false);
    tree.expandRow(0);
    tree.addTreeSelectionListener(new TreeSelectionListener() {
      public void valueChanged(TreeSelectionEvent event) {
        TreePath path = event.getPath();
        Object[] components = path.getPath();
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) components[components.length - 1];

        if (selectedNode.getUserObject() instanceof ViewProperty) {
          try {
            SimpleContinuation continuation
                    = new SimpleContinuation("SiaFrame") {
                      public void continueWithResult(Object result) {
                        //System.out.println("################## performed");
                        WingsApplication.getApplication().resumeReload();
                      }

                      public void continueWithException(Exception exception) {
                        System.out.println("################## exception");
                        exception.printStackTrace();
                        WingsApplication.getApplication().resumeReload();
                      }
                    };

            WingsApplication.getApplication().suspendReload();

            ViewProperty viewProperty = (ViewProperty) ((DefaultMutableTreeNode) selectedNode).getUserObject();
            BusinessObject businessObject = (BusinessObject) ((DefaultMutableTreeNode) selectedNode.getParent()).getUserObject();

// todo            viewProperty.open(businessObject, Keywords.NONE, continuation);
          } catch (Exception exception) {
            exception.printStackTrace(); // ???
          }
        }
      }
    });

    SPanel panel = new SPanel();

    SIcon sdmLogo = ICON_LOGO;

    panel.add(new SLabel(sdmLogo));
    panel.add(tree);

    contentPane.add(panel);

    return contentPane;
  }

  protected SPanel buildContentPanel() {
    SPanel contentPane = new SPanel();

    SForm form = new SForm();
    SPanel toolContainer = new SPanel();
    SToolbar toolBar = new SToolbar();

    try {
      container = new StackingContainer(new Keywords("useMenuBar", Boolean.FALSE,
                                                     "useToolContainer", toolContainer,
                                                     "useActivators", Boolean.TRUE));
      /* todo*/
//      container.setToolContainer(toolContainer);
    } catch (ComponentException e) {
      e.printStackTrace();
    }
    form.setLayout(new SBorderLayout());

    toolContainer.setLayout(new SFlowLayout());
    toolContainer.add(toolBar);

    SContainer containerPanel = container.getContainerPanel();

    containerPanel.setBackground(Color.white);

    form.add(toolContainer, SBorderLayout.NORTH);
    form.add(containerPanel);

    contentPane.add(form);

    final ViewManager viewManager = ViewManager.getViewManager();

    viewManager.setDefaultContainer(AbstractView.class, new Keywords(), container);
    SDesktopPane desktopPane = new SDesktopPane();

    contentPane.add(desktopPane);
// todo    ((WingsViewSession) getSession()).setDesktopPane(desktopPane);

    return contentPane;
  }
}
