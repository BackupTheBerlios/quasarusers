/*
 * Created by IntelliJ IDEA.
 * User: rademach
 * Date: 08.04.2002
 * Time: 16:28:00
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package quasarusers.portal.menue;

import com.sdm.quasar.component.ComponentException;
import com.sdm.quasar.component.NoSuchCommandException;
import com.sdm.quasar.continuation.ContinuationManager;
import com.sdm.quasar.continuation.StandardContinuationManager;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.lang.Symbol;
import com.sdm.quasar.session.implementation.LocalSessionManager;
import com.sdm.quasar.util.Assertion;
import com.sdm.quasar.view.ViewContainer;
import com.sdm.quasar.view.implementation.wings.WingsApplication;
import com.sdm.quasar.view.quickstart.PortalNode;
import com.sdm.quasar.view.server.AbstractViewServer;
import quasarusers.portal.businessobject.NodeGenerator;
import quasarusers.util.SimpleContinuation;

import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

/**
 * Dies ist der ViewServer für das Menü. Dieser hat Kommandos zum Anmelden
 * eines Benutzers und zum Ändern eines Passworts.
 *
 * @author Marco Schmickler
 * @author Matthias Rademacher
 */

//todo dpk 28.01.03 -> MC kommentieren

public final class MenueViewServer extends AbstractViewServer {
  public final static Symbol C_ANMELDEN = Symbol.forName("anmelden");
  public final static Symbol C_AENDERN_PASSWORT = Symbol.forName("aendernPasswort");
  public final static Symbol C_EXTERNAL_COMMAND = Symbol.forName("externalCommand");
  public final static Symbol C_RECHTE = Symbol.forName("rechte");

  public MenueViewServer(Keywords keywords) throws ComponentException {
    super(keywords);

    try {
      LocalSessionManager.getSessionManager().getSession().setLocal("MenueViewServer", this);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  /**
   * Entfernt aus dem optinalen Keyword OID die Hochkommata.
   *
   * @param arguments  das Keywords-Objekt
   */
  private void trimOID(Keywords arguments) {
    try {
      String oid = (String) arguments.getValue("oid");

      if (oid != null)
        arguments.setValue("oid", oid.replace('\'', ' ').trim());
    } catch (ClassCastException e) {
      // ignore
    }
  }

  private Long getLongValue(Keywords arguments, String key) {
    try {
      final Object value = arguments.getValue(key, null);

      return (value == null) ? null : new Long((String) value);
    } catch (java.lang.NumberFormatException e) {
    }

    return null;
  }

  /**
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

    useViewCommand(C_RECHTE);

  }

    public Object computeViewObject() throws ComponentException {
        if (!(getObject() instanceof PortalNode[]))
            computeRights();

        return super.computeViewObject();
    }

  private void computeRights() {
    try {
      bindObject(new NodeGenerator().makeSystemNodes(), new Keywords());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void updateCommandSetup() {
    super.updateCommandSetup();

    try {
      setCommandEnabled(C_ANMELDEN, true);
      setCommandEnabled(C_AENDERN_PASSWORT, true);
      setCommandEnabled(C_EXTERNAL_COMMAND, true);
    } catch (NoSuchCommandException e) {
      e.printStackTrace();
      Assertion.fail("Unknown command");
    }
  }

  /**
   * Dies ist eine Continuation, die die übergebene Transaction beendet.
   */

  private class TransactionalContinuation extends SimpleContinuation {
    private final UserTransaction mTransaction;
    private final ContinuationManager mContinuationManager
            = StandardContinuationManager.getContinuationManager();

    public TransactionalContinuation(UserTransaction transaction) {
      super("executeExternalCommand");

      this.mTransaction = transaction;
    }

    public void continueWithResult(Object result) {
      try {
        mTransaction.commit();
        super.continueWithResult(result);
        mContinuationManager.continueWithResult(result);
        activateMenuView();
      } catch (Exception e) {
        continueWithException(e);
      }
    }

    private void activateMenuView() throws SystemException {
      MenueView view = (MenueView) LocalSessionManager.getSessionManager().getSession().getLocal("MenueView", null);
      WingsApplication.getApplication().suspendReload();
      ((ViewContainer) view.getContainer()).activateView(view, Keywords.NONE);
    }

    public void continueWithException(Exception exception) {
      try {
        mTransaction.rollback();
        activateMenuView();
        super.continueWithException(exception);
      } catch (Exception e) {
        super.continueWithException(e);
      } finally {
        mContinuationManager.continueWithException(exception);

      }
    }
  }

}
