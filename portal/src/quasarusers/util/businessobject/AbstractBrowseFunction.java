package quasarusers.util.businessobject;

import com.sdm.quasar.businessobject.AbstractBusinessObjectFunction;
import com.sdm.quasar.businessobject.BusinessObject;
import com.sdm.quasar.businessobject.BusinessObjectFunction;
import com.sdm.quasar.businessobject.ReturnMode;
import com.sdm.quasar.businessobject.TransactionMode;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.session.Task;
import com.sdm.quasar.util.LocalizedString;

import javax.transaction.SystemException;

//todo dpk: 23/01/03 -> MR kommentieren

public abstract class AbstractBrowseFunction extends AbstractBusinessObjectFunction {
  public AbstractBrowseFunction(Class entityType, String name, LocalizedString label, LocalizedString documentation,
                                TransactionMode transactionMode, ReturnMode returnMode) {
    super(entityType, name, label, documentation, transactionMode, returnMode, true);
  }

  public AbstractBrowseFunction(Class entityType, String name, String label, String documentation,
                                TransactionMode transactionMode, ReturnMode returnMode) {
    super(entityType, name, label, documentation, transactionMode, returnMode, true);
  }

  protected Task makeTask(BusinessObject object) {
    try {
      return new BrowseTask(object, this);
    } catch (SystemException e) {
      return null;
    }
  }

  public abstract void browse(Keywords arguments);

  public BusinessObjectFunction.CallMethod getCallMethod(BusinessObject businessObject, Object[] arguments) {
    return new AbstractCallMethod() {
      public final Object call(BusinessObject object, BusinessObjectFunction function, Object[] arguments) throws Exception {
        ((AbstractBrowseFunction) function).browse((Keywords) arguments[0]);
        return null;
      }
    };
  }

}
