package quasarusers.util.wings;

import com.sdm.quasar.component.ComponentException;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.lang.Symbol;
import com.sdm.quasar.util.Arrays;
import com.sdm.quasar.view.View;
import com.sdm.quasar.view.ViewManager;
import com.sdm.quasar.view.ViewTransaction;
import com.sdm.quasar.view.implementation.wings.WingsViewVisualizer;
import quasarusers.util.modelview.wings.HtmlCellRenderer;
import quasarusers.util.mapping.HtmlContent;
import org.wings.SButton;
import org.wings.SComponent;
import org.wings.STable;
import org.wings.style.SimpleAttributeSet;
import org.wings.style.Style;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Marco Schmickler
 */
//todo dpk: 23/01/03 -> MC kommentieren

public class LinkCellRenderer extends HtmlCellRenderer {
  private static final Style TABLE_HEADER_WEISS_LINK_STYLE = new Style("TableHeaderWeissLink",
                                                                       new SimpleAttributeSet());

  static {
    TABLE_HEADER_WEISS_LINK_STYLE.putAttribute("tableHeader", "true");
  }

  private View view;
  private Symbol[] commands;
  private int[] columns;
  private String[] keys;

  public LinkCellRenderer(View view, Symbol command, int column, String key) {
    this.view = view;
    this.commands = new Symbol[]{command};
    this.columns = new int[]{column};
    this.keys = new String[]{key};
  }

  public LinkCellRenderer(View view, String command, int column, String key) {
    this.view = view;
    this.commands = new Symbol[]{Symbol.forName(command)};
    this.columns = new int[]{column};
    this.keys = new String[]{key};
  }

  public LinkCellRenderer(View view, Symbol command, int[] columns, String[] keys) {
    this.view = view;
    this.commands = new Symbol[]{command};
    this.columns = columns;
    this.keys = keys;
  }

  public LinkCellRenderer(View view, String command, int[] columns, String[] keys) {
    this.view = view;
    this.commands = new Symbol[]{Symbol.forName(command)};
    this.columns = columns;
    this.keys = keys;
  }

  public LinkCellRenderer(View view, String[] commands, int[] columns, String[] keys) {
    this.view = view;
    for (int i = 0; i < commands.length; i++) {
      String command = commands[i];
      this.commands[i] = Symbol.forName(command);
    }
    this.columns = columns;
    this.keys = keys;
  }

  public LinkCellRenderer(View view, Symbol[] commands, int[] columns, String[] keys) {
    this.view = view;
    this.commands = commands;
    this.columns = columns;
    this.keys = keys;
  }

  public SComponent getTableCellRendererComponent(final STable baseTable,
                                                  final Object value,
                                                  boolean selected,
                                                  final int row,
                                                  int col) {
    final int columnIndex = Arrays.indexOf(columns, col);

    if (columnIndex >= 0) {
      SButton button = new SButton((value != null) ? value.toString() : "");
      button.setEscapeSpecialChars(!(value instanceof HtmlContent));
      button.setShowAsFormComponent(false);

      if (row == -1)
        button.setStyle(TABLE_HEADER_WEISS_LINK_STYLE);

      button.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (view.isBlocked() || ((View) view.getRootComponent()).isBlocked())
            return;

          final ViewManager viewManager = ViewManager.getViewManager();
          final ViewTransaction transaction = viewManager.beginTransaction();
          final Keywords arguments = new Keywords("viewTransaction", transaction,
                                                  keys[columnIndex], value,
                                                  "index", new Integer(row),
                                                  "column", keys[columnIndex]);

            try {
                view.performCommand((commands.length == 1) ? commands[0] : commands[columnIndex],
                                    arguments);
            }
            catch (ComponentException e1) {
                e1.printStackTrace();  //To change body of catch statement use Options | File Templates.
            }
        }
      }
      );
      return button;
    } else {
      return super.getTableCellRendererComponent(baseTable, value,
                                                 selected, row, col);
    }
  }
}
