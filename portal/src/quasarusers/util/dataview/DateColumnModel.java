package quasarusers.util.dataview;

import quasarusers.util.dataview.ColumnModel;
import com.sdm.quasar.modelview.server.model.AttributeModel;
import com.sdm.quasar.util.LocalizedString;

import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 */
public class DateColumnModel extends ColumnModel {
  private final String title;
  private final String label;
  private final SimpleDateFormat sdfDe = new SimpleDateFormat("dd.MM.yyyy");
  private final SimpleDateFormat sdfKurz = new SimpleDateFormat("MM/yyyy");
  private final SimpleDateFormat sdfSehrKurz = new SimpleDateFormat("MM/yy");

  public final static class Modus {
    private Modus() {
    }
  }

  /**
   * Öffentliche Konstanten, die steuern, auf welche Weise Daten durch diese AttributeModels modifiziert werden sollen
   * KURZ       : Datumsangeben werden verkürzt in der Form mm/yyyy ausgegeben
   * SEHRKURZ   : Datumsangeben werden verkürzt in der Form mm/yy ausgegeben
   * AUSBLENDEN : Datumsangaben werden ausgeblendet, wenn sie mit dem 31.12.4712 gefüllt sind und leere
   *              Eingaben werden auf den 31.12.4712 gemapped
   */

  public static final Modus DE = new Modus();
  public static final Modus KURZ = new Modus();
  public static final Modus SEHRKURZ = new Modus();
  public static final Modus AUSBLENDEN = new Modus();

  /**
   * Durch diese Variable wird gesteuert, auf welche Weise das AttributeModel funktionieren soll.
   * vgl oben.
   */
  private final Modus modus;


  private static final Date LEER_DATUM = Date.valueOf("4712-12-30");
//  private static final Date MAX_DATUM = Date.valueOf("4712-12-31");

/**
 * Konstuktor für das DateColumnModel
 *
 * @param title
 * @param label
 * @param attibuteModel
 * @param index
 * @param modus vgl. oben
 */
  public DateColumnModel(String title, String label, AttributeModel attibuteModel, int index, Modus modus) {
    super(title, attibuteModel, index, false);

    this.title = title;
    this.modus = modus;
    this.label = label;
  }

  public LocalizedString getLabel() {
    return new LocalizedString(label);
  }

  /**
   * Ermittelt den umformatierten Wert eines Datums
   *
   * @param   object
   * @return  je nachdem, mit welchem Modus das DateColumnModel initialisiert wird, der umformatierte Datumswert.
   * @throws  Exception
   */
  public Object getValue(Object object) throws Exception {
    Object value = super.getValue(object);

    if (value instanceof Date) {
      if ( modus.equals(AUSBLENDEN) ) {
        if (((Date) value).after(LEER_DATUM))
          value = null;
        if (value != null)
          value = sdfDe.format(value);
      } else if ( modus.equals(DE) ) {
        value = sdfDe.format(value);
      } else if ( modus.equals(KURZ) ) {
        value = sdfKurz.format((Date) value);
      } else if ( modus.equals(SEHRKURZ) ) {
        value = sdfSehrKurz.format((Date) value);
      }
    }

    return value;
  }

}
