package quasarusers.util.swing;

import java.util.Iterator;
import javax.swing.JTable;

import com.sdm.quasar.dataview.sample.QueryPanel;
import com.sdm.quasar.util.Assertion;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.newmodelview.implementation.swing.SwingTableViewVisualizer;

/**
 * Created by IntelliJ IDEA.
 * User: schmickl
 * Date: Jan 25, 2003
 * Time: 7:34:53 PM
 * To change this template use Options | File Templates.
 */
public class SwingDefaultCollectionViewVisualizer extends SwingTableViewVisualizer {
    private JTable table;

    public SwingDefaultCollectionViewVisualizer(Keywords arguments) {
        super(arguments);
    }

    public Object makeVisualRepresentation() {
        return new QueryPanel();
    }

    public Object getTableUserInterfaceElement() {
        if (table == null) {
            Iterator elements = getUserInterfaceElements().iterator();

            while (elements.hasNext()) {
                Object element = elements.next();

                if (element instanceof JTable) {
                    table = (JTable)element;

                    return table;
                }
            }

            if (Assertion.CHECK && (table == null))
                Assertion.fail("Cannot find JTable");
        }

        return table;
    }
}
