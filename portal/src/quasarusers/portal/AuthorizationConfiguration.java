package quasarusers.portal;

import com.sdm.quasar.lang.Symbol;
import com.sdm.quasar.session.SessionManager;

/**
 * @author Marco Schmickler
 */

//todo dpk 28.01.03 -> MC kommentieren

public abstract class AuthorizationConfiguration implements RuntimeConfiguration {
  protected final Symbol runtimeLog = Symbol.forName("com.sdm.quasar.runtime");
  private ConfigurableRuntime runtime;
  private com.sdm.quasar.session.implementation.AccessController sessionAccessController;
  private com.sdm.quasar.persistence.model.AccessController persistenceAccessController;
  private com.sdm.quasar.businessobject.AccessController businessObjectAccessController;

  public Object getAuthorizationManager() {
    return null;
  }

  /**
   * Installs and initializes the authorization manager.
   */
  public void initialize(ConfigurableRuntime runtime) {
    this.runtime = runtime;
  }

  public void registerManagers() {
  }

  public final com.sdm.quasar.session.implementation.AccessController getSessionAccessController() {
    if (sessionAccessController == null)
      sessionAccessController = makeSessionAccessController();

    return sessionAccessController;
  }

  protected abstract com.sdm.quasar.session.implementation.AccessController makeSessionAccessController();

  public final com.sdm.quasar.persistence.model.AccessController getPersistenceAccessController() {
    if (persistenceAccessController == null)
      persistenceAccessController = makePersistenceAccessController();

    return persistenceAccessController;
  }

  protected abstract com.sdm.quasar.persistence.model.AccessController makePersistenceAccessController();

  public final com.sdm.quasar.businessobject.AccessController getBusinessObjectAcessController(SessionManager sessionManager) {
    if (businessObjectAccessController == null)
      businessObjectAccessController = makeBusinessObjectAcessController(sessionManager);

    return businessObjectAccessController;
  }

  protected abstract com.sdm.quasar.businessobject.AccessController makeBusinessObjectAcessController(SessionManager sessionManager);

  public void startup() {
  }

  public ConfigurableRuntime getRuntime() {
    return runtime;
  }
}
