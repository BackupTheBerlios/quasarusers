/*
* Created by IntelliJ IDEA.
* User: pannier
* Date: Mar 5, 2002
* Time: 3:07:20 PM
* To change template for new class use
* Code Style | Class Templates options (Tools | IDE Options).
*/
package quasarusers.portal.client;

import com.sdm.quasar.component.ComponentException;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.view.ViewManager;
import com.sdm.quasar.view.implementation.wings.StackingContainer;
import com.sdm.quasar.view.quickstart.PortalView;
import org.wings.*;


/**
 * Dies ist der Frame für die REMIS-Anwendung...
 *
 * @author Marco Schmickler
 */
public final class WingsPortalFrame extends SFrame {
  private StackingContainer container;
    private ViewManager viewManager;

    public WingsPortalFrame(ViewManager viewManager) {
        this.viewManager = viewManager;
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
      container = (StackingContainer)viewManager.makeViewContainer(StackingContainer.class, (new Keywords("useMenuBar", Boolean.FALSE,
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

    viewManager.setDefaultContainer(PortalView.class, new Keywords(), container);
  }
}
