/*
* Created by IntelliJ IDEA.
* User: pannier
* Date: Mar 5, 2002
* Time: 3:07:20 PM
* To change template for new class use
* Code Style | Class Templates options (Tools | IDE Options).
*/
package quasarusers.portal;

import com.sdm.quasar.component.ComponentException;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.view.ViewManager;
import com.sdm.quasar.view.implementation.wings.StackingContainer;
import com.sdm.quasar.view.implementation.wings.WingsApplication;
import com.sdm.quasar.view.quickstart.PortalView;
import com.sdm.quasar.view.server.SimpleViewDescription;
import com.sdm.quasar.view.server.ViewDescription;
import com.sdm.quasar.view.server.ViewServerManager;
import quasarusers.util.businessobject.NodeGenerator;
import org.wings.SBorderLayout;
import org.wings.SContainer;
import org.wings.SDimension;
import org.wings.SForm;
import org.wings.SFrame;


/**
 * Dies ist der Frame für die REMIS-Anwendung...
 *
 * @author Marco Schmickler
 */
public final class SiaPortalFrame extends SFrame {
  private StackingContainer container;

  public SiaPortalFrame() {
    setTitle("ISIS");

    // Workaround: Verhindert einen Session Timeout nach 300 s (Tomcat-Einstellung)

    // Nach dem Refresh wird die Seesion serverseitig nicht mehr terminiert...
    // deswegen muss der Refresher vorerst wieder ausgebaut werden.
//    addMeta("HTTP-EQUIV=REFRESH CONTENT=\"240\"");

    buildContentPanel();
  }

  public StackingContainer getContainer() {
    return container;
  }

  protected void buildContentPanel() {
    SContainer contentPane = getContentPane();
    contentPane.setLayout(new SBorderLayout());
    contentPane.setPreferredSize(new SDimension("100%", "100%"));

    try {
      container = (StackingContainer)ViewManager.getViewManager().makeViewContainer(StackingContainer.class, (new Keywords("useMenuBar", Boolean.FALSE,
                                                     "useToolContainer", Boolean.FALSE,
                                                     "useActivators", Boolean.TRUE)));
    } catch (ComponentException e) {
      e.printStackTrace();
    }

    SContainer containerPanel = container.getContainerPanel();
    containerPanel.setPreferredSize(new SDimension("100%", "100%"));

    SForm form = new SForm();

    form.setEncodingType("multipart/form-data"); // ist nötig, damit SFileChooser funktionieren
    form.setLayout(new SBorderLayout());
    form.setPreferredSize(new SDimension("100%", "100%"));
    form.add(containerPanel, SBorderLayout.CENTER);

    contentPane.add(form, SBorderLayout.CENTER);

    final ViewManager viewManager = ViewManager.getViewManager();

    viewManager.setDefaultContainer(PortalView.class, new Keywords(), container);

    final ViewServerManager viewServerManager = ConfigurableRuntime.getViewServerManager();
    final ViewDescription portalView = new SimpleViewDescription("menue",
                                                 "Menue",
                                                 "",
                                                 quasarusers.portal.menue.MenueView.class.getName(),
                                                 quasarusers.portal.menue.MenueViewServer.class.getName());

    try {
      WingsApplication.getApplication().suspendReload();
      viewServerManager.openView(portalView, new NodeGenerator().makeSystemNodes(), new Keywords());
    } catch (Exception e) {
      e.printStackTrace();
    }

    WingsApplication.getApplication().responding();
  }
}
