package quasarusers.util.dataview.wings;

import com.sdm.quasar.dataview.DataView;
import com.sdm.quasar.modelview.ObjectView;
import com.sdm.quasar.view.implementation.wings.ViewPanel;
import org.wings.SButton;
import org.wings.SComboBox;
import org.wings.SConstants;
import org.wings.SPanel;
import org.wings.STable;

import javax.swing.table.DefaultTableModel;

//todo dpk 23/01/2003 -> MR kommentieren

public class ExtentPanel extends ViewPanel {
  STable mQueryTable = new STable(new DefaultTableModel());

  public ExtentPanel() {
    super();

    SPanel panel = this;

    SButton queryButton = new SButton("query");
    queryButton.setName("queryCommand");
    panel.add(queryButton);

    SComboBox queryChooser = new SComboBox();
    queryChooser.setName("queryChooser");
    panel.add(queryChooser);

    mQueryTable.setName("ExtentTable");
    mQueryTable.setShowGrid(true);
    mQueryTable.setSelectionMode(SConstants.SINGLE_SELECTION);

    panel.add(mQueryTable);

    queryButton = new SButton("anlegen");
    queryButton.setName(ObjectView.CREATE_OBJECT + "Command");
    panel.add(queryButton);

    queryButton = new SButton("bearbeiten");
    queryButton.setName(DataView.EDIT_OBJECT + "Command");
    panel.add(queryButton);

    queryButton = new SButton("löschen");
    queryButton.setName(ObjectView.DELETE_OBJECT + "Command");
    panel.add(queryButton);

    queryButton = new SButton("abbrechen");
    queryButton.setName(ObjectView.CANCEL_VIEW + "Command");
    panel.add(queryButton);
  }
}
