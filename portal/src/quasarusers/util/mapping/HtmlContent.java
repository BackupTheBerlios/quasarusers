package quasarusers.util.mapping;

import java.io.Serializable;

import org.wings.plaf.xhtml.Utils;

public class HtmlContent implements Serializable {
//todo dpk 23/01/2003 -> MR kommentieren

  private String html;
  private String label;

  public HtmlContent(String html) {
    this(html, true);
  }

  public HtmlContent(String html, boolean escapeSpecialCharacters) {
    this.label = html;
    this.html = escapeSpecialCharacters ? escapeSpecialChars(html) : html;
  }

  public HtmlContent(String prefix, String label, String postfix) {
    this.label = label;
    this.html = prefix + escapeSpecialChars(label) + postfix;
  }

  public HtmlContent(String label, String reference) {
    this.label = label;
    this.html = "<a href=\"" + reference + "\">" + escapeSpecialChars(label) + "</a>";
  }

  public static String escapeSpecialChars(String label) {
    return Utils.escapeSpecialChars(label);
  }

  public String getHTML() {
    return html;
  }

  public String toString() {
    return label;
  }
}
