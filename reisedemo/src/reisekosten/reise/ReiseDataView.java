package reisekosten.reise;

import com.sdm.quasar.businessobject.extentview.ExtentView;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.lang.Symbol;
import com.sdm.quasar.component.ComponentException;
import com.sdm.quasar.component.NoSuchCommandException;
import com.sdm.quasar.view.ViewVisualizer;
import com.sdm.quasar.view.UserInterfaceType;
import com.sdm.quasar.util.Assertion;
import reisekosten.reise.server.ReiseDataViewServer;

/**
 * Created by IntelliJ IDEA.
 * User: schmickl
 * Date: 22.02.2003
 * Time: 19:36:10
 * To change this template use Options | File Templates.
 */
public class ReiseDataView extends ExtentView {
    public static final Symbol C_PRINT = Symbol.forName("print");

    public ReiseDataView(Keywords arguments) throws ComponentException {
        super(arguments);
    }

    protected Keywords computeViewServerArguments(Keywords arguments) {
        Keywords keywords = super.computeViewServerArguments(arguments);

        keywords.setValue("businessObject", "RemisBS.ReiseComponent");
        keywords.setValue("entityType", "reisekosten.Reise");
        keywords.setValue("serverClass", "de.sdm.sia.remis.reise.server.ReiseDataViewServer");

        return keywords;
    }

    public ViewVisualizer makeViewVisualizer(Keywords arguments) {
        UserInterfaceType userInterfaceType = getViewManager().getUserInterfaceType();


        if (userInterfaceType == UserInterfaceType.SWING)
            return new reisekosten.reise.swing.ReiseDataViewVisualizer(arguments);
        if (userInterfaceType == UserInterfaceType.WINGS)
            return new reisekosten.reise.wings.ReiseDataViewVisualizer(arguments);

        Assertion.fail("User interface type " + userInterfaceType + " not supported by " + this);

        return null;
    }

    public void buildCommands() {
        super.buildCommands();

        useViewServerCommand(ReiseDataViewServer.C_PRINT);
    }

    public boolean editObject(Keywords arguments) throws ComponentException {
        Object selectedObject = getSelectedObject(arguments);
        arguments.removeValue("index");

        Object[] primaryKey = computePrimaryKey(selectedObject);

        getViewManager().openViewAndWait(ReiseView.class, new Keywords("object", primaryKey, "lock", Boolean.TRUE, "edit", Boolean.TRUE));

        runQuery(arguments);

        return true;
    }

    protected void updateCommandSetup() {
        super.updateCommandSetup();

        try {
            setCommandEnabled(C_PRINT, true);
            setCommandEnabled(EDIT, true);
        }
        catch (NoSuchCommandException e) {
            // Cannot happen...

            Assertion.fail("Internal error detected");
        }
    }
}
