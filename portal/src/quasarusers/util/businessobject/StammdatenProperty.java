/*
 * Created by IntelliJ IDEA.
 * User: rademach
 * Date: 28.11.2002
 * Time: 13:54:51
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package quasarusers.util.businessobject;

import com.sdm.quasar.businessobject.AbstractBusinessObjectProperty;
import com.sdm.quasar.businessobject.BusinessObject;
import com.sdm.quasar.businessobject.BusinessObjectManager;
import com.sdm.quasar.businessobject.BusinessObjectNotFoundException;
import com.sdm.quasar.businessobject.BusinessObjectProperty;
import com.sdm.quasar.businessobject.PropertySelector;
import com.sdm.quasar.persistence.query.PreparedQuery;
import com.sdm.quasar.persistence.query.QueryResult;
import com.sdm.quasar.transaction.implementation.LocalTransactionManager;
import quasarusers.portal.ConfigurableRuntime;

import java.util.ArrayList;

/**
 * Mit dieser Klasse wird eine Query bei einem Business Object registriert,
 * die von einem {@link quasarusers.util.dataview.QueryEnumerationParameterModel} abgefragt wird.
 *
 * @see quasarusers.util.dataview.QueryEnumerationParameterModel
 * @author Marco Schmickler
 */
public class StammdatenProperty extends AbstractBusinessObjectProperty {
  private final static Object[] NO_OBJECTS = new Object[0];
  private Object[] values;
  private String[] names;
  private PreparedQuery query;
  private Class type;

  /**
   * Erzeugt eine <code>StammdatenProperty</code>.
   *
   * @param  name  der Name der {@link BusinessObjectProperty}
   * @param  query die zur registrierende Query
   * @param  type  der Modelltyp der Ergebnisobjekte
   */
  public StammdatenProperty(String name, PreparedQuery query, Class type) {
    super(name, name, "");

    this.query = query;
    this.type = type;
  }

  public Class getType() {
    return type;
  }

  public Object[] getValues() {
    if (values == null)
      loadElements();

    return values;
  }

  public String[] getNames() {
    if (names == null)
      loadElements();

    return names;
  }

  /** Führt die Query erneut aus. */
  public synchronized void loadElements() {
    try {
      LocalTransactionManager transactionManager = ConfigurableRuntime.getTransactionManager();

      transactionManager.begin();

      QueryResult queryResult = query.execute(NO_OBJECTS);

      ArrayList values = new ArrayList();
      ArrayList names = new ArrayList();

      values.add("");
      names.add("");

      while (queryResult.hasNext()) {
        Object[] result = ((Object[]) queryResult.next());

        names.add(result[0]);
        values.add(result[1]);
      }

      queryResult.close();

      this.names = (String[]) names.toArray(new String[names.size()]);
      this.values = (Object[]) values.toArray(new Object[values.size()]);

      transactionManager.commit();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static StammdatenProperty makeStammdatenProperty(String businessObjectName, String name) {
    BusinessObjectManager businessObjectManager = BusinessObjectManager.getBusinessObjectManager();

    try {
      BusinessObject businessObject = businessObjectManager.getObject(businessObjectName);

      BusinessObjectProperty[] stammdatenProperty = (BusinessObjectProperty[]) businessObject.getProperties(new PropertySelector(StammdatenProperty.class));

      int eLength = stammdatenProperty.length;

      for (int i = 0; i < eLength; i++) {
        StammdatenProperty stammdaten = (StammdatenProperty) stammdatenProperty[i];

        if (stammdaten.getName().equals(name))
          return stammdaten;
      }

      return null;
    } catch (BusinessObjectNotFoundException e) {
      throw new RuntimeException("BusinessObject " + businessObjectName + " not found");
    }
  }
}
