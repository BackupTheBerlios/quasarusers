package quasarusers.portal;

import com.sdm.quasar.businessobject.AccessController;
import com.sdm.quasar.businessobject.BusinessModule;
import com.sdm.quasar.businessobject.BusinessObject;
import com.sdm.quasar.businessobject.BusinessObjectManager;
import com.sdm.quasar.businessobject.BusinessObjectProperty;
import com.sdm.quasar.businessobject.BusinessSystem;
import com.sdm.quasar.businessobject.PropertySelector;
import com.sdm.quasar.businessobject.workspace.Workspace;
import com.sdm.quasar.continuation.ContinuationManager;
import com.sdm.quasar.continuation.StandardContinuationManager;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.lang.Symbol;
import com.sdm.quasar.lang.ThreadPool;
import com.sdm.quasar.persistence.Pool;
import com.sdm.quasar.persistence.query.QueryManager;
import com.sdm.quasar.persistence.query.implementation.StandardQueryManager;
import com.sdm.quasar.session.SessionManager;
import com.sdm.quasar.session.UserSession;
import com.sdm.quasar.session.implementation.LocalSessionManager;
import com.sdm.quasar.transaction.implementation.LocalTransactionManager;
import com.sdm.quasar.util.IconSize;
import com.sdm.quasar.util.Logger;
import com.sdm.quasar.view.ViewManager;
import com.sdm.quasar.view.server.ViewServerManager;
import com.sdm.quasarx.configuration.Configuration;
import com.sdm.quasarx.configuration.ConfigurationModule;
import com.sdm.quasarx.configuration.ModelViewConfiguration;
import com.sdm.quasarx.configuration.PersistenceConfiguration;
import com.sdm.quasarx.configuration.ViewConfiguration;
import com.sdm.quasarx.configuration.CommunicationConfiguration;

import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;

/**
 * The class <code>ServerRuntime</code> implements a special <code>Runtime</code> for
 * a server side process. It installs several framework components (see {@link #initializeComponents}
 * for details) before the server activates its service ports.
 *
 * @author  Marco Schmickler
 */
public class ConfigurableRuntime {
  private static ConfigurableRuntime configurableRuntime;

  private final Symbol runtimeLog = Symbol.forName("com.sdm.quasar.runtime");

  private String configurationFile = null;

  private Configuration configuration = null;

  private AuthorizationConfiguration authorizationConfiguration = null;
  private ThreadPool threadPool;

  public String getConfigurationFileNameProperty() {
    return "serverConfigurationFile";
  }

  public String getDefaultConfigurationFileName() {
    return configurationFile;
  }

  public ConfigurableRuntime(String configFile, AuthorizationConfiguration authorizationConfiguration) {
    configurableRuntime = this;
    this.authorizationConfiguration = authorizationConfiguration;
    this.configurationFile = configFile;

    this.configuration = new Configuration(null , new ConfigurationModule[]{
            new PersistenceConfiguration(),
            new CommunicationConfiguration(),
            new ViewConfiguration(),
            new ModelViewConfiguration() }) {
      protected void finishSetup() {
        initializeComponents();
        registerManagers();

        super.finishSetup();
      }
    };

    this.configuration.registerSingleton(com.sdm.quasar.session.implementation.AccessController.class, authorizationConfiguration.getSessionAccessController());
    this.configuration.setup(getClass().getClassLoader().getResource(configFile));
  }

  public static ConfigurableRuntime getRuntime() {
    return configurableRuntime;
  }

  public AuthorizationConfiguration getAuthorizationConfiguration() {
    return authorizationConfiguration;
  }

  /**
   * Installs and initializes the authorization manager.
   */
  public void initializeAuthorizationManager() {
    Logger.getConsoleLogger().log(runtimeLog, "  Initializing authorization manager...");

    getAuthorizationConfiguration().initialize(this);
  }

  /**
   * Returns the installed global thread pool.
   *
   * @return  the installed global thread pool
   */
  public final ThreadPool getThreadPool() {
    if (threadPool == null)
      threadPool = new ThreadPool(50, 10);
    return threadPool;
  }


  /**
   * Installs and initializes the business object manager.
   */
  public void initializeBusinessObjectManager() {
    Logger.getConsoleLogger().log(runtimeLog, "  Initializing business object manager...");

    final BusinessObjectManager businessObjectManager =
            new SiaBusinessObjectManager(getAuthorizationConfiguration().getBusinessObjectAcessController(getSessionManager()),
                                         new com.sdm.quasar.businessobject.ThreadPoolAdapter() {
                                           public void start(Runnable runnable) {
                                             try {
                                               getThreadPool().start(runnable);
                                             } catch (Exception e) {
                                               throw new RuntimeException("Cannot start new thread due to " + e);
                                             }
                                           }
                                         });
  }

  public SessionManager getSessionManager() {
      return (SessionManager)configuration.getSingleton(SessionManager.class);
  }


  /**
   * Starts the authorization system after all other components have been initialized.
   */
  public void startAuthorizationManager() {
    getAuthorizationConfiguration().startup();
  }


  /**
   * <code>initializeComponents</code> is called by <code>initialize</code> to install
   * and initialize all framework components configured for the runtime. This method
   * will install the components:
   * <p><ul>
   *     <li> com.sdm.quasar.communication </li>
   *     <li> com.sdm.quasar.authorization </li>
   *     <li> com.sdm.quasar.transaction </li>
   *     <li> com.sdm.quasar.session </li>
   *     <li> com.sdm.quasar.persistence </li>
   *     <li> com.sdm.quasar.businessobject </li>
   *     <li> com.sdm.quasar.validation (the engine to perform validations; the dialog part is not used)</li>
   *     <li> com.sdm.quasar.view (the server side view server manager)</li>
   * </ul>
   */
  public void initializeComponents() {
    initializeAuthorizationManager();
    initializeBusinessObjectManager();

    startAuthorizationManager();
  }

  public void registerManagers() {
    BusinessObjectManager manager = BusinessObjectManager.getBusinessObjectManager();

//    manager.registerSingleton(ContinuationManager.class, StandardContinuationManager.getContinuationManager());
    manager.registerSingleton(TransactionManager.class, configuration.getSingleton(TransactionManager.class));
    manager.registerSingleton(UserTransaction.class,  configuration.getSingleton(TransactionManager.class));
    manager.registerSingleton(SessionManager.class, getSessionManager());
    manager.registerSingleton(UserSession.class, getSessionManager());
    getAuthorizationConfiguration().registerManagers();
//    manager.registerSingleton(TaskManager.class, getTaskManager());
//    manager.registerSingleton(PersistenceModelManager.class, getPersistenceModelManager());
    manager.registerSingleton(Pool.class, configuration.getSingleton(Pool.class));
      Object singleton = configuration.getSingleton(QueryManager.class);
      manager.registerSingleton(QueryManager.class, singleton);
//    manager.registerSingleton(ValidationManager.class, getValidationManager());
//    manager.registerSingleton(ViewServerManager.class, getViewServerManager());
//    manager.registerSingleton(ModelViewServerManager.class, getModelViewServerManager());
//    manager.registerSingleton(ViewManager.class, getViewManager());
//    manager.registerSingleton(ModelViewManager.class, getModelViewManager());
  }

  private Keywords[] getBusinessSystems(BusinessObjectManager manager) {
    AccessController accessController = manager.getAccessController();
    Locale locale = getSessionManager().getLocale();
    BusinessSystem[] businessSystems = manager.getSystems();
    int sLength = businessSystems.length;
    ArrayList systems = new ArrayList(sLength);

    try {
      for (int i = 0; i < sLength; i++) {
        BusinessSystem system = businessSystems[i];

        if (!system.isAccessControlled() || accessController.isAccessPermitted(system)) {
          BusinessModule[] businessModules = manager.getModules(system);
          int mLength = businessModules.length;
          ArrayList modules = new ArrayList(mLength);

          for (int j = 0; j < mLength; j++) {
            BusinessModule module = businessModules[j];

            if (!module.isAccessControlled() || accessController.isAccessPermitted(module)) {
              BusinessObject[] businessObjects = manager.getObjects(module);
              int oLength = businessObjects.length;
              LinkedList workspaces = new LinkedList();

              for (int k = 0; k < oLength; k++) {
                BusinessObject object = businessObjects[k];

                if (!object.isAccessControlled() || accessController.isAccessPermitted(object)) {
                  BusinessObjectProperty[] properties =
                          manager.getProperties(object, new PropertySelector(Workspace.class));
                  int pLength = properties.length;

                  for (int l = 0; l < pLength; l++) {
                    Workspace workspace = (Workspace) properties[l];

                    workspaces.add(new Keywords(new Object[]{"name", object.getName() + "#" + workspace.getName(),
                                                             "label", workspace.getLabel(locale),
                                                             "documentation", workspace.getDocumentation(locale),
                                                             "icon", workspace.getIcon(IconSize.SMALL),
                                                             "class", workspace.getClassName()}));
                  }
                }
              }

              modules.add(new Keywords("name", module.getName(),
                                       "label", module.getLabel(locale),
                                       "documentation", module.getDocumentation(locale),
                                       "icon", module.getIcon(IconSize.SMALL),
                                       "objects", workspaces.toArray(new Keywords[workspaces.size()])));
            }
          }

          systems.add(new Keywords("name", system.getName(),
                                   "label", system.getLabel(locale),
                                   "documentation", system.getDocumentation(locale),
                                   "icon", system.getIcon(IconSize.SMALL),
                                   "modules", modules.toArray(new Keywords[modules.size()])));


        }
      }

      return (Keywords[]) systems.toArray(new Keywords[systems.size()]);
    } catch (SystemException e) {
      return new Keywords[0];
    }
  }

  public static StandardQueryManager getQueryManager() {
    return (StandardQueryManager)BusinessObjectManager.getBusinessObjectManager().getSingleton(QueryManager.class);
  }

  public static LocalTransactionManager getTransactionManager() {
    return (LocalTransactionManager)BusinessObjectManager.getBusinessObjectManager().getSingleton(TransactionManager.class);
  }

  public static ViewManager getViewManager() {
    return ViewManager.getViewManager();
  }

  public static ViewServerManager getViewServerManager() {
      try {
          return (ViewServerManager)LocalSessionManager.getSessionManager().getSession().getLocal("sessionViewServerManager", ViewServerManager.getViewServerManager());
      }
      catch (Exception e) {
          return ViewServerManager.getViewServerManager();
      }
  }

  public String getComponentProperty(String name) {
    return (String)configuration.getComponentProperty(name);
  }

  public String getComponentProperty(String name, String defaultValue) {
    String componentProperty = (String)configuration.getComponentProperty(name);

    return (componentProperty!=null) ? componentProperty : defaultValue;
  }

    public Configuration getConfiguration() {
        return configuration;
    }
}
