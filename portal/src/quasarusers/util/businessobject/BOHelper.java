/*
 * Author: fweber
 * Date: Feb 20, 2002
 * Time: 5:01:45 PM
 */

package quasarusers.util.businessobject;

import com.sdm.quasar.businessobject.AccessController;
import com.sdm.quasar.businessobject.BusinessModule;
import com.sdm.quasar.businessobject.BusinessObject;
import com.sdm.quasar.businessobject.BusinessObjectController;
import com.sdm.quasar.businessobject.BusinessObjectFunction;
import com.sdm.quasar.businessobject.BusinessObjectManager;
import com.sdm.quasar.businessobject.BusinessObjectProperty;
import com.sdm.quasar.businessobject.BusinessSystem;
import com.sdm.quasar.businessobject.PropertySelector;
import com.sdm.quasar.businessobject.entityview.EntityView;
import com.sdm.quasar.continuation.AbstractContinuation;
import com.sdm.quasar.continuation.Continuation;
import com.sdm.quasar.continuation.ContinuationManager;
import com.sdm.quasar.continuation.StandardContinuationManager;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.persistence.PersistenceException;
import com.sdm.quasar.persistence.Persistent;
import com.sdm.quasar.persistence.PersistentLockTimeoutException;
import com.sdm.quasar.persistence.PersistentNotFoundException;
import com.sdm.quasar.persistence.Pool;
import com.sdm.quasar.session.implementation.LocalSessionManager;
import com.sdm.quasar.util.NamedObject;
import com.sdm.quasar.view.implementation.wings.WingsApplication;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.transaction.SystemException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;

//todo dpk: 23/01/03 -> MR kommentieren

public class BOHelper {

  private static BOHelper uniqueInstance = null;

  private BOHelper() {
  }

  public static BOHelper getBOHelper() {
    if (uniqueInstance == null)
      uniqueInstance = new BOHelper();

    return uniqueInstance;
  }

  public void openEntityView(BusinessObject businessObject,
                             Class entityType,
                             EntityView view,
                             final Object object,
                             Keywords arguments) {
    final BusinessObjectManager businessObjectManager = BusinessObjectManager.getBusinessObjectManager();
    ContinuationManager continuationManager = businessObjectManager.getContinuationManager();

    Continuation continuation =
            new AbstractContinuation(continuationManager) {
              public void continueWithResult(Object result) {
                Object[] objects = (Object[]) result;

                if (objects.length == 0)
                  super.continueWithResult((object == null) ? null : Boolean.FALSE);
                else
                  super.continueWithResult((object == null) ?
                                           (Object) ((Persistent) objects[0]).getPrimaryKey() :
                                           (Object) Boolean.TRUE);
              }
            };

    try {
      arguments = new Keywords(arguments,
                               "transaction", businessObjectManager.getTransactionManager().getTransaction());

      if (object != null)
        try {
          arguments.addValue("lock", Boolean.TRUE);
          arguments.addValue("load", Boolean.TRUE);
          Pool pool = (Pool) businessObjectManager.getSingleton(Pool.class);
          businessObject.getController().open(view, pool.lookup(entityType, (Object[]) object), arguments, continuation);
        } catch (PersistenceException e) {
          continuationManager.continueWithResult(Boolean.FALSE);
        }
      else
        businessObject.getController().open(view, object, arguments, continuation);
    } catch (Exception e) {
      continuationManager.continueWithException(e);
    }
  }

  public boolean delete(Class entityType, Object object) throws PersistenceException {
    Pool pool = (Pool) BusinessObjectManager.getBusinessObjectManager().getSingleton(Pool.class);

    try {
      pool.lookup(entityType, (Object[]) object).delete();

      return true;
    } catch (PersistentNotFoundException e) {
      return false;
    } catch (PersistentLockTimeoutException e) {
      return false;
    }
  }

  public void callBusinessObjectFunction(BusinessObject businessObject, BusinessObjectFunction businessObjectFunction) {
    try {
      BusinessObjectController businessObjectController = businessObject.getController();
      ContinuationManager continuationManager = StandardContinuationManager.getContinuationManager();

      businessObjectController.call(businessObjectFunction, new Object[]{new Keywords()},
                                    new AbstractContinuation(continuationManager) {
                                      public void continueWithResult(Object result) {
                                        System.out.println("################## performed");
                                        WingsApplication.getApplication().resumeReload();
                                      }

                                      public void continueWithException(Exception exception) {
                                        System.out.println("################## exception");
                                        exception.printStackTrace();
                                        WingsApplication.getApplication().resumeReload();
                                      }
                                    });
      WingsApplication.getApplication().suspendReload();
    } catch (Exception exception) {
      exception.printStackTrace(); // ???
    }
  }

  public static class NamedNode extends DefaultMutableTreeNode {
    public NamedNode(Object userObject) {
      super(userObject);
    }

    public String toString() {
      return ((NamedObject) getUserObject()).getLabel(LocalSessionManager.getSessionManager().getLocale());
    }
  }

  public Object registerSystem(Object parent, BusinessSystem system) {
    DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parent;
    NamedNode newNode = new NamedNode(system);

    parentNode.add(newNode);

    return newNode;
  }

  public Object registerModule(Object parent, BusinessModule module) {
    NamedNode parentNode = (NamedNode) parent;
    NamedNode newNode = new NamedNode(module);

    parentNode.add(newNode);

    return newNode;
  }

  public Object registerObject(Object parent, BusinessObject object) {
    NamedNode parentNode = (NamedNode) parent;
    NamedNode newNode = new NamedNode(object);

    parentNode.add(newNode);

    return newNode;
  }

  public Object registerProperty(Object parent, ViewProperty function) {
    NamedNode parentNode = (NamedNode) parent;
    NamedNode newNode = new NamedNode(function);

    parentNode.add(newNode);

    return newNode;
  }

  public void registerBusinessSystems(Object root) {
    BusinessObjectManager manager = BusinessObjectManager.getBusinessObjectManager();
    AccessController accessController = manager.getAccessController();
    Locale locale = LocalSessionManager.getSessionManager().getLocale();
    BusinessSystem[] businessSystems = manager.getSystems();
    int sLength = businessSystems.length;

    try {
      for (int i = 0; i < sLength; i++) {
        BusinessSystem system = businessSystems[i];

        if (!system.isAccessControlled() || accessController.isAccessPermitted(system)) {
          Object registeredSystem = registerSystem(root, system);
          BusinessModule[] businessModules = manager.getModules(system);
          int mLength = businessModules.length;
          ArrayList modules = new ArrayList(mLength);

          for (int j = 0; j < mLength; j++) {
            BusinessModule module = businessModules[j];

            if (!module.isAccessControlled() || accessController.isAccessPermitted(module)) {
              Object registeredModule = registerModule(registeredSystem, module);
              BusinessObject[] businessObjects = manager.getObjects(module);
              int oLength = businessObjects.length;
              LinkedList workspaces = new LinkedList();

              for (int k = 0; k < oLength; k++) {
                BusinessObject object = businessObjects[k];

                if (!object.isAccessControlled() || accessController.isAccessPermitted(object)) {
                  Object registeredObject = registerObject(registeredModule, object);
                  BusinessObjectProperty[] properties =
                          manager.getProperties(object, new PropertySelector(ViewProperty.class));
                  int pLength = properties.length;

                  for (int l = 0; l < pLength; l++) {
                    ViewProperty function = (ViewProperty) properties[l];

                    registerProperty(registeredObject, function);
                  }
                }
              }
            }
          }
        }
      }
    } catch (SystemException e) {
    }
  }
}
