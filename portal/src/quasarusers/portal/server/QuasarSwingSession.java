package quasarusers.portal.server;

import java.rmi.RemoteException;
import java.io.Serializable;
import javax.ejb.EJBObject;

import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.view.View;
import com.sdm.quasar.view.server.ViewServerInternalException;
import com.sdm.quasar.component.ComponentException;
import com.sdm.quasar.component.CommandController;
import com.sdm.quasar.businessobject.BusinessObjectException;

/**
 * Created by IntelliJ IDEA.
 * User: schmickl
 * Date: Feb 11, 2003
 * Time: 8:19:27 PM
 * To change this template use Options | File Templates.
 */
public interface QuasarSwingSession extends EJBObject {
    public Keywords startViewServer(Keywords arguments) throws RemoteException, ComponentException;
    public void stopViewServer(Keywords arguments) throws ComponentException, RemoteException;

    public Object performCommand(Keywords arguments) throws ComponentException, RemoteException;

    public Object[] performUseCase(Keywords arguments) throws RemoteException, BusinessObjectException;

    public boolean login(String username, String password) throws RemoteException;
}
