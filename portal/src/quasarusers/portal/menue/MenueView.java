/*
 * Created by IntelliJ IDEA.
 * User: rademach
 * Date: 08.04.2002
 * Time: 16:27:25
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package quasarusers.portal.menue;

import com.sdm.quasar.component.Command;
import com.sdm.quasar.component.ComponentException;
import com.sdm.quasar.component.NoSuchCommandException;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.lang.Symbol;
import com.sdm.quasar.session.Session;
import com.sdm.quasar.session.implementation.LocalSession;
import com.sdm.quasar.session.implementation.LocalSessionManager;
import com.sdm.quasar.util.Assertion;
import com.sdm.quasar.view.UserInterfaceType;
import com.sdm.quasar.view.View;
import com.sdm.quasar.view.ViewContainer;
import com.sdm.quasar.view.ViewManager;
import com.sdm.quasar.view.ViewTransaction;
import com.sdm.quasar.view.quickstart.PortalNode;
import com.sdm.quasar.view.quickstart.PortalViewVisualizer;
import com.sdm.quasar.view.quickstart.PortalView;
import com.sdm.quasar.view.quickstart.server.ServerPortalView;
import quasarusers.portal.SessionBenutzer;
import quasarusers.portal.client.businessobject.Call;
import quasarusers.portal.client.businessobject.UseCaseManager;

import javax.transaction.SystemException;

/**
 * @author Marco Schmickler
 */

//todo dpk 28.01.03 -> MC kommentieren

public final class MenueView extends PortalView {
  public final static Symbol C_ANMELDEN = Symbol.forName("anmelden");
  public final static Symbol C_AENDERN_PASSWORT = Symbol.forName("aendernPasswort");
  public final static Symbol C_ABMELDEN = Symbol.forName("abmelden");
  public final static Symbol C_EXTERNAL_COMMAND = Symbol.forName("externalCommand");
  public final static Symbol C_RECHTE = Symbol.forName("rechte");

  public MenueView(Keywords arguments) throws ComponentException {
    super(arguments);
  }

  protected void initialize(Keywords arguments) {
    arguments.addValue("serverClass", "de.sdm.sia.remis.portal.menue.MenueViewServer");

    super.initialize(arguments);

    arguments.addValue("name", "MenuView");
    arguments.addValue("object", "");
  }

    public boolean isServerObject(Object object, Keywords arguments) {
        return object instanceof String;
    }

  public String computeVisualizerClassName(Keywords arguments) {
    UserInterfaceType userInterfaceType = getViewManager().getUserInterfaceType();
    String className = null;

    if (userInterfaceType == UserInterfaceType.SWING)
      className = "com.sdm.quasar.view.quickstart.swing.SwingPortalViewVisualizer";
    else if (userInterfaceType == UserInterfaceType.WINGS)
      className = MenueVisualizer.class.getName();
    else
      Assertion.fail("User interface type " + userInterfaceType + " not supported by " + this);

    return className;
  }

//  protected void finalize(Keywords arguments) throws ComponentException {
//    super.finalize(arguments);
//
//    ((PortalViewVisualizer) getViewVisualizer()).getPortalContainer().setupCommands();
//  }

//  public void opened(Keywords arguments) {
//    super.opened(arguments);
//
//    try {
//      performCommand(C_ANMELDEN, new Keywords());
//    } catch (ComponentException e) {
//      e.printStackTrace();
//    }
//  }

  /**
   * Erweitert die geerbten Kommandos um folgende clientseitige Kommandos:
   * <ul>
   *  <li>{@link #OPEN_SUBVIEW}
   * </ul>
   * Erweitert die geerbten Kommandos um folgendes serverseitiges Kommando:
   * <ul>
   *  <li>{@link #C_ANMELDEN}
   *  <li>{@link #C_RECHTE}
   *  <li>{@link #C_AENDERN_PASSWORT}
   *  <li>{@link #C_EXTERNAL_COMMAND}
   * </ul>
   */
  public void buildCommands() {
    super.buildCommands();

      addCommand(new Command(OPEN_SUBVIEW) {
          public Object perform(Keywords arguments) throws ComponentException {
              PortalNode portalNode = (PortalNode)arguments.getValue("portalNode");
              Integer portalNodeId = new Integer(portalNode.getId());
              arguments.setValue("portalNode", portalNodeId);

              if (Boolean.TRUE.equals(arguments.getValue("inPortal"))) {
                  ViewContainer portalContainer = ((PortalViewVisualizer)getViewVisualizer()).getPortalContainer();

                  arguments.addValue("container", portalContainer.getID());
              }

              openSubview(portalNode, arguments);

              return null;
          }
      });

    useViewServerCommand(C_ANMELDEN);

    addCommand(new Command(C_RECHTE) {
      protected Object perform(Keywords arguments) throws ComponentException {
        bindObject(arguments.getValue("rechte"), arguments);

        return null;
      }
    });

    addCommand(new Command(C_ABMELDEN) {
      protected Object perform(Keywords arguments) throws ComponentException {
        try {
          Session session = LocalSessionManager.getSessionManager().getSession();

//          Task[] tasks = session.getTasks();
//          for (int i = 0; i < tasks.length; i++) {
//            tasks[i].abort();
//            tasks[i].setStatus(TaskStatus.STATUS_FINISHED);
//          }

          terminate();

          session.close();

// redirect          ((WingsViewSession)SessionManager.getSession()).getSessionServlet().getFrame().set
        } catch (Exception e) {
          e.printStackTrace();
          return Boolean.FALSE;
        }

        return Boolean.TRUE;
      }
    });

    useViewServerCommand(C_AENDERN_PASSWORT);
    useViewServerCommand(C_EXTERNAL_COMMAND);
  }

  public void updateCommandSetup() {
    super.updateCommandSetup();

    boolean darfAnmelden = true;
    boolean darfAendern = true;

//    try {
//      LocalSession session = (LocalSession) LocalSessionManager.getSessionManager().getSession();
//      SessionBenutzer benutzer = (session != null) ? (SessionBenutzer) LocalSessionManager.getSessionManager().getSession().getLocal("siaUser", null) : null;
//
//      darfAnmelden = (benutzer != null) ? true : false;
//      darfAendern = (benutzer != null) ? benutzer.hatRecht("passwortAendern") : false;
//    } catch (Exception e) {
//    }

    try {
      setCommandEnabled(C_RECHTE, true);
      setCommandEnabled(C_ABMELDEN, true);
      setCommandEnabled(C_ANMELDEN, darfAnmelden);
      setCommandEnabled(C_AENDERN_PASSWORT, darfAendern);
    } catch (NoSuchCommandException e) {
      e.printStackTrace();
      Assertion.fail("NoSuchCommandException");
    }
  }

  public void terminate() {
    ViewContainer container = ((PortalViewVisualizer) getViewVisualizer()).getPortalContainer();
    ViewManager viewManager = getViewManager();

    Object[] openViews = container.getComponents().toArray();

    for (int i = 0; i < openViews.length; i++) {
      View view = (View) openViews[i];
      System.out.println("closed view " + view);
      container.closeView(view, new Keywords());
    }

    viewManager.delistContainer(container);

    ViewTransaction viewTransaction = viewManager.beginTransaction();

    try {
      getContainer().performCommand(ViewContainer.CANCEL_ACTIVE_VIEW,
                                    new Keywords("viewtransaction", viewTransaction, "force", Boolean.TRUE));
      viewManager.commitTransaction(viewTransaction);
    } catch (ComponentException e) {
      viewManager.rollbackTransaction(viewTransaction);
    }
  }

  protected void finalize() throws Throwable {
    System.out.println("finalized " + this);

    super.finalize();
  }

  public void closeOpenViews() {
    ViewContainer container = ((MenueVisualizer) getViewVisualizer()).getPortalContainer();
    Object[] openViews = container.getComponents().toArray();

    for (int i = 0; i < openViews.length; i++) {
      View view = (View) openViews[i];

      try {
        container.closeView(view, new Keywords());
      } catch (Throwable t) {
        t.printStackTrace();
      }
    }
  }

  protected void openSubview(PortalNode portalNode, Keywords arguments) throws ComponentException {
    if (getViewManager().getUserInterfaceType() == UserInterfaceType.WINGS)
        closeOpenViews();

      Keywords keywords = (Keywords)((Keywords)portalNode.getView()).clone();

      if (Boolean.TRUE.equals(arguments.getValue("inPortal"))) {
          ViewContainer portalContainer = ((PortalViewVisualizer)getViewVisualizer()).getPortalContainer();

          keywords.addValue("container", portalContainer.getID());
      }

      String callClassName = (String)keywords.getValue("functioncall", "de.sdm.sia.remis.portal.client.businessobject.OpenViewCall");

      try {
          Call call = (Call)Class.forName(callClassName).newInstance();

          UseCaseManager.getUseCaseManager().call(call, keywords);
      } catch (InstantiationException e) {
          e.printStackTrace();  //To change body of catch statement use Options | File Templates.
      } catch (IllegalAccessException e) {
          e.printStackTrace();  //To change body of catch statement use Options | File Templates.
      } catch (ClassNotFoundException e) {
          e.printStackTrace();  //To change body of catch statement use Options | File Templates.
      }

//      getViewManager().openView((String)keywords.getValue("view"), keywords);
  }
}
