/*
 * Created by IntelliJ IDEA.
 * User: rademach
 * Date: 19.06.2002
 * Time: 17:41:57
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package quasarusers.util.dataview;

import com.sdm.quasar.dataview.model.LiteralParameterModel;
import com.sdm.quasar.util.LocalizedString;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;

/**
 * Dies ist ein Parametermodell für die DataView. Es behandelt
 * Datumsfelder. Nicht in Date konvertierbare Benutzereingaben
 * werden einfach ignoriert, d.h. es erfolgt keine Fehlermeldung.
 *
 * @author Marco Schmickler
 * @author Matthias Rademacher
  */
public class DateParameterModel extends LiteralParameterModel {
  /**
   * Constructs a new <code>LiteralParameterModel</code>.
   *
   * @param       name                    the identifying name of the parameter model
   * @param       label                   the logical label of the parameter model
   * @param       documentation           the documentation of the parameter model
   * @param       type                    the Java type of the values for the parameter
   */
  public DateParameterModel(String name, String label, String documentation, Class type) {
    super(name, label, documentation, type);
  }

  /**
   * Constructs a new <code>LiteralParameterModel</code>.
   *
   * @param       name                    the identifying name of the parameter model
   * @param       label                   the logical label of the parameter model
   * @param       documentation           the documentation of the parameter model
   * @param       type                    the Java type of the values for the parameter
   */
  public DateParameterModel(String name, LocalizedString label, LocalizedString documentation, Class type) {
    super(name, label, documentation, type);
  }

  public Object prepareValue(Object value) {
    try {
      return DateFormat.getDateInstance(DateFormat.DEFAULT).format(value);
    } catch (Exception e) {
      return "";
    }
  }

  public Object processValue(Object value) {
    if (value.equals(""))
      throw new IllegalArgumentException("ignore");

    try {
        Date date;
        if (((String)value).length() == 8 ) {
            // Behandlung der Eingabe von zweistellingen Jahreszahlen
            date = new Date(DateFormat.getDateInstance(DateFormat.SHORT).parse((String) value).getTime());
        } else {
            date = new Date(DateFormat.getDateInstance(DateFormat.DEFAULT).parse((String) value).getTime());
        }
        return date;
    } catch (ParseException e) {
      throw new IllegalArgumentException("ignore");
    }
  }
}

