/*
 * Created by IntelliJ IDEA.
 * User: rademach
 * Date: 18.06.2002
 * Time: 17:33:08
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package quasarusers.util.dataview;

import com.sdm.quasar.businessobject.BusinessObjectManager;
import com.sdm.quasar.dataview.model.ParameterModel;
import com.sdm.quasar.dataview.server.model.FilterModel;
import com.sdm.quasar.persistence.Pool;
import com.sdm.quasar.persistence.query.OrderingExpression;
import com.sdm.quasar.persistence.query.QueryResult;
import com.sdm.quasar.persistence.query.ResultExpression;
import com.sdm.quasar.persistence.query.SingleValuedResultExpression;
import com.sdm.quasar.persistence.query.implementation.StandardQueryManager;
import com.sdm.quasar.transaction.implementation.LocalTransactionManager;
import com.sdm.quasar.util.Arrays;

import java.util.ArrayList;

/**
 * Dies Klasse beherbergt das Filtermodell für eine Aufzählung. Die
 * Auswahlmöglichkeiten werden aus der DB geladen.
 *
 * @author Matthias Rademacher
 */
public class EnumerationFilterModel extends FilterModel {
  /** Überdeckt das entsprechende Field der Vaterklasse. */
  protected ParameterModel[] parameterModels;
  boolean like = false;

  /**
   * Erstellt das Filtermodell für Organisationseinheiten.
   *
   * @param  name             der Name und das Label des Filtermodells
   * @param  clazz            die Klasse, deren Instanzen in der Combobox
   *                          dargestellt werden
   * @param  spaltenname      der darzustellende Spaltenname
   */
  public EnumerationFilterModel(final String name,
                                final Class clazz,
                                final String spaltenname) {
    this(name, name, clazz, spaltenname);
  }

  /**
   * Erstellt das Filtermodell für Organisationseinheiten.
   *
   * @param  name        der Name und das Label des Filtermodells
   * @param  clazz       die Klasse, deren Instanzen in der Combobox
   *                     dargestellt werden
   * @param  spaltenname der darzustellende Spaltennam
   * @param  orderSpalte der Spaltenname des Sortierkriteriums
   */
  public EnumerationFilterModel(final String name,
                                final Class clazz,
                                final String spaltenname,
                                final String orderSpalte) {
    this(name, name, clazz, spaltenname, orderSpalte);
  }

  /**
   * Erstellt das Filtermodell für Organisationseinheiten.
   *
   * @param  name             der Name und das Label des Filtermodells
   * @param  clazz            die Klasse, deren Instanzen in der Combobox
   *                          dargestellt werden
   * @param  spaltenname      der darzustellende Spaltenname
   * @param  additionalNames  die zusätzlichen Namen
   * @param  additionalValues die zusätzlichen Werte zu den Namen
   */
  public EnumerationFilterModel(final String name,
                                final Class clazz,
                                final String spaltenname,
                                final String[] additionalNames,
                                final String[] additionalValues) {
    this(name, name, clazz, spaltenname);
  }

  /**
   * Erstellt das Filtermodell für Organisationseinheiten.
   *
   * @param  name             der Name des Filtermodells
   * @param  label            das Label des Filtermodells
   * @param  clazz            die Klasse, deren Instanzen in der Combobox
   *                          dargestellt werden
   * @param  spaltenname      der darzustellende Spaltenname
   * @param  orderSpalte      der Spaltenname des Sortierkriteriums
   * @param  additionalNames  die zusätzlichen Namen
   * @param  additionalValues die zusätzlichen Werte zu den Namen
   */
  public EnumerationFilterModel(final String name,
                                String label,
                                final Class clazz,
                                final String spaltenname,
                                final String orderSpalte,
                                final String[] additionalNames,
                                final String[] additionalValues) {
    super(name,
          label,
          "",
          // Knostruktor muss abschlossen sein, um Anpassungen vorzunehmen
          new ParameterModel[0]);

    this.parameterModels = new ParameterModel[]{
            new EnumerationParameterModel(name,
                                          name,
                                          "",
                                          new String[0],
                                          new Object[0]) {
              private String[] mElemente;
              private String[] mWerte;

              public String[] getParameterNames() {
                if (mElemente == null)
                  try {
                    mElemente = loadElemente();
                  } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Loading Enumeration " + name + " failed!");
                    // ignore
                  }

                return mElemente;
              }

              public Object[] getParameterValues() {
                getParameterNames();

                return mWerte;
              }

              public Object processValue(Object value) {
                value = super.processValue(value);

                if ((value == null) || value.equals(""))
                  throw new IllegalArgumentException("Ignore");

                // ermöglicht %Suche bei Sucheinschränkungen des Benutzers
                if (like)
                  return "%" + value + "%";

                return value;
              }

              /**
               * Ermittelt die Elemente
               *
               * @return  die Namen der Organisation
               * @throws Exception bei DB-Fehlern
               */
              private String[] loadElemente() throws Exception {
                LocalTransactionManager transactionManager = quasarusers.portal.ConfigurableRuntime.getTransactionManager();

                transactionManager.begin();

                StandardQueryManager queryManager = quasarusers.portal.ConfigurableRuntime.getQueryManager();
                Pool pool = (Pool) BusinessObjectManager.getBusinessObjectManager().getSingleton(Pool.class);
                SingleValuedResultExpression Type = queryManager.get(clazz);
                SingleValuedResultExpression columnExpr = Type.get(spaltenname);
                SingleValuedResultExpression orderExpr = Type.get(orderSpalte);

                QueryResult queryResult = queryManager.queryAll(pool,
                                                                new ResultExpression[]{columnExpr},
                                                                null, // alle Datensätze
                                                                new OrderingExpression[]{orderExpr.ascending()},
                                                                new Object[0]);

                ArrayList result = new ArrayList();
                ArrayList werte = new ArrayList();

                result.add("");
                werte.add("");

                while (queryResult.hasNext()) {
                  Object organisation = ((Object[]) queryResult.next())[0];

                  result.add(organisation);
                  werte.add(organisation);
                }

                queryResult.close();

                {
                  final int length = additionalNames.length;
                  for (int i = 0; i < length; i++) {
                    result.add(additionalNames[i]);
                    werte.add(additionalValues[i]);
                  }
                }

                String[] werte2 = (String[]) result.toArray(new String[result.size()]);
                mWerte = (String[]) werte.toArray(new String[werte.size()]);

                transactionManager.commit();

                return werte2;
              }
            }
          };
  }

  /**
   * Erstellt das Filtermodell für Organisationseinheiten.
   *
   * @param  name             der Name des Filtermodells
   * @param  label            das Label des Filtermodells
   * @param  clazz            die Klasse, deren Instanzen in der Combobox
   *                          dargestellt werden
   * @param  spaltenname      der darzustellende Spaltenname
   * @param  additionalNames  die zusätzlichen Namen
   * @param  additionalValues die zusätzlichen Werte zu den Namen
   */
  public EnumerationFilterModel(final String name,
                                String label,
                                final Class clazz,
                                final String spaltenname,
                                final String[] additionalNames,
                                final String[] additionalValues) {
    this(name, label, clazz, spaltenname, spaltenname, additionalNames,
         additionalValues);
  }

  /**
   * Erstellt das Filtermodell für Organisationseinheiten.
   *
   * @param  name        der Name des Filtermodells
   * @param  label       das Label des Filtermodells
   * @param  clazz       die Klasse, deren Instanzen in der Combobox
   *                     dargestellt werden
   * @param  spaltenname der darzustellende Spaltenname
   */
  public EnumerationFilterModel(final String name,
                                String label,
                                final Class clazz,
                                final String spaltenname) {
    this(name, label, clazz, spaltenname, spaltenname, new String[0], new String[0]);
  }

  /**
   * Erstellt das Filtermodell für Organisationseinheiten.
   *
   * @param  name        der Name des Filtermodells
   * @param  label       das Label des Filtermodells
   * @param  clazz       die Klasse, deren Instanzen in der Combobox
   *                     dargestellt werden
   * @param  spaltenname der darzustellende Spaltennam
   * @param  orderSpalte der Spaltenname des Sortierkriteriums
   */
  public EnumerationFilterModel(final String name,
                                String label,
                                final Class clazz,
                                final String spaltenname,
                                final String orderSpalte) {
    this(name, label, clazz, spaltenname, orderSpalte, new String[0], new String[0]);
  }

  /**
   * Returns the ParameterModels of the model.
   *
   * @return	the ParameterModels of the model
   */
  public ParameterModel[] getParameterModels() {
    return parameterModels;
  }

  /**
   * Fügt ein {@link ParameterModel} hinzu.
   *
   * @param	 parameterModel das zusätzliche {@link ParameterModel}
   */
  public void addParameterModel(ParameterModel parameterModel) {
    parameterModels = (ParameterModel[]) Arrays.add(ParameterModel.class,
                                                    parameterModels,
                                                    parameterModel);
  }

  public void setLike(boolean like) {
    this.like = like;
  }
}
