package quasarusers.util.dataview.wings;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Rectangle;
import java.io.IOException;

import org.wings.STable;
import org.wings.SButton;
import org.wings.SPanel;
import org.wings.SLabel;
import org.wings.io.Device;

//todo dpk 23/01/2003 -> MR kommentieren

public class SkippingTableControlPanel extends SPanel {
  private int maximumRows;
  private Rectangle rectangle;
  private int lastRowCount = 0;

  private SButton back = new SButton("<");
  private SButton forward = new SButton(">");
  private SButton start = new SButton("|<");
  private SButton end = new SButton(">|");
  private SLabel  pageCount = new SLabel();
  private STable table;

  public SkippingTableControlPanel(STable table, int maxRows) {
    this.table = table;
    this.maximumRows = maxRows;

    if (table.getViewportSize() == null)
        rectangle = new Rectangle(0, 0, table.getColumnCount(), maximumRows);
    else
        rectangle = table.getViewportSize();

    add(start);
    add(back);
    add(forward);
    add(end);
    add(pageCount);

    back.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (getCurrentPage() > 0) {
            rectangle.translate(0, - maximumRows);
        }
      }
    });

    forward.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (getCurrentPage() < getMaxPage())
            rectangle.translate(0, maximumRows);
      }
    });

    start.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        rectangle.setLocation(0, 0);
      }
    });

    end.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        rectangle.setLocation(0, getMaxPage() * maximumRows);
      }
    });
  }

  public STable getTable() {
    return table;
  }

  public void setTable(STable table) {
    this.table = table;
  }

  private final int getCurrentStartRow() {
    return rectangle.y;
  }

  private final int getRowCount() {
    return table.getRowCount();
  }

  private final int getCurrentPage() {
    return getCurrentStartRow() / maximumRows;
  }

  private final int getMaxPage() {
    return (getRowCount() - 1) / maximumRows;
  }

  public void setupButtons() {
    if (table.getViewportSize() == null)
        table.setViewportSize(rectangle);

    if (lastRowCount != getRowCount()) {
      lastRowCount = getRowCount();
      rectangle.y = 0;
    }

    start.setEnabled(getCurrentPage() > 0);
    back.setEnabled(getCurrentPage() > 0);
    forward.setEnabled(getCurrentPage() < getMaxPage());
    end.setEnabled(getCurrentPage() < getMaxPage());

    rectangle.height = Math.min(getRowCount() - rectangle.y, maximumRows);
    rectangle.width = table.getColumnCount();

    if (table.getRowCount() == 0)
        pageCount.setText("     Die Liste enthält keine Einträge");
    else
        pageCount.setText("   " + (rectangle.y + 1) + " - " + (rectangle.y + rectangle.height) + " von " + getRowCount());
  }

  public void write(Device s) throws IOException {
    setupButtons();

    if (table.getRowCount() > maximumRows)
        super.write(s);
  }
}
