package quasarusers.util.modelview.wings;

import com.sdm.quasar.modelview.implementation.wings.WingsTableCellRenderer;
import org.wings.SComponent;
import org.wings.STable;
import quasarusers.util.mapping.HtmlContent;

//todo dpk 23/01/2003 -> MR kommentieren

public class HtmlCellRenderer extends WingsTableCellRenderer {
  public SComponent getTableCellRendererComponent(STable baseTable,
                                                  Object value,
                                                  boolean selected,
                                                  int row,
                                                  int col) {
    if (value instanceof HtmlContent) {
      value = ((HtmlContent)value).getHTML();
      setEscapeSpecialChars(false);
    }
    else
      setEscapeSpecialChars(true);

    return super.getTableCellRendererComponent(baseTable, value, selected, row, col);
  }
}
