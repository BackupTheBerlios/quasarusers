/*
 * Created by IntelliJ IDEA.
 * User: rademach
 * Date: 11.04.2002
 * Time: 08:36:32
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package quasarusers.util;

import java.sql.Date;
import java.util.Calendar;

/**
 * Diese Klasse bietet die Methode {@link #today}, die das
 * Tagesdatum liefert.
 *
 * author Matthias Rademacher
 */
public final class Today {
  /** Die Dauer eines Tags in ms. */
  public final static long DAY_LENGTH = 24 * 60 * 60 * 1000;

  /** Das DB-Seitig verwendete Datum für "Undefiniert". */
  public final static Date UNDEF_DATE = makeUndefDate();

  /**
   * Ermittelt das Tagesdatum (0 Uhr in der Default-Zeitzone).
   *
   * @return das Tagesdatum
   */
  public final static Date today() {
    Calendar cal = Calendar.getInstance();

    cal.setTime(/* now = */ new Date(System.currentTimeMillis()));
    cal.set(Calendar.HOUR, 0);
    cal.set(Calendar.AM_PM, Calendar.AM);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);

    return new Date(cal.getTime().getTime());
  }

  private final static Date makeUndefDate() {
    Calendar cal = Calendar.getInstance();

    cal.set(Calendar.YEAR, 4711);
    cal.set(Calendar.DAY_OF_MONTH, 31);
    cal.set(Calendar.MONTH, Calendar.DECEMBER);
    cal.set(Calendar.HOUR, 11);
    cal.set(Calendar.AM_PM, Calendar.PM);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);

    return new Date(cal.getTime().getTime());
  }
}
