package quasarusers.portal.businessobject;

import quasarusers.util.businessobject.ViewProperty;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.businessobject.FunctionSelector;

/**
 * Created by IntelliJ IDEA.
 * User: schmickl
 * Date: Feb 16, 2003
 * Time: 4:26:46 PM
 * To change this template use Options | File Templates.
 */
public class FunctionViewProperty extends ViewProperty {
    public FunctionViewProperty(String name, String label, String documentation, String businessobject, Class function, Class type) {
        super(name, label, documentation, new Keywords("functioncall", "de.sdm.sia.remis.portal.client.businessobject.BusinessFunctionCall",
                           "businessObject", businessobject,
                           "functionSelector", new FunctionSelector(function, type, null, true),
                           "arguments", new Object[] {new Keywords()}
              ), true);
    }

    public FunctionViewProperty(String name, String label, String documentation, String businessobject, String functionName) {
        super(name, label, documentation, new Keywords("functioncall", "de.sdm.sia.remis.portal.client.businessobject.BusinessFunctionCall",
                           "businessObject", businessobject,
                           "functionName", functionName,
                           "arguments", new Object[] {new Keywords()}
              ), true);
    }
}
