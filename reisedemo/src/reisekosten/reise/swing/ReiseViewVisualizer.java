package reisekosten.reise.swing;

import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.view.ViewVisualizer;
import com.sdm.quasar.view.View;
import com.sdm.quasar.newmodelview.implementation.swing.SwingObjectViewVisualizer;

    import reisekosten.*;
    
        import javax.swing.*;
        import quasarusers.util.swing.SwingXMLBuilder;
    



public class ReiseViewVisualizer extends SwingObjectViewVisualizer {
    
    private SwingXMLBuilder builder;
    

    public ReiseViewVisualizer(Keywords arguments) {
        super(arguments);
    }

    
    protected ViewVisualizer provideChildVisualizer(View child, Keywords arguments) {
        
        Object panel = null;

        
        if (child.getName().equals("Kostenpunkt")) {
            panel = builder.getComponentByName("panel/Kostenpunkt");
            return super.provideChildVisualizer(child, new Keywords(arguments, "visualRepresentation", panel, "fieldMappingFactory", new reisekosten.swing.MoneyFieldMappingFactory(), "columnNames", new String[]{"betrag"}));
        }
        
        return super.provideChildVisualizer(child, arguments);
        
    }
    
        public Object makeVisualRepresentation() {
        
                        builder = SwingXMLBuilder.create(ClassLoader.getSystemResourceAsStream("Reise.xml"));

                        return builder.getComponentByName("panel");
                    
        }

        
       public static void main(String[] args) {
        SwingXMLBuilder builder = SwingXMLBuilder.create(ClassLoader.getSystemResourceAsStream("Reise.xml"));
        JComponent component = builder.getComponentByName("panel");

        JFrame borderFrame= new JFrame();

        borderFrame.setSize(800, 600);
        borderFrame.getContentPane().add(component);
        borderFrame.pack();
        borderFrame.show();
      }
        
}
       