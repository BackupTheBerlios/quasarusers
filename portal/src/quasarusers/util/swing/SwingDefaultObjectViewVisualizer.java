package quasarusers.util.swing;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import javax.swing.JComponent;

import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.view.ViewVisualizer;
import com.sdm.quasar.view.View;
import com.sdm.quasar.view.AbstractView;
import com.sdm.quasar.dataview.DataView;
import com.sdm.quasar.newmodelview.implementation.swing.SwingObjectViewVisualizer;
import com.sdm.quasar.newmodelview.ObjectView;
import com.sdm.quasar.newmodelview.CollectionView;
import com.sdm.quasar.newmodelview.model.ViewModel;
import com.sdm.quasar.newmodelview.model.FieldModel;
import com.sdm.quasar.newmodelview.model.OperationType;

/**
 * Created by IntelliJ IDEA.
 * User: schmickl
 * Date: Jan 25, 2003
 * Time: 9:29:53 PM
 * To change this template use Options | File Templates.
 */
public class SwingDefaultObjectViewVisualizer extends SwingObjectViewVisualizer {
    public SwingDefaultObjectViewVisualizer(Keywords arguments) {
        super(arguments);
    }

    public Object makeVisualRepresentation() {
        JPanel panel = new JPanel();

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        return panel;
    }

    protected ViewVisualizer provideChildVisualizer(View child, Keywords arguments) {
        if (child instanceof DataView) {
            arguments.addValue("class", "de.sdm.sia.remis.util.dataview.swing.SwingExtentViewVisualizer");
        }
        else if (child instanceof CollectionView) {
            arguments.addValue("class", "de.sdm.sia.remis.util.swing.SwingDefaultCollectionViewVisualizer");
        }
        else if (child instanceof ObjectView) {
            arguments.addValue("class", "de.sdm.sia.remis.util.swing.SwingDefaultObjectViewVisualizer");
        }
        else if (child instanceof AbstractView) {
            arguments.addValue("class", "de.sdm.sia.remis.util.swing.SwingBlindViewVisualizer");
        }

        ViewVisualizer viewVisualizer = super.provideChildVisualizer(child, arguments);

        JPanel parentPanel = (JPanel)getVisualRepresentation();
        parentPanel.add((JComponent)viewVisualizer.getVisualRepresentation());

        return viewVisualizer;
    }

    public void mapFields(ViewModel model) {
        FieldModel[] fieldModels = model.getFieldModels();

        JPanel panel = (JPanel)getVisualRepresentation();

        for (int i = 0; i < fieldModels.length; i++) {
            FieldModel fieldModel = fieldModels[i];
            if (fieldModel.isVisible() && fieldModel.isOperationPermitted(OperationType.READ) && !fieldModel.getName().equals("oid")) {
                addTextField(fieldModel.getName(), fieldModel.getName());
            }
        }

        panel.repaint();
        panel.revalidate();

        super.mapFields(model);
    }

    protected void addTextField(String name, String label) {
        JPanel parentPanel = (JPanel)getVisualRepresentation();
        JPanel panel = new JPanel();
        panel.add(new JLabel(label));
        JTextField field = new JTextField(20);
        field.setName(name);
        panel.add(field);
        parentPanel.add(panel);
    }
}
