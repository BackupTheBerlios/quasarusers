/*
* Created by IntelliJ IDEA.
* User: pannier
* Date: Mar 4, 2002
* Time: 4:15:54 PM
* To change template for new class use
* Code Style | Class Templates options (Tools | IDE Options).
*/
package quasarusers.portal.client;

import com.sdm.quasar.businessobject.BusinessObjectException;
import com.sdm.quasar.component.ComponentException;
import com.sdm.quasar.component.SynchronousCommandPerformer;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.newmodelview.*;
import com.sdm.quasar.newmodelview.implementation.wings.WingsFieldMappingFactory;
import com.sdm.quasar.newmodelview.mapping.FieldMappingFactory;
import com.sdm.quasar.view.*;
import com.sdm.quasar.view.implementation.swing.SwingDialogManager;
import com.sdm.quasar.view.implementation.swing.WindowContainer;
import com.sdm.quasar.view.implementation.wings.WingsApplication;
import quasarusers.portal.menue.MenueView;
import quasarusers.portal.client.businessobject.UseCaseManager;
import quasarusers.portal.server.QuasarSwingSession;
import quasarusers.portal.server.QuasarSwingSessionHome;
import quasarusers.util.MD5Encoder;
import org.wings.SConstants;
import org.wings.SFrame;

import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.SystemException;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Das WingsClient existiert einmal pro wingS-Session. Es übernimmt die
 * automatische Anmeldung des Benutzers als Gast. Die Argumente eines
 * Requests werden als jeff-Session-lokale Variable abgelegt.
 * Beim Beenden der wingS-Session wird die jeff-Session beendet.
 *
 * @author Marco Schmickler
 * @author Matthias Rademacher
 */
public final class WingsClient extends WingsApplication implements SConstants {
  private static final String PATH = "de/sdm/sia/remis/portal/";

  private SFrame frame;
  private ViewManager viewManager;
  private QuasarSwingSession session;


  protected void buildSession() {
    try {
      Locale.setDefault(Locale.GERMANY);

      HttpServletRequest servletRequest = getSession().getServletRequest();

      String user = servletRequest.getParameter("user");
      String password = new MD5Encoder().encode(servletRequest.getParameter("password"));

      if (user == null) {
        final String[] cookieData = getDataFromCookie(servletRequest);

        if (cookieData != null) {
          user = cookieData[0];
          password = cookieData[1];
        }
      }

        InitialContext jndiContext = new InitialContext();
        System.out.println("Got context");

        // Get a reference to the Interest Bean
        Object ref  = jndiContext.lookup("quasar/QuasarSwingSession");
        System.out.println("Got reference");

        // Get a reference from this to the Bean's Home interface
        QuasarSwingSessionHome home = (QuasarSwingSessionHome)
        PortableRemoteObject.narrow(ref, QuasarSwingSessionHome.class);

        // Create an Interest object from the Home interface
        session = home.create();

        viewManager = new ViewManager(new Keywords(new Object[] {
                                                "dialogManager", new SwingDialogManager(),
                                                "delegate", new SwingClient.EJBCommandControllerManager(session),
                                                "commandPerformer", new Object[] { null, new SynchronousCommandPerformer(),
                                                                                   AbstractView.class, new ViewCommandPerformer(),
                                                                                   ObjectView.class, new SimpleObjectViewCommandPerformer()},
                                                "viewOpener", new SimpleViewOpener(),
                                                "userInterfaceType", UserInterfaceType.WINGS,
                                                "viewServerStarter", new SwingClient.CommunicationViewServerStarter() }));

        viewManager.setDefaultContainer(
            null, Keywords.NONE,
            new ViewManager.ContainerGenerator() {
                public ViewContainer makeContainer(Keywords arguments) throws ComponentException {
                    return viewManager.makeViewContainer(
                        WindowContainer.class,
                        new Keywords(new Object[] { "viewMenus", Boolean.TRUE, "viewTools", Boolean.TRUE,
                                                    "activators", Boolean.TRUE }));
                }
        });

        new ModelViewManager(viewManager) {
              public ViewCollectionController makeViewCollectionController(CollectionView collectionView, Object object) {
                  return new ProxyCollectionController(collectionView);
              }

            public FieldMappingFactory getDefaultFieldMappingFactory() {
                return new WingsFieldMappingFactory();
            }
          };

        new UseCaseManager() {
            public Object[] performRemote(Keywords arguments) {
                try {
                    return session.performUseCase(arguments);
                } catch (RemoteException e) {
                    e.printStackTrace();  //To change body of catch statement use Options | File Templates.
                } catch (BusinessObjectException e) {
                    e.printStackTrace();  //To change body of catch statement use Options | File Templates.
                }

                return null;
            }

            public ViewManager getViewManager() {
                return viewManager;
            }
        };

      if (user != null) {
        try {
          session.login(user, password);
        } catch (Exception e) {
            session.login("gast", "");
        }
      } else
          session.login("gast", "");

      frame = new WingsPortalFrame(viewManager);
      frame.show();

      viewManager.openView(MenueView.class, new Keywords());

      // Locale des Benutzers steckt im AcessTicket
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  protected Object buildEnvironment() {
    try {
      Object runtime = this;


      return runtime;
    } catch (Throwable exception) {
      exception.printStackTrace();
    }
    return null;
  }

  public void freeSessionResources() {
    ViewManager viewManager = ViewManager.getViewManager();

//    try {
//      MenueView view = (MenueView) mSession.getLocal("MenueView", null);
//      ViewContainer container = (ViewContainer) view.getContainer();
//
//      if (view != null)
//        view.terminate();
//
//      viewManager.delistContainer(container);
//
//      getSession().setProperty("container", null);
//
//      Task[] tasks = mSession.getTasks();
//
//      for (int i = 0; i < tasks.length; i++)
//        tasks[i].setStatus(TaskStatus.STATUS_FINISHED);
//
//      mSession.close();
//      mSession = null;
//    } catch (Exception e) {
//      e.printStackTrace();
//    }

    long before = Runtime.getRuntime().freeMemory();

    Runtime.getRuntime().runFinalization();
  }

  /**
   * Ermittelt Benutzerkennung und verschlüsseltes Passwort aus dem
   * Cookie mit dem Namen "ISIS".
   *
   * @return  die Benutzerkennung und das verschlüsselte Passwort
   */
  private String[] getDataFromCookie(HttpServletRequest servletRequest) {
    Cookie[] cookies = servletRequest.getCookies();

    try {
      if (cookies != null)
        for (int i = 0; i < cookies.length; i++) {
          Cookie cookie = cookies[i];
          if ("ISIS".equals(cookie.getName())) {
            StringTokenizer stringTokenizer = new StringTokenizer(cookie.getValue(), "|");

            String user = stringTokenizer.nextToken();
            String password = stringTokenizer.nextToken();

            return new String[]{user, password};
          }
        }
    } catch (Exception e) {
      // ignore
    }

    return null;
  }
}
