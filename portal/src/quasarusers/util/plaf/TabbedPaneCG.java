// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   TabbedPaneCG.java

package quasarusers.util.plaf;

import org.wings.SAbstractButton;
import org.wings.SComponent;
import org.wings.SConstants;
import org.wings.SContainer;
import org.wings.SIcon;
import org.wings.STabbedPane;
import org.wings.border.SBorder;
import org.wings.io.Device;
import org.wings.plaf.CGManager;
import org.wings.plaf.compiler.Utils;
import org.wings.session.SessionManager;
import org.wings.style.Style;

import java.io.IOException;

//todo dpk 23/01/2003 -> MR kommentieren ???

public class TabbedPaneCG
        implements SConstants, org.wings.plaf.TabbedPaneCG {

  public TabbedPaneCG() {
    CGManager manager = SessionManager.getSession().getCGManager();

    setConnectIconNormal((SIcon) manager.getObject("TabbedPaneCG.connectIconNormal", org.wings.SIcon.class));
    setConnectIconSelectedLeft((SIcon) manager.getObject("TabbedPaneCG.connectIconSelectedLeft", org.wings.SIcon.class));
    setConnectIconSelectedRight((SIcon) manager.getObject("TabbedPaneCG.connectIconSelectedRight", org.wings.SIcon.class));
    setFirstIconNormal((SIcon) manager.getObject("TabbedPaneCG.firstIconNormal", org.wings.SIcon.class));
    setFirstIconSelected((SIcon) manager.getObject("TabbedPaneCG.firstIconSelected", org.wings.SIcon.class));
    setImageAlign((String) manager.getObject("TabbedPaneCG.imageAlign", java.lang.String.class));
    setLastIconNormal((SIcon) manager.getObject("TabbedPaneCG.lastIconNormal", org.wings.SIcon.class));
    setLastIconSelected((SIcon) manager.getObject("TabbedPaneCG.lastIconSelected", org.wings.SIcon.class));
    setTabNonSelectedStyle((Style) manager.getObject("TabbedPaneCG.tabNonSelectedStyle", org.wings.style.Style.class));
  }

  public void installCG(SComponent comp) {
    STabbedPane component = (STabbedPane) comp;
    CGManager manager = component.getSession().getCGManager();
    Object value = manager.getObject("STabbedPane.selectionStyle", org.wings.style.Style.class);

    if (value != null)
      component.setSelectionStyle((Style) value);
  }

  public void uninstallCG(SComponent scomponent) {
  }

  public void write(Device device, SComponent _c)
          throws IOException {
    STabbedPane component = (STabbedPane) _c;
    SBorder _border = component.getBorder();

    if (_border != null)
      _border.writePrefix(device);

    int maxTabsPerLine = component.getMaxTabsPerLine();
    SContainer buttons;
    SContainer contents;

    if ("Center".equals(component.getConstraintAt(0))) {
      buttons = (SContainer) component.getComponentAt(1);
      contents = (SContainer) component.getComponentAt(0);
    } else {
      buttons = (SContainer) component.getComponentAt(0);
      contents = (SContainer) component.getComponentAt(1);
    }
    boolean newLine = true;
    boolean selected = false;
    boolean selectedBefore = false;

    int loops = buttons.getComponentCount();

    device.write(__table_tr_td_nopad); // PATCH RADE

    for (int i = 0; i < loops; i++) {
      selected = i == component.getSelectedIndex();
      device.write(__img);
      Utils.optAttribute(device, "align", imageAlign);

      if (newLine) {
        if (selected)
          Utils.optAttribute(device, "src", firstIconSelected.getURL());
        else
          Utils.optAttribute(device, "src", firstIconNormal.getURL());

        newLine = false;
      } else if (selectedBefore)
        Utils.optAttribute(device, "src", connectIconSelectedRight.getURL());
      else if (selected)
        Utils.optAttribute(device, "src", connectIconSelectedLeft.getURL());
      else
        Utils.optAttribute(device, "src", connectIconNormal.getURL());

      device.write(__);

      SAbstractButton button = (SAbstractButton) buttons.getComponentAt(i);
      String text = button.getText();

      if (text != null && !text.endsWith("&nbsp;")) {
        button.setText(text + "&nbsp;");
        button.setEscapeSpecialChars(false);
      }

      selected = i == component.getSelectedIndex();
      button.setStyle(selected ? component.getSelectionStyle() : tabNonSelectedStyle);
      button.write(device);

      if (maxTabsPerLine > 0 && (i + 1) % maxTabsPerLine == 0) {
        device.write(__img);
        Utils.optAttribute(device, "align", imageAlign);

        if (selected)
          Utils.optAttribute(device, "src", lastIconSelected.getURL());
        else
          Utils.optAttribute(device, "src", lastIconNormal.getURL());

        device.write(__br);
        newLine = true;
      }
      selectedBefore = selected;
    }

    if (!newLine) {
      device.write(__img);
      Utils.optAttribute(device, "align", imageAlign);
      if (selected)
        Utils.optAttribute(device, "src", lastIconSelected.getURL());
      else
        Utils.optAttribute(device, "src", lastIconNormal.getURL());
      device.write(__);
    }

    device.write(__td_tr_table); // PATCH RADE

    // device.write(__table_width_10); PATCH RADE
    //  device.write(__td); PATCH RADE
    contents.write(device);
    // device.write(__td_tr_table); PATCH RADE

    if (_border != null)
      _border.writePostfix(device);
  }

  public SIcon getConnectIconNormal() {
    return connectIconNormal;
  }

  public void setConnectIconNormal(SIcon connectIconNormal) {
    this.connectIconNormal = connectIconNormal;
  }

  public SIcon getConnectIconSelectedLeft() {
    return connectIconSelectedLeft;
  }

  public void setConnectIconSelectedLeft(SIcon connectIconSelectedLeft) {
    this.connectIconSelectedLeft = connectIconSelectedLeft;
  }

  public SIcon getConnectIconSelectedRight() {
    return connectIconSelectedRight;
  }

  public void setConnectIconSelectedRight(SIcon connectIconSelectedRight) {
    this.connectIconSelectedRight = connectIconSelectedRight;
  }

  public SIcon getFirstIconNormal() {
    return firstIconNormal;
  }

  public void setFirstIconNormal(SIcon firstIconNormal) {
    this.firstIconNormal = firstIconNormal;
  }

  public SIcon getFirstIconSelected() {
    return firstIconSelected;
  }

  public void setFirstIconSelected(SIcon firstIconSelected) {
    this.firstIconSelected = firstIconSelected;
  }

  public String getImageAlign() {
    return imageAlign;
  }

  public void setImageAlign(String imageAlign) {
    this.imageAlign = imageAlign;
  }

  public SIcon getLastIconNormal() {
    return lastIconNormal;
  }

  public void setLastIconNormal(SIcon lastIconNormal) {
    this.lastIconNormal = lastIconNormal;
  }

  public SIcon getLastIconSelected() {
    return lastIconSelected;
  }

  public void setLastIconSelected(SIcon lastIconSelected) {
    this.lastIconSelected = lastIconSelected;
  }

  public Style getTabNonSelectedStyle() {
    return tabNonSelectedStyle;
  }

  public void setTabNonSelectedStyle(Style tabNonSelectedStyle) {
    this.tabNonSelectedStyle = tabNonSelectedStyle;
  }

  private static final byte __img[] = "<img".getBytes();
  private static final byte __[] = " />".getBytes();
  private static final byte __br[] = " /><br />".getBytes();
  private static final byte __table_tr_td_nopad[] = "\n<table width=\"100%\" cellpadding=\"0\" cellspacing=\"1\" border=\"0\"><tr><td>".getBytes(); // Patch RADE
  private static final byte __table_tr_td[] = "\n<table><tr><td>".getBytes(); // PATCH RADE
  private static final byte __td[] = "<td".getBytes();
  private static final byte ___1[] = ">".getBytes();
  private static final byte __td_tr_table[] = "</td></tr></table>\n".getBytes();
  private SIcon connectIconNormal;
  private SIcon connectIconSelectedLeft;
  private SIcon connectIconSelectedRight;
  private SIcon firstIconNormal;
  private SIcon firstIconSelected;
  private String imageAlign;
  private SIcon lastIconNormal;
  private SIcon lastIconSelected;
  private Style tabNonSelectedStyle;

}
