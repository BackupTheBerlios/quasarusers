package reisekosten.reise;
import com.sdm.quasar.newmodelview.ObjectView;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.component.ComponentException;
import com.sdm.quasar.view.ViewVisualizer;
import com.sdm.quasar.view.UserInterfaceType;
import com.sdm.quasar.view.ViewManager;
import com.sdm.quasar.util.Assertion;

    import reisekosten.*;
    

public class ReiseView extends ObjectView {
    

    public ReiseView(Keywords arguments) throws ComponentException {
        super(arguments);
    }

    
    protected void initialize(Keywords arguments) {
        arguments.addValue("name", "Reise");
        arguments.addValue("objectModel", "RemisBS.ReiseComponent");
        arguments.addValue("serverClass", "com.sdm.quasar.newmodelview.server.ObjectViewServer");

        super.initialize(arguments);
    }
    
    public void buildStructure(Keywords arguments) throws ComponentException {
        addChildComponent(getViewManager().makeView(KostenpunktTableView.class, new Keywords("name", "Kostenpunkt")));
        
    }
    
    public ViewVisualizer makeViewVisualizer(Keywords arguments) {
        UserInterfaceType userInterfaceType = getViewManager().getUserInterfaceType();

        
        if (userInterfaceType == UserInterfaceType.SWING)
            return new reisekosten.reise.swing.ReiseViewVisualizer(arguments);
        
        if (userInterfaceType == UserInterfaceType.WINGS)
            return new reisekosten.reise.wings.ReiseViewVisualizer(arguments);
        

        Assertion.fail("User interface type " + userInterfaceType + " not supported by " + this);

        return null;
    }

}
       