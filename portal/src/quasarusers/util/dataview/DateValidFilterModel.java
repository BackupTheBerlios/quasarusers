/*
 * Created by IntelliJ IDEA.
 * User: rademach
 * Date: 13.11.2002
 * Time: 10:35:45
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package quasarusers.util.dataview;

import com.sdm.quasar.dataview.server.model.FilterModel;
import com.sdm.quasar.dataview.model.ParameterModel;
import com.sdm.quasar.dataview.model.FilterPart;

import java.sql.Date;

import quasarusers.util.Today;

/**
 * Erzeugt ein FilterModel für eine Combobox mit den Auswahlmöglichkeiten
 * "", "ja", "nein".
 * <ol>
 * <li><i>alle</i>:          Filter wird ignoriert
 * <li><i>gültige</i>:       datum &gt;= today<p>
 *                            &lt;=&gt; ((datum &gt;= today) and (datum &lt;= 1.1.3000))
 * <li><i>nicht gültige</i>: datum &lt; today<p>
 *                           &lt;=&gt; ((datum &gt;= 1.1.1970) and (datum &lt; heute)</ol>
 *
 * Die Anbindung an die Query muss wie folgt durchgeführt werden:<p>
 *
 *     <code>filterModel.setDataIteratorFilter(
 *    computePathExpression(&lt;pfad&gt;).greaterThanOrEqual("?").and(
 *     computePathExpression(&lt;pfad&gt;).lessThan("?")));</code>

 * @author Markus Steindl
 * @author Matthias Rademacher
 */
public final class DateValidFilterModel extends FilterModel {
  private static final String[] VALUES = new String[]{"", "ja", "nein"};
  private static final Date PAST = new Date(0);
  private static final Date FUTURE = Date.valueOf("5000-01-01");

  /**
   * Erzeugt ein DateValidFilterModel.
   *
   * @param  name  der Name und das Label des FilterModels
   */
  public DateValidFilterModel(String name) {
    super(name, name, "",
        new ParameterModel[]{
          new EnumerationParameterModel(name, name, "", VALUES, VALUES) {
            public Object processValue(Object value) {
              value = super.processValue(value);

              if ("".equals(value))
                throw new IllegalArgumentException("Filter is ignored");

              if ("ja".equals(value))
                return Today.today();

              if ("nein".equals(value))
                return PAST;

              return value;
            }

            public Object prepareValue(Object value) {
              String[] parameterNames = getParameterNames();
              Object[] parameterValues = getParameterValues();

              if (value == null || value.equals(""))
                  return parameterNames[0];
              else if (value == PAST)
                  return parameterNames[2];

              return parameterNames[1];
            }

          },
          new ClonedParameterModel(name + "2", name) {
            public Object processValue(Object value,
                                       FilterPart filterPart) {
              return (filterPart.getValues()[0] == PAST) ? Today.today() : FUTURE;
            }
          }
        });
  }
}
