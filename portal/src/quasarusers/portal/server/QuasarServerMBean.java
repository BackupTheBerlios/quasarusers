package quasarusers.portal.server;

import org.jboss.system.Service;

/**
 * Created by IntelliJ IDEA.
 * User: schmickl
 * Date: Feb 11, 2003
 * Time: 8:09:18 PM
 * To change this template use Options | File Templates.
 */
public interface QuasarServerMBean {
    public String getConfigFile();
    public void setConfigFile(String configFile);
     /**
    * create the service, do expensive operations etc
    */
   void create() throws Exception;

   /**
    * start the service, create is already called
    */
   void start() throws Exception;

   /**
    * stop the service
    */
   void stop();

   /**
    * destroy the service, tear down
    */
   void destroy();
}
