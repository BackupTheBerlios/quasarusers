// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   TableCG.java

package quasarusers.util.plaf;

import org.wings.ClickableRenderComponent;
import org.wings.RequestURL;
import org.wings.SCellRendererPane;
import org.wings.SDimension;
import org.wings.STable;
import org.wings.io.Device;
import org.wings.plaf.xhtml.Utils;
import org.wings.style.Style;
import org.wings.table.STableCellRenderer;
import org.wings.util.AnchorRenderStack;
import org.wings.util.CGUtil;

import java.awt.*;
import java.io.IOException;

//todo dpk 23/01/2003 -> MR kommentieren ???

public final class TableCG extends org.wings.plaf.xhtml.TableCG {

  public TableCG() {
  }

  public void setStyle(Style style) {
    this.style = style;
  }

  public Style getStyle() {
    return style;
  }

  public void writePrefix(Device d, STable table)
          throws IOException {
    boolean showHorizontalLines = table.getShowHorizontalLines();
    boolean showVerticalLines = table.getShowVerticalLines();
    SDimension intercellPadding = table.getIntercellPadding();
    SDimension intercellSpacing = table.getIntercellSpacing();

    d.print("\n<table");
    CGUtil.writeSize(d, table);

    int thickness = 0;

    if (showHorizontalLines || showVerticalLines)
      thickness = 1;

    if (showHorizontalLines && showVerticalLines)
      d.print(" rules=\"all\"");
    else if (showVerticalLines)
      d.print(" rules=\"cols\"");
    else if (showHorizontalLines)
      d.print(" rules=\"rows\"");
    else
      d.print(" rules=\"none\"");

    if (thickness >= 0) // PATCH RADE > --> >=
      d.print(" border=\"").print(thickness).print("\"");

    if (intercellSpacing != null && intercellSpacing.width != null)
      d.print(" cellspacing=\"").print(intercellSpacing.width).print("\"");

    if (intercellPadding != null && intercellPadding.width != null)
      d.print(" cellpadding=\"").print(intercellPadding.width).print("\"");

    String style = table.getStyle() == null ? null : table.getStyle().getName();

    if (style == null)
      style = table.getAttributes().size() <= 0 ? null : "_" + table.getComponentId();

    if (style != null)
      d.print(" class=\"").print(style).print("\"");

    d.print(">\n");
  }

  public void writeBody(Device d, STable table)
          throws IOException {
    Style style = table.getStyle();
    int startRow = 0;
    int startCol = 0;
    int endRow = table.getRowCount();
    int endCol = table.getColumnCount();
    Rectangle viewport = table.getViewportSize();
    if (viewport != null) {
      startRow = viewport.y;
      startCol = viewport.x;
      endRow = startRow + viewport.height;
      endCol = startCol + viewport.width;
    }
    SCellRendererPane rendererPane = table.getCellRendererPane();
    if (table.isHeaderVisible()) {
      d.print("<tr");
      Style headerStyle = table.getHeaderStyle();
      if (headerStyle != null)
        d.print(" class=\"").print(headerStyle.getName()).print("\"");
      d.print(">");
      boolean selectionWritten = table.getRowSelectionColumn() < 0;
      for (int c = startCol; c < endCol; c++) {
        if (!selectionWritten && c >= table.getRowSelectionColumn()) {
          writeEmptyHeaderCell(table, d);
          selectionWritten = true;
        }
        writeHeaderCell(d, table, rendererPane, c);
      }

      if (!selectionWritten)
        writeEmptyHeaderCell(table, d);
      d.print("</tr>");
    }
    for (int r = startRow; r < endRow; r++) {
      d.print("<tr ");
      Style rowStyle = table.isRowSelected(r) ? table.getSelectionStyle() : table.getStyle();
      if (rowStyle != null)
        d.print(" class=\"").print(rowStyle.getName()).print("\"");
      d.print(">");
      boolean selectionWritten = table.getSelectionModel().getSelectionMode() == -1 || table.getRowSelectionColumn() < 0;
      for (int c = startCol; c < endCol; c++) {
        if (!selectionWritten && c >= table.getRowSelectionColumn()) {
          writeRowSelection(d, table, rendererPane, r, c);
          selectionWritten = true;
        }
        writeCell(d, table, rendererPane, r, c);
      }

      if (!selectionWritten)
        writeRowSelection(d, table, rendererPane, r, endCol);
      d.print("</tr>");
    }

  }

  protected void writeCell(Device d, STable table, SCellRendererPane rendererPane, int row, int col)
          throws IOException {
    org.wings.SComponent comp = null;
    boolean isEditingCell = table.isEditing() && row == table.getEditingRow() && col == table.getEditingColumn();
    if (isEditingCell)
      comp = table.getEditorComponent();
    else
      comp = table.prepareRenderer(table.getCellRenderer(row, col), row, col);
    d.print("<td>");
    boolean pushedURL = false;
    if (!isEditingCell && table.isCellEditable(row, col)) {
      RequestURL editAddr = table.getRequestURL();
      editAddr.addParameter(table, table.getEditParameter(row, col));
      if (comp instanceof ClickableRenderComponent) {
        AnchorRenderStack.push(editAddr, null);
        pushedURL = true;
      } else {
        d.print("<a href=\"").print(editAddr.toString()).print("\">");
        Utils.printIcon(d, editIcon, null);
        d.print("</a>&nbsp;");
      }
    }
    rendererPane.writeComponent(d, comp, table);
    if (pushedURL)
      AnchorRenderStack.pop();
    d.print("</td>");
  }

  protected void writeRowSelection(Device d, STable table, SCellRendererPane rendererPane, int row, int col)
          throws IOException {
    STableCellRenderer rowSelectionRenderer = table.getRowSelectionRenderer();
    if (rowSelectionRenderer == null) {
      if (table.getResidesInForm()) {
        writeDefaultFormRowSelection(d, table, rendererPane, row);
        return;
      }
      rowSelectionRenderer = org.wings.plaf.xhtml.TableCG.DEFAULT_ROW_SELECTION_RENDERER;
    }
    org.wings.SComponent comp = rowSelectionRenderer.getTableCellRendererComponent(table, table.getToggleSelectionParameter(row, -1), table.isRowSelected(row), row, -1);
    d.print("<td>");
    RequestURL toggleSelectionAddr = table.getRequestURL();
    toggleSelectionAddr.addParameter(table, table.getToggleSelectionParameter(row, col));
    if (comp instanceof ClickableRenderComponent)
      AnchorRenderStack.push(toggleSelectionAddr, null);
    else
      d.print("<a href=\"").print(toggleSelectionAddr.toString()).print("\">");
    rendererPane.writeComponent(d, comp, table);
    if (comp instanceof ClickableRenderComponent)
      AnchorRenderStack.pop();
    else
      d.print("</a>");
    d.print("</td>");
  }

  protected void writeHeaderCell(Device d, STable table, SCellRendererPane rendererPane, int c)
          throws IOException {
    org.wings.SComponent comp = table.prepareHeaderRenderer(c);
    Style headerStyle = table.getHeaderStyle(); // // PATCH RADE

    d.print("<td"); // PATCH RADE. War vorher: d.print("<th");

    // PATCH RADE: Hinzugefügt
    if (headerStyle != null)
      d.print(" class=\"").print(headerStyle.getName()).print("\"");
    d.print(">");
    // ENDE PATCH RADE

    rendererPane.writeComponent(d, comp, table);

    d.print("</td>"); // PATCH RADE. War vorher  d.print("</th>");
  }

  protected void writeEmptyHeaderCell(STable table, Device d)
          throws IOException {
    d.print("<th>&nbsp;</th>");
  }

  private Style style;
}
