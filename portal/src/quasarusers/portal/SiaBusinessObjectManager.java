/*
 * Created by IntelliJ IDEA.
 * User: rademach
 * Date: 13.09.2002
 * Time: 11:51:47
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package quasarusers.portal;

import com.sdm.quasar.businessobject.AccessController;
import com.sdm.quasar.businessobject.BusinessObject;
import com.sdm.quasar.businessobject.BusinessObjectManager;
import com.sdm.quasar.businessobject.BusinessObjectNotFoundException;
import com.sdm.quasar.businessobject.ThreadPoolAdapter;

import java.util.StringTokenizer;

/**
 * Dies ist der BusinessObjectManager für REMIS. Im Gegensatz zu
 * der Standardimplementierung arbeitet er mit einer zweistufigen
 * Hierarchie der Business Objectsm d.h.:
 *  Business System - Business Object.
 * Dies entspricht dem zweistufigen Menü der Anwendung.
 *
 * @author Matthias Rademacher
 */
public final class SiaBusinessObjectManager extends BusinessObjectManager {
  public SiaBusinessObjectManager(AccessController accessController, ThreadPoolAdapter threadPoolAdapter) {
    super(accessController, threadPoolAdapter);
  }

  public BusinessObject getObject(String name) throws BusinessObjectNotFoundException {
    try {
      // Abweichend vom Standard haben wir die Struktur "BusinessSystem.BusinessObject".
      // Der Standard ist "BusinessSystem.BusinessModule.BusinessObject"

      StringTokenizer st = new StringTokenizer(name, ".");
      String systemName = st.nextToken();
      String objectName = st.nextToken();

      if (st.hasMoreTokens()) // Zu viele Punkte im Namen
        throw new Exception();

      return (BusinessObject) getRegistry().getComponent(getSystem(systemName),
                                                         objectName);

    } catch (Exception e) {
      throw new BusinessObjectNotFoundException("Business object " + name + " not found");
    }
  }
}
