/*
 * Created by IntelliJ IDEA.
 * User: rademach
 * Date: 14.08.2002
 * Time: 12:00:48
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package quasarusers.util.businessobject;

import com.sdm.quasar.businessobject.AbstractUseCase;
import com.sdm.quasar.businessobject.BusinessObject;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.session.Session;
import com.sdm.quasar.session.SessionType;
import com.sdm.quasar.session.implementation.LocalSessionManager;

/**
 * Diese Klasse führt einen Use Case aus.
 *
 * @author Matthias Rademacher
 */
public class UseCaseExecutor {
  public UseCaseExecutor(BusinessObject businessObject, AbstractUseCase useCase, boolean background) {
    String name = businessObject.getName() + "." + useCase.getName();

    System.out.println("------ Start " + name + " ------ ");

    try {
      Session session = null;

      if (background) {
        LocalSessionManager sessionManager = LocalSessionManager.getSessionManager();

        sessionManager.open("system", "", SessionType.BACKGROUND);

        session = sessionManager.getSession();
      }

      businessObject.getController().run(useCase, Keywords.NONE);

      if (background) {
        session.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      System.out.println("------ End " + name + " ------ ");
    }
  }
}
