package quasarusers.util.businessobject;

import javax.transaction.SystemException;

import com.sdm.quasar.businessobject.BusinessObjectFunctionTask;
import com.sdm.quasar.businessobject.BusinessObject;
import com.sdm.quasar.businessobject.BusinessObjectFunction;

//todo dpk: 23/01/03 -> MR kommentieren

public class BrowseTask extends BusinessObjectFunctionTask {
  public BrowseTask(BusinessObject object, BusinessObjectFunction function) throws SystemException {
    super(object, function);
  }
}
