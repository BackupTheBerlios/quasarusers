package reisekosten.reise.swing;

import com.sdm.quasar.dataview.implementation.swing.SwingDataViewVisualizer;
import com.sdm.quasar.dataview.DataView;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.view.implementation.swing.SwingViewUtilities;
import com.sdm.quasar.view.implementation.swing.CommandTrigger;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import reisekosten.reise.ReiseDataView;

/**
 * Created by IntelliJ IDEA.
 * User: schmickl
 * Date: 22.02.2003
 * Time: 19:39:53
 * To change this template use Options | File Templates.
 */
public class ReiseDataViewVisualizer extends SwingDataViewVisualizer {
    public ReiseDataViewVisualizer(Keywords arguments) {
        super(arguments);
    }

    public Object makeVisualRepresentation() {
        JPanel panel = new JPanel();

        panel.setLayout(new BorderLayout());

        JTable table = new JTable();
        table.setName("ExtentTable");

        panel.add(table, BorderLayout.CENTER);

        return panel;
    }

    protected JPopupMenu makeContextMenu(Component component, int x, int y) {
        JPopupMenu popupMenu = super.makeContextMenu(component, x, y);

        popupMenu.add(SwingViewUtilities.makeCommandMenuItem(new CommandTrigger(ReiseDataView.C_PRINT, false, true),
                "drucken", null, null, null));

        return popupMenu;
    }

    public List buildTools() {
        return super.buildTools();
    }
}
