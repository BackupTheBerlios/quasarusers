package quasarusers.portal.businessobject;

import quasarusers.util.businessobject.AbstractBrowseFunction;
import quasarusers.portal.client.businessobject.OpenViewCall;
import com.sdm.quasar.businessobject.TransactionMode;
import com.sdm.quasar.businessobject.ReturnMode;
import com.sdm.quasar.businessobject.BusinessObjectFunction;
import com.sdm.quasar.businessobject.BusinessObject;
import com.sdm.quasar.lang.Keywords;

/**
 * Created by IntelliJ IDEA.
 * User: schmickl
 * Date: Feb 16, 2003
 * Time: 4:42:22 PM
 * To change this template use Options | File Templates.
 */
public class InteractiveBrowseFunction extends AbstractBrowseFunction {
    private String viewClass;

    public InteractiveBrowseFunction(Class entityType, String name, String label, String documentation, String viewClass) {
        super(entityType, name, label, documentation, TransactionMode.OPTIONAL, ReturnMode.DIRECT);

        this.viewClass = viewClass;
    }

    public void browse(Keywords arguments) {
    }

  public BusinessObjectFunction.CallMethod getCallMethod(BusinessObject businessObject, Object[] arguments) {
    return new AbstractCallMethod() {
      public final Object call(BusinessObject object, BusinessObjectFunction function, Object[] arguments) throws Exception {
          Keywords keywords = (Keywords)arguments[0];

          keywords.addValue("view", viewClass);

          return new Object[] { keywords, new OpenViewCall() };
      }
    };
  }
}
