package quasarusers.util.businessobject;

import com.sdm.quasar.businessobject.AccessController;
import com.sdm.quasar.businessobject.BusinessModule;
import com.sdm.quasar.businessobject.BusinessObject;
import com.sdm.quasar.businessobject.BusinessObjectManager;
import com.sdm.quasar.businessobject.BusinessObjectProperty;
import com.sdm.quasar.businessobject.BusinessSystem;
import com.sdm.quasar.businessobject.PropertySelector;
import com.sdm.quasar.session.implementation.LocalSessionManager;
import com.sdm.quasar.util.registry.RegistryObject;
import com.sdm.quasar.view.quickstart.PortalNode;
import com.sdm.quasar.view.quickstart.server.ServerPortalNode;
import com.sdm.quasar.view.server.ViewServerManager;
import com.sdm.quasar.view.UserInterfaceType;
import quasarusers.portal.SiaPortalNode;
import quasarusers.portal.ConfigurableRuntime;

import javax.transaction.SystemException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Diese Klasse baut den Menübaum enstprechend den Berechtigungen
 * des Benutzers auf.
 *
 * @author Marco Schmickler
 * @author Matthias Rademacher
 */
//todo dpk: 23/01/03 -> MR kommentieren
//todo dpk: 23/01/03 -> toten Code entfernen

public final class NodeGenerator {
  private BusinessObjectManager mBoManager = BusinessObjectManager.getBusinessObjectManager();
  private AccessController mAccessController = mBoManager.getAccessController();
  private Locale mLocale = LocalSessionManager.getSessionManager().getLocale();
  private UserInterfaceType type;

  public NodeGenerator() {
    type = UserInterfaceType.WINGS;
  }

  public NodeGenerator(UserInterfaceType type) {
    this.type = type;
  }

  public void makeSystemNodes(ArrayList viewProperties) throws Exception {
    BusinessSystem[] businessSystems = mBoManager.getSystems();

    for (int i = 0; i < businessSystems.length; i++) {
      RegistryObject businessSystem = businessSystems[i];

      if (accessGranted(businessSystem))
        makeNodes(businessSystem.getLabel(mLocale), businessSystem, viewProperties);
    }
  }

  public void makeNodes(String path, RegistryObject registryObject, ArrayList viewProperties) throws Exception {
    if (registryObject instanceof BusinessObject) {
      makeViewNodes(path, (BusinessObject) registryObject, viewProperties);

      return;
    }

    RegistryObject[] businessComponents = mBoManager.getRegistry().getComponents(registryObject);

    for (int i = 0; i < businessComponents.length; i++) {
      RegistryObject businessComponent = businessComponents[i];

      if (accessGranted(businessComponent))
        makeNodes(path + "|" + businessComponent.getLabel(mLocale), businessComponent, viewProperties);
    }
  }

  public void makeViewNodes(String path, BusinessObject component, ArrayList viewProperties) throws Exception {
    BusinessObjectProperty[] properties
            = mBoManager.getProperties(component, new PropertySelector(ViewProperty.class));
    List functions = new ArrayList(properties.length);

    for (int l = 0; l < properties.length; l++) {
      ViewProperty function = (ViewProperty) properties[l];

      if (!function.isAccessControlled() ||
              mAccessController.isAccessPermitted(component, function)) {
        if (function.getPath() == null)
          function.setPath(path + "|" + function.getLabel(mLocale));
        viewProperties.add(function);
      }
    }
  }

  public PortalNode[] makeSystemNodes() throws Exception {
    Map map = new HashMap();
    ArrayList arrayList = new ArrayList();
    makeSystemNodes(arrayList);
    ArrayList value = new ArrayList();
    value.add("root");
    map.put("", value);

    for (Iterator iterator = arrayList.iterator(); iterator.hasNext();) {
      ViewProperty viewProperty = (ViewProperty) iterator.next();

      if(viewProperty.isSupported(type)) {
          StringTokenizer stringTokenizer = new StringTokenizer(viewProperty.getPath(), "|");
          StringBuffer path = new StringBuffer();
          String lastPath;

          while (stringTokenizer.hasMoreTokens()) {
            lastPath = path.toString();
            String name = stringTokenizer.nextToken();

            path.append(name);
            path.append("|");
            ArrayList lastnode = (ArrayList) map.get(lastPath);

            if (stringTokenizer.hasMoreTokens()) {
              ArrayList node = (ArrayList) map.get(path.toString());

              if (node == null) {
    //          System.out.println("added " + path + "name" + name + " to " + lastnode.get(0));

                map.put(path.toString(), node = new ArrayList());
                node.add(name);
                lastnode.add(node);
              }
            } else
              lastnode.add(new SiaPortalNode(viewProperty));
          }
      }
    }

    PortalNode node = makeNode((ArrayList) map.get(""));

//    BusinessSystem[] businessSystems = mBoManager.getSystems();
//    List systems = new ArrayList(businessSystems.length);
//
//    for (int i = 0; i < businessSystems.length; i++) {
//      RegistryObject businessSystem = businessSystems[i];
//
//      if (accessGranted(businessSystem)) {
//        systems.add(new ServerPortalNode(businessSystem.getLabel(mLocale),
//                                   null, ViewServerManager.getViewServerManager(),
//                                   makeNodes(businessSystem)));
//      }
//    }

    return node.getChildren();
  }

  private PortalNode makeNode(Object o) {
    if (o instanceof SiaPortalNode)
      return (PortalNode) o;

    List list = (List) o;
    Iterator iterator = list.iterator();
    String name = (String) iterator.next();
    PortalNode[] children = new PortalNode[list.size() - 1];

    for (int i = 0; iterator.hasNext(); i++) {
      children[i] = makeNode(iterator.next());
    }

    return new ServerPortalNode(name, null, ConfigurableRuntime.getViewServerManager(), children);
  }

  public PortalNode[] makeNodes(RegistryObject registryObject) throws Exception {
    if (registryObject instanceof BusinessObject)
      return makeViewNodes((BusinessObject) registryObject);

    RegistryObject[] businessComponents = mBoManager.getRegistry().getComponents(registryObject);
    List components = new ArrayList(businessComponents.length);

    for (int i = 0; i < businessComponents.length; i++) {
      RegistryObject businessComponent = (RegistryObject) businessComponents[i];

      if (accessGranted(businessComponent))
        components.add(new ServerPortalNode(businessComponent.getLabel(mLocale),
                                            null, ConfigurableRuntime.getViewServerManager(),
                                            makeNodes(businessComponent)));
    }

    return toArray(components);
  }

  public PortalNode[] makeViewNodes(BusinessObject component) throws Exception {
    BusinessObjectProperty[] properties
            = mBoManager.getProperties(component, new PropertySelector(ViewProperty.class));
    List functions = new ArrayList(properties.length);

    for (int l = 0; l < properties.length; l++) {
      ViewProperty function = (ViewProperty) properties[l];

      if (!function.isAccessControlled() ||
              mAccessController.isAccessPermitted(component, function)) {
        functions.add(new SiaPortalNode(function));
      }
    }

    return toArray(functions);
  }

  private boolean isAccessControlled(RegistryObject businessComponent) {
    // Designfehler in Jeff: Die berechtigungsrelevanten RegistryObjects
    // haben keine gemeinsame Vaterklasse !
    if (businessComponent instanceof BusinessObject)
      return ((BusinessObject) businessComponent).isAccessControlled();
    else if (businessComponent instanceof BusinessModule)
      return ((BusinessModule) businessComponent).isAccessControlled();
    else if (businessComponent instanceof BusinessSystem)
      return ((BusinessSystem) businessComponent).isAccessControlled();
    else
      return false;
  }

  private boolean isAccessPermitted(RegistryObject businessComponent) throws SystemException {
    // Designfehler in Jeff: Die berechtigungsrelevanten RegistryObjects
    // haben keine gemeinsame Vaterklasse !
    if (businessComponent instanceof BusinessObject)
      return mAccessController.isAccessPermitted((BusinessObject) businessComponent);
    else if (businessComponent instanceof BusinessModule)
      return mAccessController.isAccessPermitted((BusinessModule) businessComponent);
    else if (businessComponent instanceof BusinessSystem)
      return mAccessController.isAccessPermitted((BusinessSystem) businessComponent);
    else
      return true;
  }

  private boolean accessGranted(RegistryObject registryObject) throws SystemException {
    return !isAccessControlled(registryObject) || isAccessPermitted(registryObject);
  }

  private PortalNode[] toArray(List systems) {
    return (PortalNode[]) systems.toArray(new PortalNode[systems.size()]);
  }
}
