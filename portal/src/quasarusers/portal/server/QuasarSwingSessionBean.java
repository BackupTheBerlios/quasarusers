package quasarusers.portal.server;

import com.sdm.quasar.businessobject.*;
import com.sdm.quasar.component.CommandController;
import com.sdm.quasar.component.ComponentException;
import com.sdm.quasar.component.StandardCommandControllerManager;
import com.sdm.quasar.component.SynchronousCommandPerformer;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.lang.Symbol;
import com.sdm.quasar.newmodelview.server.*;
import com.sdm.quasar.newmodelview.server.adapter.StandardTransactionAdapter;
import com.sdm.quasar.newmodelview.server.model.ObjectModel;
import com.sdm.quasar.session.InvalidSessionException;
import com.sdm.quasar.session.Session;
import com.sdm.quasar.session.SessionManager;
import com.sdm.quasar.session.SessionType;
import com.sdm.quasar.view.ResultProcessor;
import com.sdm.quasar.view.server.*;
import com.sdm.quasar.view.serveradapter.communication.CommunicationViewGetter;
import quasarusers.portal.businessobject.ObjectModelProperty;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.transaction.SystemException;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: schmickl
 * Date: Feb 11, 2003
 * Time: 8:19:27 PM
 * To change this template use Options | File Templates.
 */
public class QuasarSwingSessionBean implements SessionBean {
  private ViewServerManager viewServerManager;
  private Session session;

  public QuasarSwingSessionBean() {
  }

  public void ejbActivate() throws EJBException {
  }

  public void ejbPassivate() throws EJBException {
  }

  public void ejbRemove() throws EJBException {
  }

  public void setSessionContext(SessionContext sessionContext) throws EJBException {
  }

    public void ejbCreate() throws CreateException {
        QuasarServer.getQuasarServer();

        viewServerManager = new ViewServerManager(new Keywords("delegate", new StandardCommandControllerManager(),
                "commandPerformer", new Object[] { null, new SynchronousCommandPerformer(),
                                                   ObjectViewServer.class, new ObjectViewServerCommandPerformer(),
                                                   AbstractViewServer.class, new ViewServerCommandPerformer() },
                "viewOpener", new DummyServerViewOpener(),
                "viewGetter", new CommunicationViewGetter()));

        new StandardModelViewServerManager(viewServerManager) {
            public TransactionAdapter makeTransactionAdapter(ObjectViewServer objectViewServer, Keywords arguments) {
                return new StandardTransactionAdapter(objectViewServer, QuasarServer.getQuasarServer().getTransactionManager());
            }

            public ModelManager getModelManager() {
                if (modelManager == null)
                    modelManager = new BusinessObjectModelManager();

                return modelManager;
            }

        };
    }

  public Keywords startViewServer(Keywords arguments) throws ComponentException {
    resumeSession();

    ViewServer viewServer = viewServerManager.makeViewServer((String)arguments.getValue("controllerClass"), arguments);

    arguments.removeValue("controllerClass");

    Keywords viewArguments = viewServer.computeRemoteArguments(arguments);

    return viewArguments;
  }

  public void stopViewServer(Keywords arguments) throws ComponentException {
    resumeSession();

    CommandController viewServer = viewServerManager.getCommandController((Serializable)arguments.getValue("originalController"));

    viewServer.shutdown(arguments);
  }

  public Object performCommand(Keywords arguments) throws ComponentException {
    resumeSession();

    CommandController controller = viewServerManager.getCommandController((Serializable)arguments.getValue("receiver"));
    Symbol command = (Symbol)arguments.getValue("command");

    arguments.removeValue("receiver");
    arguments.removeValue("command");

    return controller.performCommand(command, arguments);
  }

  public boolean login(String username, String password) {
    SessionManager sessionManager = QuasarServer.getQuasarServer().getSessionManager();

    try {
      sessionManager.open(username, password,SessionType.INTERACTIVE);
      session = sessionManager.getSession();
    }
    catch (Exception e) {
      return false;
    }

    return true;
  }

  private void resumeSession() {
    SessionManager sessionManager = QuasarServer.getQuasarServer().getSessionManager();

    try {
      if (sessionManager.getSession() != session) {
        sessionManager.suspend();

        if (session != null)
          sessionManager.resume(session);
      }
    }
    catch (SystemException e) {
      // ignore
    }
    catch (IllegalStateException e) {
      e.printStackTrace();  //To change body of catch statement use Options | File Templates.
    }
    catch (InvalidSessionException e) {
      e.printStackTrace();  //To change body of catch statement use Options | File Templates.
    }
    catch (SecurityException e) {
      e.printStackTrace();  //To change body of catch statement use Options | File Templates.
    }
  }

    public Object[] performUseCase(Keywords arguments) throws BusinessObjectException {
        resumeSession();

        String name = (String)arguments.getValue("businessObject");

        BusinessObjectManager businessObjectManager = BusinessObjectManager.getBusinessObjectManager();

        BusinessObject object = businessObjectManager.getObject(name);

        String functionname = (String)arguments.getValue("functionName");

        BusinessObjectFunction function;

        if (functionname == null) {
            FunctionSelector functionselector = (FunctionSelector)arguments.getValue("functionSelector");

            BusinessObjectFunction[] functions = object.getFunctions(functionselector);

            if (functions.length > 1) {
                String[] functionnames = new String[functions.length];

                for (int i = 0; i < functions.length; i++)
                    functionnames[i] = functions[i].getName();

                return functionnames;
            }
            else {
                if (functions.length == 0)
                    throw new BusinessObjectPropertyNotFoundException();

                function = functions[0];
            }
        }
        else
            function = object.getFunction(functionname);

        Object result = object.getController().call(function, (Object[])arguments.getValue("arguments"));

        if (result instanceof Object[])
            return (Object[])result;

        return new Object[] { result };
    }

    private static class DummyServerViewOpener implements ServerViewOpener {
    public void openView(String view, Keywords arguments) throws ComponentException {
      throw new UnsupportedOperationException("no bidirectional communication");
    }

    public Object openViewAndWait(String view, Keywords arguments) throws ComponentException {
      throw new UnsupportedOperationException("no bidirectional communication");
    }

    public void openAlertDialog(String message, boolean error) throws ComponentException {
      throw new UnsupportedOperationException("no bidirectional communication");
    }

    public void openAlertDialog(String message, boolean error, ResultProcessor resultProcessor) {
      throw new UnsupportedOperationException("no bidirectional communication");
    }

    public String openInputDialog(String message) throws ComponentException {
      throw new UnsupportedOperationException("no bidirectional communication");
    }

    public void openInputDialog(String message, ResultProcessor resultProcessor) {
      throw new UnsupportedOperationException("no bidirectional communication");
    }

    public int openConfirmDialog(String message, boolean cancel) throws ComponentException {
      throw new UnsupportedOperationException("no bidirectional communication");
    }

    public void openConfirmDialog(String message, boolean cancel, ResultProcessor resultProcessor) {
      throw new UnsupportedOperationException("no bidirectional communication");
    }

    public String openChoiceDialog(String message, String[] choices,
                                   String defaultChoice) throws ComponentException {
      throw new UnsupportedOperationException("no bidirectional communication");
    }

    public void openChoiceDialog(String message, String[] choices, String defaultChoice,
                                 ResultProcessor resultProcessor) {
      throw new UnsupportedOperationException("no bidirectional communication");
    }
  }

    private static class BusinessObjectModelManager implements ModelManager {
        public ObjectModel getObjectModel(String name) {
            BusinessObjectManager businessObjectManager = BusinessObjectManager.getBusinessObjectManager();

            try {
                BusinessObject businessObject = businessObjectManager.getObject(name);

                BusinessObjectProperty[] objectModels = (BusinessObjectProperty[])businessObject.getProperties(new PropertySelector(ObjectModelProperty.class));
                int eLength = objectModels.length;

                for (int i = 0; i < eLength; i++) {
                    ObjectModelProperty objectModel = (ObjectModelProperty)objectModels[i];

                    return objectModel.getObjectModel();
                }

                return null;
            }
            catch (BusinessObjectNotFoundException e) {
                throw new RuntimeException("BusinessObject " + name + " not found");
            }
        }

        public void registerObjectModel(String name, ObjectModel objectModel) {
            System.out.println("registered...");
        }
    }
}
