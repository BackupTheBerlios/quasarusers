package quasarusers.util;

import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.continuation.Continuation;
import com.sdm.quasar.businessobject.BusinessObjectManager;
import com.sdm.quasar.view.server.ViewServerManager;
import com.sdm.quasar.util.LocalizedString;
import quasarusers.portal.ConfigurableRuntime;

/**
 * Dies ist die Vaterklasse für ViewDescrptions einfacher Views
 * (nicht ModelViews und nicht DataViews).
 *
 * @author Marco Schmickler
 */
public class SimpleView extends com.sdm.quasar.view.server.SimpleViewDescription {
    public SimpleView(String name,
                      LocalizedString label,
                      LocalizedString documentation,
                      String viewClassName,
                      String viewServerClassName) {
        super(name, label, documentation, viewClassName, viewServerClassName);
    }

    public SimpleView(String name,
                      String label,
                      String documentation,
                      String viewClassName,
                      String viewServerClassName) {
        super(name, label, documentation, viewClassName, viewServerClassName);
    }

    public SimpleView(String name,
                      LocalizedString label,
                      LocalizedString documentation,
                      String viewClassName) {
        super(name, label, documentation, viewClassName);
    }

    public SimpleView(String name,
                      String label,
                      String documentation,
                      String viewClassName) {
        super(name, label, documentation, viewClassName);
    }

    public void open(Object object,
                     Keywords arguments,
                     Continuation continuation) {
        arguments = (Keywords)arguments.clone();

        try {
            arguments.addValue("transaction", BusinessObjectManager.getBusinessObjectManager().getTransactionManager().getTransaction());

            ConfigurableRuntime.getViewServerManager().openView(this, object, arguments, new ContinuationResultProcessor(continuation));
        }
        catch (Exception e) {
            BusinessObjectManager.getBusinessObjectManager().getContinuationManager().continueWithException(e);
        }
    }

    public void open(Object object,
                     Keywords arguments) {
        arguments = (Keywords)arguments.clone();

        try {
            arguments.addValue("transaction", BusinessObjectManager.getBusinessObjectManager().getTransactionManager().getTransaction());

            ConfigurableRuntime.getViewServerManager().openView(this, object, arguments);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
