package reisekosten.reise.wings;

import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.view.ViewVisualizer;
import com.sdm.quasar.view.View;
import com.sdm.quasar.newmodelview.implementation.wings.WingsObjectViewVisualizer;

    import reisekosten.*;
    
        import quasarusers.portal.TemplateViewPanel;
    



public class ReiseViewVisualizer extends WingsObjectViewVisualizer {
    

    public ReiseViewVisualizer(Keywords arguments) {
        super(arguments);
    }

    
    protected ViewVisualizer provideChildVisualizer(View child, Keywords arguments) {
        
        Object panel = null;

        
        if (child.getName().equals("Kostenpunkt")) {
            panel = ((TemplateViewPanel)getVisualRepresentation()).getComponent("Kostenpunkt");
            return super.provideChildVisualizer(child, new Keywords(arguments, "visualRepresentation", panel, "fieldMappingFactory", new reisekosten.swing.MoneyFieldMappingFactory(), "columnNames", new String[]{"betrag"}));
        }
        
        return super.provideChildVisualizer(child, arguments);
        
    }
    
        public Object makeVisualRepresentation() {
        
                        return new TemplateViewPanel("Reise.html");
                    
        }

        
}
       