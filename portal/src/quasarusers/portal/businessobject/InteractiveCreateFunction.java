package quasarusers.portal.businessobject;

import com.sdm.quasar.businessobject.AbstractCreateFunction;
import com.sdm.quasar.businessobject.TransactionMode;
import com.sdm.quasar.businessobject.ReturnMode;
import com.sdm.quasar.lang.Keywords;
import quasarusers.portal.client.businessobject.OpenViewCall;

/**
 * Created by IntelliJ IDEA.
 * User: schmickl
 * Date: Feb 16, 2003
 * Time: 4:20:50 PM
 * To change this template use Options | File Templates.
 */
public class InteractiveCreateFunction extends AbstractCreateFunction {
    private String viewClass;

    public InteractiveCreateFunction(Class entityType, String name, String label, String documentation, String viewClass) {
        super(entityType, name, label, documentation, TransactionMode.OPTIONAL, ReturnMode.DIRECT);

        this.viewClass = viewClass;
    }

    public Object[] create(Keywords arguments) throws Exception {
        return new Object[] { new Keywords("view", viewClass, "new", Boolean.TRUE), new OpenViewCall()};
    }
}
