/*
* Created by IntelliJ IDEA.
* User: pannier
* Date: Mar 4, 2002
* Time: 4:15:54 PM
* To change template for new class use
* Code Style | Class Templates options (Tools | IDE Options).
*/
package quasarusers.portal;

import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.session.SessionAccessDeniedException;
import com.sdm.quasar.session.SessionType;
import com.sdm.quasar.session.Task;
import com.sdm.quasar.session.TaskStatus;
import com.sdm.quasar.session.implementation.LocalSessionManager;
import com.sdm.quasar.view.ViewContainer;
import com.sdm.quasar.view.ViewManager;
import com.sdm.quasar.view.implementation.wings.WingsApplication;
import com.sdm.quasar.view.server.ViewServerManager;
import quasarusers.portal.menue.MenueView;
import quasarusers.portal.menue.MenueViewServer;
import quasarusers.util.MD5Encoder;
import org.wings.SConstants;
import org.wings.SFrame;
import org.wings.event.SRequestEvent;
import org.wings.event.SRequestListener;
import org.wings.session.SessionManager;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.SystemException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Das MainSessionServlet existiert einmal pro wingS-Session. Es übernimmt die
 * automatische Anmeldung des Benutzers als Gast. Die Argumente eines
 * Requests werden als jeff-Session-lokale Variable abgelegt.
 * Beim Beenden der wingS-Session wird die jeff-Session beendet.
 *
 * @author Marco Schmickler
 * @author Matthias Rademacher
 */
public final class MainSessionServlet extends WingsApplication implements SConstants {
  private static final String PATH = "de/sdm/sia/remis/portal/";

  /** Die Jeff-Session */
  private com.sdm.quasar.session.Session mSession;

  private SFrame frame;

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

      if (user != null) {
        try {
          LocalSessionManager.getSessionManager().open(user, password, SessionType.INTERACTIVE);
        } catch (Exception e) {
          openGastSession();
        }
      } else
        openGastSession();

      mSession = LocalSessionManager.getSessionManager().getSession();

      SessionManager.getSession().addRequestListener(new SRequestListener() {
        public void processRequest(SRequestEvent e) {
          if (e.getType() == SRequestEvent.DISPATCH_START) {
            LocalSessionManager sessionManager = LocalSessionManager.getSessionManager();

            try {
              if (sessionManager.getSession() != mSession) {
                sessionManager.suspend();
                sessionManager.resume(mSession);
              }
            } catch (Exception e1) {
            }
          }
        }
      });

      mSession.setLocal("sessionViewServerManager", ConfigurableRuntime.getRuntime().getConfiguration().getSingleton(ViewServerManager.class));

      frame = new SiaPortalFrame();
      frame.show();

      // Locale des Benutzers steckt im AcessTicket
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  protected Object buildEnvironment() {
    try {
      ConfigurableRuntime runtime;

      String configFile = getSession().getServletContext().getInitParameter("configuration");
      String authorizationClassName = getSession().getServletContext().getInitParameter("authorization");
      Class authorizationClass;

      if (authorizationClassName == null)
        authorizationClass = quasarusers.portal.SiaAuthorizationConfiguration.class;
      else
        authorizationClass = Class.forName(authorizationClassName);

      if (configFile == null)
        configFile = PATH + "configuration.xml";

      runtime = new ConfigurableRuntime(configFile, (AuthorizationConfiguration) authorizationClass.newInstance());

//        runtime.initialize(new String[0]); todo: fix this

      return runtime;
    } catch (Throwable exception) {
      exception.printStackTrace();
    }
    return null;
  }

  public synchronized void dispatch() {
    try {
      HttpServletRequest req = getSession().getServletRequest();
      LocalSessionManager.getSessionManager().suspend();
      LocalSessionManager.getSessionManager().resume(mSession);

      // Argumente des requests in der Session ablegen
      final String commandString = req.getParameter("command");

      if (commandString != null) {
        if (commandString.equals("exit")) {
          exit("http://sww.sdm.de");
          return;
        }

        // Statusseite aufrufbar über Parameter status
        if (commandString.equals("status")) {
          new StatusFrame().show();
          return;
        }

        final Keywords arguments = makeKeywords(req);
        final MenueViewServer viewserver = (MenueViewServer) mSession.getLocal("MenueViewServer", null);
//        StackingContainer container = ((SiaPortalFrame) getFrame()).getContainer();

        closeOpenViews();

        if (viewserver != null) {
          suspendReload();

          try {
            ViewManager.getViewManager().getThreadPoolAdapter().start(new Runnable() {
              public void run() {
                try {
//                  viewserver.executeExternalCommand(arguments);
                } catch (Exception e) {
                  e.printStackTrace();
                }
              }
            });

            responding();

          } catch (Exception e) {
            e.printStackTrace();

            resumeReload();
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /** Schließt alle offenen Views */
  private void closeOpenViews() {
    try {
      MenueView menueView = (MenueView) mSession.getLocal("MenueView", null);

      menueView.closeOpenViews();
    } catch (SecurityException e) {
    } catch (SystemException e) {
    }
  }

  /**
   * Wandelt die Parameter des HTTP-requests in ein Keywords-Objekt.
   *
   * @param  req       der HTTP-request
   * @return die Keywords
   */
  private Keywords makeKeywords(HttpServletRequest req) {
    final Keywords arguments = new Keywords();
    Enumeration parameterNames = req.getParameterNames();

    while (parameterNames.hasMoreElements()) {
      String parameter = (String) parameterNames.nextElement();
      String value = req.getParameter(parameter);

      arguments.addValue(parameter, value);
    }

    return arguments;
  }

  protected void finalizeRequest(HttpServletRequest req,
                                 HttpServletResponse response) {
    try {
      LocalSessionManager.getSessionManager().suspend();
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (SystemException e) {
      e.printStackTrace();
    }

  }

  public void freeSessionResources() {
    ViewManager viewManager = ViewManager.getViewManager();

    try {
      MenueView view = (MenueView) mSession.getLocal("MenueView", null);
      ViewContainer container = (ViewContainer) view.getContainer();

      if (view != null)
        view.terminate();

      viewManager.delistContainer(container);

      getSession().setProperty("container", null);

      Task[] tasks = mSession.getTasks();

      for (int i = 0; i < tasks.length; i++)
        tasks[i].setStatus(TaskStatus.STATUS_FINISHED);

      mSession.close();
      mSession = null;
    } catch (Exception e) {
      e.printStackTrace();
    }

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

  /**
   * Startet eine Session für einen Gast.
   */
  private void openGastSession() throws SessionAccessDeniedException, SystemException {
    LocalSessionManager.getSessionManager().open("gast", // Benutzerkennung
                                                 "XXXXXXXXXXXXXXXX", // Das verschlüsselte Passwort
                                                 SessionType.INTERACTIVE);
  }
}
