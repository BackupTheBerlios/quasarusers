package quasarusers.util.modelview.server;

import com.sdm.quasar.modelview.server.model.AttributeModel;
import com.sdm.quasar.modelview.server.model.TransformedAttributeModel;
import com.sdm.quasar.persistence.Persistent;
import com.sdm.quasar.util.Assertion;

import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Diese Klasse baut ein AttributModell für die Modelview, das das DB-seitig
 * als Leerdatum verwendete "31.12.4712" viewseitig durch einen Leersting
 * ersetzt.
 *
 * @author Dominic Kerstjens
 */

public final class DateAttributeModel extends TransformedAttributeModel {
  /** Hilfsdatentyp */
  public final static class Modus {
    /** Der Name des Modus (wird nur gebraucht, damit man im Debugger
     * erkennen kann, welches Modus-Objekt man gerade ansieht). */
    private final String mName;

    /** Erzeugt ein Modus-Objekt.
     *
     * @param  name  der Name des Modus
     */
    private Modus(String name) {
      mName = name;
    }
  }

  /**
   * Es soll das Datumsformat "mm/yyyy" verwendet werden.
   */
  public static final Modus KURZ = new Modus("kurz");

  /**
   * Das Datum "31.12.4712" soll viewseitig ausgeblendet wird.
   */
  public static final Modus AUSBLENDEN = new Modus("ausblenden");

  private static final java.sql.Date LEER_DATUM = java.sql.Date.valueOf("4712-12-30");
  private static final java.sql.Date MAX_DATUM = java.sql.Date.valueOf("4712-12-31");

  private final static SimpleDateFormat KURZ_FORMAT = new SimpleDateFormat("MM/yyyy");

  /**
   * Durch diese Variable wird gesteuert, auf welche Weise das AttributeModel funktionieren soll.
   * vgl oben.
   */
  private final Modus[] mModi;

  /**
   * Erzeugt ein <code>DateAttributeModel</code>.
   *
   * @param  attributeModel das ursprüngliche Attributmodell
   * @param  modus          der Modus ({@link #KURZ} oder {@link #AUSBLENDEN})
   */
  public DateAttributeModel(AttributeModel attributeModel, Modus modus) {
    super(attributeModel);

    Assertion.checkNotNull(modus, "modus");

    this.mModi = new Modus[]{modus};
  }

  /**
   * Erzeugt ein <code>DateAttributeModel</code>.
   *
   * @param  attributeModel das ursprüngliche Attributmodell
   * @param  modi           die Modus ({@link #KURZ} und/oder {@link #AUSBLENDEN})
   */
  public DateAttributeModel(AttributeModel attributeModel, Modus[] modi) {
    super(attributeModel);

    Assertion.checkNotNull(modi, "modi");

    this.mModi = modi;
  }

  public Object getValue(Object object) throws Exception {
    Object value = super.getValue(object);

    if (object instanceof Persistent && value instanceof Date) {
      if (hatModus(AUSBLENDEN) && ((Date) value).after(LEER_DATUM))
          value = null;
      else if (hatModus(KURZ))
        value = KURZ_FORMAT.format((Date) value);
    }

    return value;
  }

  public void setValue(Object object, Object value) throws Exception {
    if (object instanceof Persistent && value == null && hatModus(AUSBLENDEN))
        value = MAX_DATUM;

    super.setValue(object, value);
  }

  public boolean isRequired() {
    return false;
  }

  private boolean hatModus(Modus modus) {
    for (int i = 0 ; i < mModi.length; i++)
      if (modus == mModi[i])
        return true;

    return false;
  }
}

