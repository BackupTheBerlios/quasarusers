/*
 * Created by IntelliJ IDEA.
 * User: rademach
 * Date: 28.11.2002
 * Time: 14:20:17
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package quasarusers.util.dataview;

import com.sdm.quasar.dataview.server.model.DataModel;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.businessobject.*;
import com.sdm.quasar.businessobject.extentview.server.model.ExtentDataModel;
import quasarusers.util.businessobject.StammdatenProperty;

/**
 * Dies ist ein ParameterModel für eine Liste (Combobox) für die DataView.
 * Zum Aufbau der Liste wird eine Query ausgeführt, die  als
 * {@link StammdatenProperty} bei einem Business Object registriert ist.
 *
 * @see StammdatenProperty
 * @author Marco Schmickler
 */
public class QueryEnumerationParameterModel extends EnumerationParameterModel {
  private StammdatenProperty stammdatenProperty;
  private String businessObjectName;
  private String queryName;

  /**
   * Erzeugt ein <code>QueryEnumerationParameterModel</code>
   *
   * @param  name                der Name des {@link com.sdm.quasar.dataview.model.ParameterModel}s
   * @param  label               das Label des {@link com.sdm.quasar.dataview.model.ParameterModel}s
   * @param  documentation       die Dokumentation des {@link com.sdm.quasar.dataview.model.ParameterModel}s
   * @param  businessObjectName  der Names des Business Objects, in dem die
   *                             Query registriert ist
   * @param  queryName           der Name der {@link StammdatenProperty} für
   *                             die Query
   */
  public QueryEnumerationParameterModel(String name,
                                        String label,
                                        String documentation,
                                        String businessObjectName,
                                        String queryName) {
    super(name, label, documentation, new String[0], new Object[0]);

    this.queryName = queryName;
    this.businessObjectName = businessObjectName;
  }

  public StammdatenProperty getStammdatenProperty() {
    if (stammdatenProperty == null)
      stammdatenProperty
              = StammdatenProperty.makeStammdatenProperty(businessObjectName,
                                                          queryName);

    return stammdatenProperty;
  }

  public String[] getParameterNames() {
    return getStammdatenProperty().getNames();
  }

  public Object[] getParameterValues() {
    return getStammdatenProperty().getValues();
  }
}
