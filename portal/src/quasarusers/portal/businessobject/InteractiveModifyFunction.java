package quasarusers.portal.businessobject;

import com.sdm.quasar.businessobject.*;
import com.sdm.quasar.lang.Keywords;
import quasarusers.portal.client.businessobject.OpenViewCall;

/**
 * Created by IntelliJ IDEA.
 * User: schmickl
 * Date: Feb 16, 2003
 * Time: 4:20:50 PM
 * To change this template use Options | File Templates.
 */
public class InteractiveModifyFunction extends AbstractModifyFunction {
    private String viewClass;

    public InteractiveModifyFunction(Class entityType, String name, String label, String documentation, String viewClass) {
        super(entityType, name, label, documentation, TransactionMode.OPTIONAL, ReturnMode.DIRECT);

        this.viewClass = viewClass;
    }

  public BusinessObjectFunction.CallMethod getCallMethod(BusinessObject businessObject, Object[] arguments) {
    return new AbstractCallMethod() {
      public final Object call(BusinessObject businessObject, BusinessObjectFunction function, Object[] arguments) throws Exception {
          Keywords keywords = (Keywords)arguments[0];
          Object object = keywords.getValue("object");

          return new Object[] { new Keywords("view", viewClass, "object", object, "load", Boolean.TRUE), new OpenViewCall()};
      }
    };
  }
}
