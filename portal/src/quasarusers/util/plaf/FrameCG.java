// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   FrameCG.java

package quasarusers.util.plaf;

import org.wings.SComponent;
import org.wings.SFrame;
import org.wings.SLink;
import org.wings.StaticResource;
import org.wings.io.Device;
import org.wings.plaf.CGManager;
import org.wings.script.DynamicScriptResource;
import org.wings.script.ScriptListener;
import org.wings.style.DynamicStyleSheetResource;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

//todo dpk 23/01/2003 -> MR kommentieren ???

public final class FrameCG extends org.wings.plaf.xhtml.FrameCG {

  public FrameCG() {
  }

  public void installCG(SComponent component) {
    super.installCG(component);
    SFrame frame = (SFrame) component;
    org.wings.DynamicResource styleSheetResource = new DynamicStyleSheetResource(frame);
    frame.addDynamicResource(styleSheetResource);
    frame.addLink(new SLink("stylesheet", null, "text/css", null, styleSheetResource));
    org.wings.DynamicResource scriptResource = new DynamicScriptResource(frame);
    frame.addDynamicResource(scriptResource);
    frame.addLink(new SLink("javascript", null, "application/x-javascript", null, scriptResource));
    CGManager cgManager = frame.getSession().getCGManager();
    StaticResource staticResource = (StaticResource) cgManager.getObject("lookandfeel.stylesheet", org.wings.Resource.class);
    staticResource.setMimeType("text/css");
    frame.addLink(new SLink("stylesheet", null, "text/css", null, staticResource));
  }

  protected void writeAdditionalHeaders(Device d, SFrame frame)
          throws IOException {
    SLink link;
    for (Iterator iterator = frame.links().iterator(); iterator.hasNext(); link.write(d))
      link = (SLink) iterator.next();

  }

  protected void writeBody(Device d, SFrame frame)
          throws IOException {
    d.print("<body");
    String style = frame.getStyle() == null ? null : frame.getStyle().getName();
    if (style == null)
      style = frame.getAttributes().size() <= 0 ? null : "_" + frame.getComponentId();
    if (style != null)
      d.print(" class=\"").print(style).print("\"");
    for (Iterator it = frame.getScriptListeners().iterator(); it.hasNext(); d.print("\"")) {
      ScriptListener script = (ScriptListener) it.next();
      d.print(" ");
      d.print(script.getEvent());
      d.print("=\"");
      d.print(script.getCode());
    }

    d.print(">");
    writeContents(d, frame);
    d.print("\n</body>\n</html>");
  }

  // BEGIN PATCH RADE: Methode aus Vaterklasse hochgezogen und angepasst
  protected void writeHeader(Device d, SFrame frame)
          throws IOException {
    String language = "de"; // PATCH RADE
    String title = frame.getTitle();
    List metas = frame.metas();
    List headers = frame.headers();
    d.print("<?xml version=\"1.0\" encoding=\"");
    d.print(charSetFor(frame.getSession().getLocale()));
    d.print("\"?>\n");
    d.print("<!DOCTYPE html\n");
    d.print("   PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n");
    d.print("   \"DTD/xhtml1-transitional.dtd\">\n");
    d.print("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"");
    d.print(language);
    d.print("\" lang=\"");
    d.print(language);
    d.print("\">\n");
    d.print("<head>\n");
    if (title != null)
      d.print("<title>").print(title).print("</title>\n");
    if (frame.getBaseTarget() != null)
      d.print("<base target=\"").print(frame.getBaseTarget()).print("\" />");
    d.print("<meta http-equiv=\"Content-type\" content='text/html; charset=\"");
    d.print(charSetFor(frame.getSession().getLocale()));
    d.print("\"' />\n");
    d.print("<meta http-equiv=\"expires\" content=\"1000\" />\n");
    d.print("<meta http-equiv=\"pragma\" content=\"no-cache\" />\n");
    for (Iterator it = metas.iterator(); it.hasNext(); d.print(" />\n")) {
      d.print("<meta ");
      d.print(it.next());
    }

    for (Iterator it = headers.iterator(); it.hasNext(); d.print("\n"))
      d.print(it.next());

    writeAdditionalHeaders(d, frame);
    d.print("</head>\n");
  }

  private String charSetFor(Locale locale) {
    String language = locale.getLanguage();
    if (language.equals("pl"))
      return "iso-8859-2";
    else
      return "iso-8859-1";
  }
  // END PATCH RADE: Methode aus Vaterklasse hochgezogen und angepasst
}
