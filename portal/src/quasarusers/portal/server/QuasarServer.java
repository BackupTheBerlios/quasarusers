package quasarusers.portal.server;

import org.jboss.system.ServiceMBeanSupport;
import quasarusers.portal.ConfigurableRuntime;
import quasarusers.portal.SimpleAuthorizationConfiguration;
import com.sdm.quasar.view.server.ViewServerManager;
import com.sdm.quasar.session.SessionManager;
import com.sdm.quasar.persistence.query.QueryManager;
import com.sdm.quasar.persistence.Pool;

import javax.transaction.TransactionManager;

/**
 * Created by IntelliJ IDEA.
 * User: schmickl
 * Date: Feb 11, 2003
 * Time: 8:09:05 PM
 * To change this template use Options | File Templates.
 */
public class QuasarServer extends ServiceMBeanSupport implements QuasarServerMBean {
  private ConfigurableRuntime runtime;
  private String configFile = "serverconfiguration.xml";
  static private QuasarServer quasarServer;

  public QuasarServer() {
    quasarServer = this;

    System.out.println("create");

    runtime = new ConfigurableRuntime(getConfigFile(), new SimpleAuthorizationConfiguration());
  }

  public String getConfigFile() {
    return configFile;
  }

  public void setConfigFile(String configFile) {
    this.configFile = configFile;
  }

  public static QuasarServer getQuasarServer() {
    if (quasarServer == null)
        return new QuasarServer();

    return quasarServer;
  }

  public QueryManager getQueryManager() {
    return (QueryManager) runtime.getConfiguration().getSingleton(QueryManager.class);
  }

  public SessionManager getSessionManager() {
    return (SessionManager) runtime.getConfiguration().getSingleton(SessionManager.class);
  }

  public TransactionManager getTransactionManager() {
      return (TransactionManager) runtime.getConfiguration().getSingleton(TransactionManager.class);
  }

  public Pool getPool() {
      return (Pool) runtime.getConfiguration().getSingleton(Pool.class);
  }
}
