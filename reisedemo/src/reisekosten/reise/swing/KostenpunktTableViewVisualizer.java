package reisekosten.reise.swing;

import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.view.View;
import com.sdm.quasar.view.implementation.wings.ViewPanel;
import com.sdm.quasar.view.ViewVisualizer;
import com.sdm.quasar.newmodelview.implementation.swing.SwingTableViewVisualizer;

        import javax.swing.*;
        import quasarusers.util.swing.SwingXMLBuilder;
    
    import reisekosten.*;
    

public class KostenpunktTableViewVisualizer extends SwingTableViewVisualizer {
    public KostenpunktTableViewVisualizer(Keywords arguments) {
        super(arguments);
    }

    
        public Object makeVisualRepresentation() {
        return null;
        }

        
       public static void main(String[] args) {
        SwingXMLBuilder builder = SwingXMLBuilder.create(ClassLoader.getSystemResourceAsStream("Kostenpunkt.xml"));
        JComponent component = builder.getComponentByName("panel");

        JFrame borderFrame= new JFrame();

        borderFrame.setSize(800, 600);
        borderFrame.getContentPane().add(component);
        borderFrame.pack();
        borderFrame.show();
      }
        
}
       