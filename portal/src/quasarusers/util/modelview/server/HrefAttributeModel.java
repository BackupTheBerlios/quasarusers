/*
 * Created by IntelliJ IDEA.
 * User: rademach
 * Date: 20.06.2002
 * Time: 13:42:28
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package quasarusers.util.modelview.server;

import com.sdm.quasar.modelview.server.model.AttributeModel;
import quasarusers.util.mapping.HtmlContent;

/**
 * Diese Klasse extrahiert ein Attributmodell im Sinne der modellbasierten
 * Präsentation aus einem Array von Attributmodellen. Optional kann es so
 * umgewandelt werden, dass es Wings-Seitig als Href dargestellt wird.
 *
 * @author Matthias Rademacher
 */
public class HrefAttributeModel extends com.sdm.quasar.modelview.server.model.TransformedAttributeModel {
  private final boolean doHref;
  private final String hrefPraefix;
  private final String hrefSuffix;

  /**
   * Extrahiert das gesuchte Attributmodell aus der üebrgenenen Liste und
   * wandelt es um.
   *
   * @param  models           die verfügabren Attributmodelle
   * @param  attributeName    der Name des gesuchten Attributs
   * @param  doHref           falls <code>true</code>, wird der Wert als HRef
   *                          zurückgeliefert
   * @param  hrefPraefix      der Präfix für den HRef (z. B. "mailto:")
   * @param  hrefSuffix       der Suffix für den HRef (z. B. ".jpg")
   */
  public  HrefAttributeModel(AttributeModel[] models,
                            String attributeName,
                            boolean doHref,
                            final String hrefPraefix,
                            final String hrefSuffix) {
    this(extractAttributeModel(models, attributeName), doHref, hrefPraefix, hrefSuffix);
  }

  /**
   * Wandelt das gegebene Attributmodell um.
   *
   * @param  model            das Attributmodell
   * @param  doHref           falls <code>true</code>, wird der Wert als HRef
   *                          zurückgeliefert
   * @param  hrefPraefix      der Präfix für den HRef (z. B. "mailto:")
   * @param  hrefSuffix       der Suffix für den HRef (z. B. ".jpg")
   */
  public HrefAttributeModel(AttributeModel model,
                            boolean doHref,
                            final String hrefPraefix,
                            final String hrefSuffix) {
    super(model);

    this.doHref = doHref;
    this.hrefPraefix = hrefPraefix;
    this.hrefSuffix = hrefSuffix;
  }

  public Object getValue(Object object) throws Exception {
    Object value = super.getValue(object);

    if (doHref) {
      String text = (value == null) ? "" : value.toString();
      String href  = getHrefPraefix() + text.toString() + getHrefSuffix();



      value = makeHRef(text, href);
    }

    return value;
  }

  private static AttributeModel extractAttributeModel(
          AttributeModel[] persistentModels,
          String attributeName) {
    int length = persistentModels.length;

    for (int i = 0; i < length; i++) {
      AttributeModel model = persistentModels[i];

      if (model.getName().equals(attributeName))
        return model;
    }

    return null;
  }

  public boolean isDoHref() {
    return doHref;
  }

  public String getHrefPraefix() {
    return hrefPraefix;
  }

  public String getHrefSuffix() {
    return hrefSuffix;
  }

  protected HtmlContent makeHRef(final String text, String href) {
    return new HtmlContent(text, href);
  }
}
