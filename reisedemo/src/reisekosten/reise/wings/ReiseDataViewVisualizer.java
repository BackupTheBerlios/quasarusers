package reisekosten.reise.wings;

import com.sdm.quasar.dataview.implementation.wings.WingsDataViewVisualizer;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.modelview.implementation.wings.WingsTableCellRenderer;

import javax.swing.table.DefaultTableModel;

import org.wings.STable;
import org.wings.SPanel;
import org.wings.SBorderLayout;
import org.wings.SButton;

/**
 * Created by IntelliJ IDEA.
 * User: schmickl
 * Date: 22.02.2003
 * Time: 19:39:53
 * To change this template use Options | File Templates.
 */
public class ReiseDataViewVisualizer extends WingsDataViewVisualizer {
    public ReiseDataViewVisualizer(Keywords arguments) {
        super(arguments);
    }

    public Object makeVisualRepresentation() {
        SPanel panel = new SPanel();

        panel.setLayout(new SBorderLayout());

        STable table = new STable(new DefaultTableModel());
        table.setName("ExtentTable");

        panel.add(table, SBorderLayout.CENTER);

        SButton button = new SButton("schliessen");
        button.setName("cancelViewCommand");

        panel.add(button, SBorderLayout.SOUTH);

        return panel;
    }

    protected void initializeTable(STable table) {
        table.setDefaultRenderer(new WingsTableCellRenderer());
    }
}
