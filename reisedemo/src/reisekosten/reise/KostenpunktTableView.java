package reisekosten.reise;

import com.sdm.quasar.newmodelview.TableView;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.component.ComponentException;
import com.sdm.quasar.view.ViewVisualizer;
import com.sdm.quasar.view.UserInterfaceType;
import com.sdm.quasar.view.ViewManager;
import com.sdm.quasar.util.Assertion;

    import reisekosten.*;
    

public class KostenpunktTableView extends TableView {
    

    public KostenpunktTableView(Keywords arguments) throws ComponentException {
        super(arguments);
    }

    
    public ViewVisualizer makeViewVisualizer(Keywords arguments) {
        UserInterfaceType userInterfaceType = getViewManager().getUserInterfaceType();

        
        if (userInterfaceType == UserInterfaceType.SWING)
            return new reisekosten.reise.swing.KostenpunktTableViewVisualizer(arguments);
        
        if (userInterfaceType == UserInterfaceType.WINGS)
            return new reisekosten.reise.wings.KostenpunktTableViewVisualizer(arguments);
        

        Assertion.fail("User interface type " + userInterfaceType + " not supported by " + this);

        return null;
    }

}
       