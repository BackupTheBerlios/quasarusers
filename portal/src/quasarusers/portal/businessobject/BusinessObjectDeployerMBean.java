package quasarusers.portal.businessobject;

/**
 * Created by IntelliJ IDEA.
 * User: schmickl
 * Date: Feb 15, 2003
 * Time: 6:12:39 PM
 * To change this template use Options | File Templates.
 */
public interface BusinessObjectDeployerMBean {
    public String getResource();

    public void setResource(String resource);

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
