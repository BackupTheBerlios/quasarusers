/*
 * Created by IntelliJ IDEA.
 * User: rademach
 * Date: 05.04.2002
 * Time: 18:20:10
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package quasarusers.portal;

import com.sdm.quasar.businessobject.BusinessObjectManager;
import com.sdm.quasar.persistence.Pool;
import com.sdm.quasar.persistence.query.PredicateExpression;
import com.sdm.quasar.persistence.query.QueryResult;
import com.sdm.quasar.persistence.query.ResultExpression;
import com.sdm.quasar.persistence.query.SingleValuedResultExpression;
import com.sdm.quasar.persistence.query.implementation.StandardQueryManager;
import com.sdm.quasar.session.SessionAccessDeniedException;
import com.sdm.quasar.util.Assertion;
import com.sdm.quasar.util.HashMapSize;
import de.sdm.sia.remis.modelle.Benutzer;
import de.sdm.sia.remis.modelle.Benutzergruppe;
import de.sdm.sia.remis.modelle.Recht;
import quasarusers.util.Today;
import quasarusers.util.ToplevelTransaction;

import javax.transaction.Transaction;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Ein <code>SessionBenutzer</code> repräsentiert innerhalb einer
 * {@link com.sdm.quasar.session.Session} einen {@link Benutzer}. Es
 * kann gleichzeitig mehrere <code>SessionBenutzer</code> zu einem
 * {@link Benutzer} geben.<p>
 *
 * Bei der Anmeldung wird eine {@link java.util.Map} mit den zu diesem Zeitpunkt gültigen
 * {@link Recht}en des {@link Benutzer}s aufgebaut. Diese gelten solange wie
 * die {@link com.sdm.quasar.session.Session} aktiv ist. Falls die Rechte sich
 * währenddessen ändern, schlagen diese also nicht auf laufende Sessions durch.
 *
 * @see quasarusers.portal.SiaAuthorizationConfiguration
 * @see de.sdm.sia.remis.modelle.Benutzer
 * @see de.sdm.sia.remis.modelle.Recht
 * @author Matthias Rademacher
 */
public final class SessionBenutzer {
  /** Die Kennung des Benutzers */
  String mKennung;

  /** Die Namen der Rechte des Benutzers */
  Set mRechte = new HashSet(HashMapSize.LARGE);

  /**
   * Erzeugt einen <code>SessionBenutzer</code>.
   *
   * @param  kennung                       die Kennung des Benutzers
   * @param  verschluesseltesPasswort      das MP5-verschlüsselte Passwort
   *                                       des Benutzers
   * @throws  SessionAccessDeniedException falls keine Rechte ermittelt werden können. Der Benutzer hat
   *                                       dann entweder keine Rechte oder Kennung/Passwort stimmen nicht
   *                                       oder die Kennung ist abgelaufen. Außerdem kann ein Fehler beim
   *                                       Ausführen des DB-Statements aufgetreten sein.
   */
  public SessionBenutzer(String kennung, String verschluesseltesPasswort) throws SessionAccessDeniedException {
    try {
      Assertion.checkNotNull(kennung, "kennung");
      Assertion.checkNotNull(verschluesseltesPasswort, "verschluesseltesPasswort");

      mKennung = kennung;

      BusinessObjectManager businessObjectManager = BusinessObjectManager.getBusinessObjectManager();
      Pool pool = (Pool) businessObjectManager.getSingleton(Pool.class);
      Transaction userTransaction = null;
      StandardQueryManager queryManager = quasarusers.portal.ConfigurableRuntime.getQueryManager();

      try {
        userTransaction = new ToplevelTransaction();
        /*
         * Ermittle alle Namen von Rechten, die zu einer Benutzergruppe gehören, die einen Benutzer
         * zugeordnet haben, der den angegebenen Namen und das angegebene Passwort hat und der
         * zusätzlich weder gesperrt noch gelöscht markiert ist.
         */
        SingleValuedResultExpression rechtExression = queryManager.get(Recht.class);
        SingleValuedResultExpression benutzergruppeExpression = rechtExression.get(Recht.BENUTZERGRUPPE);
        SingleValuedResultExpression benutzerExpression = benutzergruppeExpression.get(Benutzergruppe.BENUTZER);

        PredicateExpression kennungExpression = benutzerExpression.get(Benutzer.KENNUNG).equal("?1");
        PredicateExpression passwortExpression = benutzerExpression.get(Benutzer.PASSWORT).equal("?2");
        PredicateExpression gesperrtExpression = benutzerExpression.get(Benutzer.ISTGESPERRT).equal("?3");
        PredicateExpression geloeschtExpression = benutzerExpression.get(Benutzer.ISTGELOESCHT).equal("?4");
        PredicateExpression gueltigVonExpression = benutzerExpression.get(Benutzer.GUELTIGVON).lessThanOrEqual("?5");
        PredicateExpression gueltigBisExpression = benutzerExpression.get(Benutzer.GUELTIGBIS).greaterThanOrEqual("?6");
        PredicateExpression gueltigVon2Expression = benutzergruppeExpression.get(Benutzergruppe.GUELTIGVON).lessThanOrEqual("?7");
        PredicateExpression gueltigBis2Expression = benutzergruppeExpression.get(Benutzergruppe.GUELTIGBIS).greaterThanOrEqual("?8");
        PredicateExpression gesperrt2Expression = benutzergruppeExpression.get(Benutzergruppe.ISTGESPERRT).equal("?9");

        PredicateExpression gesamtExpression = kennungExpression.and(
                passwortExpression.and(
                        gesperrtExpression.and(geloeschtExpression).and(
                                gueltigVonExpression.and(gueltigBisExpression).and(
                                        gueltigVon2Expression.and(gueltigBis2Expression).and(
                                                gesperrt2Expression)))));

        Date today = Today.today();

        QueryResult results = queryManager.queryAll(
                pool,
                new ResultExpression[]{rechtExression.get(Recht.NAME)},
                gesamtExpression,
                new Object[]{kennung,
                             verschluesseltesPasswort,
                             Boolean.FALSE,
                             Boolean.FALSE,
                             today,
                             today,
                             today,
                             today,
                             Boolean.FALSE});

        int size;

        for (size = 0; results.hasNextResult(); size++) {
          Object recht = ((Object[]) results.nextResult())[0];

//          System.out.println("Adding Recht " + recht + " for " + hashCode());

          mRechte.add(recht);
        }

        if (size == 0)
          throw new SessionAccessDeniedException();
      } finally {
        if (userTransaction != null)
          userTransaction.rollback(); // da wir nichts geändert haben
      }
    } catch (Exception e) {
      e.printStackTrace(); // TODO: Exception wrappen?
      throw new SessionAccessDeniedException();
    }
  }

  /**
   * Prüft, ob der Benutzer das angegebene Recht hat.
   *
   * @param  recht der Name des Rechts
   * @return <code>true</code>, wenn der Benutzer das Recht hat.
   *         <code>false</code>, wenn der Benutzer das Recht nicht hat.
   */
  public boolean hatRecht(String recht) {
//    return true;
    return mRechte.contains(recht);
  }

  public String getKennung() {
    return mKennung;
  }

  public String toString() {
    return "Benutzer " + mKennung + " ID " + hashCode();
  }
}
