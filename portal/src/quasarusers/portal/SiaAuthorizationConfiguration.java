package quasarusers.portal;

import com.sdm.quasar.businessobject.BusinessModule;
import com.sdm.quasar.businessobject.BusinessObject;
import com.sdm.quasar.businessobject.BusinessObjectManager;
import com.sdm.quasar.businessobject.BusinessObjectProperty;
import com.sdm.quasar.businessobject.BusinessSystem;
import com.sdm.quasar.persistence.AccessMode;
import com.sdm.quasar.persistence.model.PropertyDescriptor;
import com.sdm.quasar.persistence.model.StandardPersistenceModelManager;
import com.sdm.quasar.persistence.model.TypeDescriptor;
import com.sdm.quasar.session.AccessTicket;
import com.sdm.quasar.session.Session;
import com.sdm.quasar.session.SessionAccessDeniedException;
import com.sdm.quasar.session.SessionEvent;
import com.sdm.quasar.session.SessionListener;
import com.sdm.quasar.session.SessionManager;
import com.sdm.quasar.session.SessionType;
import com.sdm.quasar.session.implementation.AccessController;

import javax.transaction.SystemException;
import java.util.Locale;

/**
 * Dies ist die Anbindung des Remis-Berechtigungssystem an Quasar.
 *
 * @author Matthias Rademacher
 */
public final class SiaAuthorizationConfiguration
        extends AuthorizationConfiguration
        implements SessionListener {
  private SessionManager mSessionManager;
  private Session mBackgroundSession;

  /**
   * Beim Anmelden legt hier jeder {@link Thread}
   * seinen eigenen Wert für den {@link SessionBenutzer}
   * ab. Dieser wird in {@link SessionAccessController#authorize} eingetragen
   * und in {@link #sessionOpened} ausgelesen/ausgetragen.
   */
  private ThreadLocal mSiaUser = new InheritableThreadLocal();

  public void sessionOpened(SessionEvent event) {
    try {
      // Wenn die Session geäffnet wird, wird der SessionBenutzer
      // dort eingetragen.
      event.getSession().setLocal("siaUser", mSiaUser.get());

      // Der SessionBenutzer wird sicherheitshalber aus mSiaUser ausgetragen
      // (damit keiner mehr versehentlich dran kommt)
      mSiaUser.set(null);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void sessionClosed(SessionEvent event) {
    try {
      event.getSession().setLocal("siaUser", null);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void startup() {
    super.startup();
    mSessionManager = getRuntime().getSessionManager();

    try {
      mSessionManager.open("system", "", SessionType.BACKGROUND);
      mBackgroundSession = mSessionManager.suspend();
      mSessionManager.addSessionListener(this);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private Object getUser() {
    try {
      Session session = mSessionManager.getSession();
      Object user = session.getLocal("siaUser", null);

      return user;
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (SystemException e) {
      e.printStackTrace();
    }

    return null;
  }

  protected AccessController makeSessionAccessController() {
    return new SessionAccessController();
  }

  protected com.sdm.quasar.persistence.model.AccessController makePersistenceAccessController() {
    return new PersistenceAccessController();
  }

  protected com.sdm.quasar.businessobject.AccessController makeBusinessObjectAcessController(
          SessionManager sessionManager) {
    return new BusinessObjectAccessController();
  }

  protected class SessionAccessController implements AccessController {
    public AccessTicket authorize(final String user, String password) throws SessionAccessDeniedException {
      if (!user.equals("system")) {
        try {
          try {
            // Wechsel in die BackgroundSession
            mSessionManager.suspend();
            mSessionManager.resume(mBackgroundSession);

            // Erzeugen des SessionBenutzers (mit den Rechten des Benutzers)
            final SessionBenutzer sessionBenutzer = new SessionBenutzer(user, password);

            // Merken des Objekts zum Thread
            mSiaUser.set(sessionBenutzer);
          } catch (Exception e) {
            e.printStackTrace();
          } finally {
            // Wechselt zurück in die eigentliche Session
            mBackgroundSession = mSessionManager.suspend();
          }

        } catch (Exception e) {
          e.printStackTrace();
        }
      }

      return new AccessTicket() {
        public String getUser() {
          return user;
        }
        ;

        public Locale getLocale() {
          return Locale.getDefault();
        }
        ;
      };
    }

    public boolean isAccessRestricted(String type, Object object, String operation,
                                      Object argument) throws IllegalStateException, SystemException {
      return false;
    };

    public boolean isAccessPermitted(AccessTicket ticket, String type, Object object, String operation,
                                     Object argument) throws IllegalStateException, SystemException {
      return true;
    };
  }

  protected class PersistenceAccessController implements com.sdm.quasar.persistence.model.AccessController {
    public boolean isAccessControlled(PropertyDescriptor propertyDescriptor, AccessMode accessMode) {
      return false;
    }

    public boolean isAccessControlled(TypeDescriptor typeDescriptor, AccessMode accessMode) {
      return false;
    }

    public boolean isAccessPermitted(PropertyDescriptor propertyDescriptor, AccessMode accessMode) {
      return true;
    }

    public boolean isAccessPermitted(TypeDescriptor typeDescriptor, AccessMode accessMode) {
      return true;
    }

    public void setPersistenceModelManager(StandardPersistenceModelManager modelManager) {
    }
  }

  protected class BusinessObjectAccessController implements com.sdm.quasar.businessobject.AccessController {
    public boolean isAccessPermitted(BusinessSystem system) {
      return hatRecht(system.getName());
    }

    public boolean isAccessPermitted(BusinessModule module) {
      return hatRecht(module.getName());
    }

    public boolean isAccessPermitted(BusinessObject object) {
      return hatRecht(object.getName());
    }

    public boolean isAccessPermitted(BusinessObject object, BusinessObjectProperty property) {
      return hatRecht(property.getName());
    }

    public void setBusinessObjectManager(BusinessObjectManager manager) {
    }

    /**
     * Prüft, ob der Benutzer der aktuellen Session das angefrage Recht hat.
     *
     * @param  name der Name des Rechts
     * @return <code>true</code>, wenn der Benutzer das Recht hat
     */
    private boolean hatRecht(String name) {
      boolean ergebnis = ((SessionBenutzer) getUser()).hatRecht(name);

//      System.out.println("Recht " + name + (ergebnis ? "" : " nicht") + " gewährt für " +
//              ((SessionBenutzer) getUser()).getKennung() + "(" + getUser() + ")");

      return ergebnis;
    }
  }
}
