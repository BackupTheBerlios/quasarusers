package quasarusers.portal.server;

import java.rmi.RemoteException;
import javax.ejb.EJBHome;
import javax.ejb.CreateException;

/**
 * Created by IntelliJ IDEA.
 * User: schmickl
 * Date: Feb 11, 2003
 * Time: 8:19:27 PM
 * To change this template use Options | File Templates.
 */
public interface QuasarSwingSessionHome extends EJBHome {
  QuasarSwingSession create() throws RemoteException, CreateException;
}
