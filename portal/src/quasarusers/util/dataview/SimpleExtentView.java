/*
* Created by IntelliJ IDEA.
* User: madams
* Date: Mar 25, 2002
* Time: 4:33:57 PM
* To change template for new class use
* Code Style | Class Templates options (Tools | IDE Options).
*/
package quasarusers.util.dataview;

//import com.sdm.quasar.businessobject.BusinessModule;

import com.sdm.quasar.businessobject.BusinessModuleNotFoundException;
import com.sdm.quasar.businessobject.BusinessObject;
import com.sdm.quasar.businessobject.BusinessObjectManager;
import com.sdm.quasar.businessobject.BusinessSystem;
import com.sdm.quasar.businessobject.TransactionMode;
import com.sdm.quasar.businessobject.ReturnMode;
import com.sdm.quasar.businessobject.extentview.server.model.SimpleExtentDataModel;
import com.sdm.quasar.continuation.AbstractContinuation;
import com.sdm.quasar.continuation.Continuation;
import com.sdm.quasar.continuation.StandardContinuationManager;
import com.sdm.quasar.dataview.model.CombinationPart;
import com.sdm.quasar.dataview.model.FilterPart;
import com.sdm.quasar.dataview.model.ParameterModel;
import com.sdm.quasar.dataview.model.SearchPart;
import com.sdm.quasar.dataview.model.SimpleSearchModel;
import quasarusers.util.dataview.ColumnModel;
import com.sdm.quasar.dataview.server.model.DataModel;
import com.sdm.quasar.dataview.server.model.FilterModel;
import com.sdm.quasar.dataview.server.model.QueryModel;
import com.sdm.quasar.dataview.server.persistence.PersistenceQuery;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.modelview.server.model.ObjectModel;
import com.sdm.quasar.modelview.server.persistence.PersistenceAttributeModel;
import com.sdm.quasar.persistence.AttributeModel;
import com.sdm.quasar.persistence.PersistenceException;
import com.sdm.quasar.persistence.Pool;
import com.sdm.quasar.persistence.PropertyModel;
import com.sdm.quasar.persistence.RelationshipModel;
import com.sdm.quasar.persistence.TypeModel;
import com.sdm.quasar.persistence.TypeModelNotFoundException;
import com.sdm.quasar.persistence.query.OrderingExpression;
import com.sdm.quasar.persistence.query.PredicateExpression;
import com.sdm.quasar.persistence.query.ResultExpression;
import com.sdm.quasar.persistence.query.SingleValuedResultExpression;
import com.sdm.quasar.util.Assertion;
import com.sdm.quasar.util.LocalizedString;
import com.sdm.quasar.util.registry.RegistryObject;
import com.sdm.quasar.view.server.SimpleViewDescription;
import quasarusers.util.ContinuationResultProcessor;
import quasarusers.util.businessobject.AbstractBrowseFunction;
import quasarusers.portal.ConfigurableRuntime;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Diese Klasse wird in Remis für DataViews zur Beschreibung des
 * Modells eingesetzt.
 *
 * @author Marco Schmickler
 * @author Matthias Rademacher
 */

//todo dpk 23/01/2003 -> MR kommentieren

public abstract class SimpleExtentView extends SimpleViewDescription {
  private Pool pool = null;
  private Class entityClass;
  private BusinessObject mBusinessObject;
  private Map pathMap;
  private SingleValuedResultExpression typeExpression;
  private DataModel dataModel;
  private String modelName;

  public SimpleExtentView(String name,
                          String label,
                          String documentation,
                          String viewClassName,
                          String viewServerClassName,
                          BusinessObject businessObject,
                          Class entityClass) {
    super(name, label, documentation, viewClassName, viewServerClassName);

    this.entityClass = entityClass;
    this.mBusinessObject = businessObject;

    try {
      this.typeExpression = quasarusers.portal.ConfigurableRuntime.getQueryManager().get(entityClass);
    } catch (PersistenceException e) {
      e.printStackTrace();
    }

    modelName = null;
  }

  public SimpleExtentView(String name,
                          String label,
                          String documentation,
                          String viewClassName,
                          String viewServerClassName,
                          BusinessObject businessObject,
                          Class entityClass,
                          String modelName) {
    this(name, label, documentation, viewClassName, viewServerClassName, businessObject, entityClass);

    this.modelName = modelName;
  }

  public void initialize() {
      getDataModel();
  }

  public AbstractBrowseFunction makeBrowseFunction() {
    initialize();

    return new AbstractBrowseFunction(entityClass,
                                      SimpleExtentView.this.getName() + "BrowseFunction",
                                      SimpleExtentView.this.getLabel(),
                                      SimpleExtentView.this.getDocumentation(),
                                      TransactionMode.OPTIONAL,
                                      ReturnMode.CONTINUATION) {
      public void browse(Keywords arguments) {
        open(null, arguments, null);
      }

      public boolean isAccessControlled() {
        return false;
      }
    };
  }

  public TypeModel getTypeModel() {
    try {
      return getPool().getTypeModel(entityClass);
    } catch (TypeModelNotFoundException e) {
      return null;
    }
  }

  public DataModel getDataModel() {
    if (dataModel == null) {
      pathMap = new HashMap();

      dataModel = makeDataModel();
      checkQueryModels();

        SimpleExtentDataModel extentDataModel = new SimpleExtentDataModel(entityClass,
                                                              getName(),
                                                              getLabel(),
                                                              getDocumentation(),
                                                              dataModel);

        BusinessObjectManager.BusinessObjectRegistry registry
                = BusinessObjectManager.getBusinessObjectManager().getRegistry();

        registry.registerProperty(mBusinessObject, extentDataModel);
    }

    return dataModel;
  }

  public void openForModify(Object object, Keywords arguments) {
    StandardContinuationManager continuationManager
            = StandardContinuationManager.getContinuationManager();

    // Erzeugung einer eigenen Continuation, die einen boolean als Rückgabewert liefert. Dieser wird
    // als Rückgabewert der modify-Funktion verwendet.
    Continuation continuation = new AbstractContinuation(continuationManager) {
      public void continueWithResult(Object result) {
        super.continueWithResult(Boolean.TRUE);
      }
    };

    open(object, arguments, continuation);
  }

  public void open(Object object,
                   Keywords arguments,
                   Continuation continuation) {
    BusinessObjectManager BOManager
            = BusinessObjectManager.getBusinessObjectManager();

    try {
      arguments = (Keywords) arguments.clone();

      BusinessSystem businessSystem = getBusinessSystem(BOManager);

      getDataModel();

      arguments.addValue("entityType", entityClass.getName());
      arguments.addValue("businessObject", businessSystem.getName() + "." +
                                           mBusinessObject.getName());
      arguments.addValue("modelName", modelName);

      ConfigurableRuntime.getViewServerManager().openView(this,
                                                        null,
                                                        arguments, new ContinuationResultProcessor(continuation));
    } catch (Exception e) {
      BOManager.getContinuationManager().continueWithException(e);
    }
  }

  public void open(Object object,
                   Keywords arguments) {
    getDataModel();

    BusinessObjectManager BOManager
            = BusinessObjectManager.getBusinessObjectManager();

    try {
      arguments = (Keywords) arguments.clone();

      BusinessSystem businessSystem = getBusinessSystem(BOManager);

      arguments.addValue("entityType", entityClass.getName());
      arguments.addValue("businessObject", businessSystem.getName() + "." +
                                           mBusinessObject.getName());

      ConfigurableRuntime.getViewServerManager().openView(this,
                                                        null,
                                                        arguments);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private BusinessSystem getBusinessSystem(BusinessObjectManager BOManager)
          throws BusinessModuleNotFoundException {
    Assertion.checkNotNull(mBusinessObject, "mBusinessObject");

    BusinessSystem businessSystem = null;
    RegistryObject[] systems = BOManager.getSystems();

    for (int s = 0; s < systems.length; s++) {
      businessSystem = (BusinessSystem) systems[s];
      RegistryObject[] objects = BOManager.getRegistry().getComponents(businessSystem);

      for (int o = 0; o < objects.length; o++)
        if (objects[o] == mBusinessObject)
          return businessSystem;
    }

    throw new BusinessModuleNotFoundException(
            "BusinessSystem for " + mBusinessObject.getName() + " not found");
  }

  protected ColumnModel makeColumnModel(TypeModel typeModel,
                                        String name,
                                        String path,
                                        int columnIndex,
                                        boolean primaryKey) throws PersistenceException {
    return new ColumnModel(name,
                           new PersistenceAttributeModel(computePathProperty(typeModel, path)),
                           columnIndex,
                           primaryKey);
  }

  /**
   * Erzeugt ein Spaltenmodell, bei dem die Spaltenüberschrift
   * von der der DB-Spalte abweichen kann.
   */
  protected ColumnModel makeColumnModel(TypeModel typeModel,
                                        String name,
                                        final String title,
                                        String path,
                                        int columnIndex,
                                        boolean primaryKey) throws PersistenceException {
    return new ColumnModel(name,
                           new PersistenceAttributeModel(computePathProperty(typeModel, path)),
                           columnIndex,
                           primaryKey) {
      public LocalizedString getLabel() {
        return new LocalizedString(title);
      }
    };
  }

  /**
   * Erzeugt ein Spaltenmodell, bei dem die Spaltenüberschrift
   * von der der DB-Spalte abweichen kann. Name und Spaltenüberschrift
   * sind identisch. Sollte nur benutzt werden, wenn der Name keine
   * Umlaute und Sonderzeichen (wie "-", ".") enthält.<p>
   *
   * Außerdem wird das Standard-TypeModel verwendet (siehe {@link #getTypeModel}).
   *
   * @param  title       der Name und die Spaltenüberschrift der Spalte
   * @param  path        die Pfadangabe für die Spalte
   * @param  columnIndex der Index der Spalte
   * @param  primaryKey  die Angabe, ob die Spalte der Primärschlüssel ist.
   *                     Falls <code>true</code> eingestellt ist, wird die
   *                     Spalte nicht angezeigt.
   * @return das Spaltenmodell
   */
  protected ColumnModel makeColumnModel(final String title,
                                        String path,
                                        int columnIndex,
                                        boolean primaryKey) throws PersistenceException {
    return new ColumnModel(title,
                           new PersistenceAttributeModel(computePathProperty(getTypeModel(), path)),
                           columnIndex,
                           primaryKey) {
      public LocalizedString getLabel() {
        return new LocalizedString(title);
      }
    };
  }

  /**
   * Erzeugt ein Spaltenmodell, bei dem die Spaltenüberschrift
   * von der der DB-Spalte abweichen kann. Name und Spaltenüberschrift
   * sind identisch. Sollte nur benutzt werden, wenn der Name keine
   * Umlaute und Sonderzeichen (wie "-", ".") enthält.<p>
   *
   * Außerdem wird das Standard-TypeModel verwendet (siehe {@link #getTypeModel}).
   *
   * @param  title       der Name und die Spaltenüberschrift der Spalte
   * @param  path        die Pfadangabe für die Spalte
   * @param  columnIndex der Index der Spalte
   * @return das Spaltenmodell
   */
  protected ColumnModel makeColumnModel(final String title,
                                        String path,
                                        int columnIndex) throws PersistenceException {
    return new ColumnModel(title,
                           new PersistenceAttributeModel(computePathProperty(getTypeModel(), path)),
                           columnIndex,
                           false) {
      public LocalizedString getLabel() {
        return new LocalizedString(title);
      }
    };
  }

  /**
   * Kopiert ein QueryModel, um die Sortierung nach der Spaltenüberschrift
   * zu unterstützen.
   *
   * @param  name              der Name und das Label des neuen QueryModels
   * @param  querystandard     die Kopiervorlage
   * @param  resultExpressions die Result Expressions der Kopiervorlage
   * @param  sortPath          die Pfadangabe des Sortierkriteriums
   * @return das Kopierte QueryModel
   */
  protected QueryModel makeCopyQueryModel(String name,
                                          QueryModel querystandard,
                                          ResultExpression[] resultExpressions,
                                          String sortPath) throws PersistenceException {
    QueryModel query = DataViewHelper.copyQueryModel(querystandard, name, name, "");

    query.setDataIteratorQuery(new PersistenceQuery(
            resultExpressions, null, null, null,
            new OrderingExpression[]{(computePathExpression(sortPath)).ascending()}));

    return query;
  }

  /**
   * Kopiert ein QueryModel, um die Sortierung nach der Spaltenüberschrift
   * zu unterstützen.
   *
   * @param  name              der Name und das Label des neuen QueryModels
   * @param  querystandard     die Kopiervorlage
   * @param  resultExpressions die Result Expressions der Kopiervorlage
   * @param  sortPath          die Pfadangabe des Sortierkriteriums
   * @return das Kopierte QueryModel
   */
  protected QueryModel makeCopyQueryModel(String name,
                                          QueryModel querystandard,
                                          ResultExpression[] resultExpressions,
                                          PredicateExpression selection,
                                          String sortPath) throws PersistenceException {
    QueryModel query = DataViewHelper.copyQueryModel(querystandard, name, name, "");

    query.setDataIteratorQuery(new PersistenceQuery(
            resultExpressions, selection, null, null,
            new OrderingExpression[]{computePathExpression(sortPath).ascending()}));

    return query;
  }

  /**
   * Kopiert ein QueryModel, um die Sortierung nach der Spaltenüberschrift
   * zu unterstützen.
   *
   * @param  name              der Name und das Label des neuen QueryModels
   * @param  querystandard     die Kopiervorlage
   * @param  resultExpressions die Result Expressions der Kopiervorlage
   * @param  sortPaths         die Pfadangaben der Sortierkriterien
   * @return das Kopierte QueryModel
   */
  protected QueryModel makeCopyQueryModel(String name,
                                          QueryModel querystandard,
                                          ResultExpression[] resultExpressions,
                                          String[] sortPaths) throws PersistenceException {
    QueryModel query = DataViewHelper.copyQueryModel(querystandard, name, name, "");

    final int length = sortPaths.length;
    OrderingExpression[] orderingExpressions = new OrderingExpression[length];

    for (int i = 0; i < length; i++)
      orderingExpressions[i] = computePathExpression(sortPaths[i]).ascending();

    query.setDataIteratorQuery(new PersistenceQuery(
            resultExpressions, null, null, null, orderingExpressions));

    return query;
  }

  /**
   * Erstellt das Default-Suchmodell. Dabei wird insbes. das HTML-Template
   * festgelegt.<p>
   *
   * Die Standardimplementierung legt UND-verknüpfte Filterparts mit Leerstrings
   * an. Falls dies nicht passt, kann die Methode spezialisiert werden.
   *
   * @param  name         der Name des HTML-Templates
   * @param  filterModels die FilterModels
   * @return das Suchmodell
   */
  public SimpleSearchModel makeDefaultSearchModel(String name, FilterModel[] filterModels) {
    final int length = filterModels.length;
    final SearchPart[] parts = new SearchPart[length];

    for (int i = 0; i < length; i++) {
      final FilterModel filterModel = filterModels[i];
      final ParameterModel[] parameterModels = filterModel.getParameterModels();
      final int pLength = parameterModels.length;
      Object[] params = new Object[pLength];

      for (int j = 0; j < pLength; j++)
        params[j] = "";

      parts[i] = new FilterPart(filterModels[i].getName(), params);
    }

    return new SimpleSearchModel(
            name,
            "",
            "",
            new CombinationPart(parts, CombinationPart.AND));
  }

  /**
   * Ermittelt zu einer Pfadangabe in Punktnotation die {@link SingleValuedResultExpression}.
   * Outer Joins werden mit dem Zeichen + dargestellt.
   *
   * @param   path        die Pfadangabe in Punktnotation
   * @return  die {@link SingleValuedResultExpression} des Attributs
   *
   */
  public SingleValuedResultExpression computePathExpression(String path)
          throws PersistenceException {
    SingleValuedResultExpression expression
            = (SingleValuedResultExpression) pathMap.get(path);

    if (expression == null) {
      expression = makePathExpression(path);

      pathMap.put(path, expression);
    }

    return expression;
  }

  protected abstract DataModel makeDataModel();

  protected AttributeModel computePathProperty(TypeModel typeModel,
                                               String path)
          throws PersistenceException {
    PropertyModel propertyModel;
    StringTokenizer pathTokenizer = new StringTokenizer(path, ".+");

    while (pathTokenizer.hasMoreTokens()) {
      propertyModel = typeModel.getPropertyModel(pathTokenizer.nextToken());

      if (propertyModel instanceof AttributeModel) {
        return (AttributeModel) propertyModel;
      }

      typeModel = ((RelationshipModel) propertyModel).getToTypeModel();
    }

    return null;
  }

  protected SingleValuedResultExpression makePathExpression(String path)
          throws PersistenceException {
    SingleValuedResultExpression expression;
    String trimmedPath = trim(path);

    if (trimmedPath.equals(""))
      expression = getTypeExpression();
    else {
      String prefix = pathPrefix(trimmedPath);
      String postfix = pathPostfix(trimmedPath);

      if (isOuterJoin(path))
        expression = computePathExpression(prefix).getAllowingNull(postfix);
      else
        expression = computePathExpression(prefix).get(postfix);
    }
    return expression;
  }

  protected Pool getPool() {
    return (pool == null) ?
            (pool = (Pool) BusinessObjectManager.getBusinessObjectManager().getSingleton(Pool.class)) :
            pool;
  }

  protected SingleValuedResultExpression getTypeExpression() {
    return typeExpression;
  }

  /*
   * Utility methods for computePathExpression
   */
  private static final String pathPrefix(String path) {
    return path.substring(0, Math.max(path.lastIndexOf("."),
                                      path.lastIndexOf("+")) + 1);
  }

  private static final String pathPostfix(String path) {
    return path.substring(Math.max(path.lastIndexOf("."),
                                   path.lastIndexOf("+")) + 1);
  }

  private static final boolean isOuterJoin(String path) {
    return path.endsWith("+");
  }

  private static final String trim(String path) {
    if ((path.endsWith("+")) || (path.endsWith(".")))
      return path.substring(0, path.length() - 1);
    else
      return path;
  }

  private void checkQueryModels() {
    // Prüfen, ob Attributemodels gesetzt
    final QueryModel[] models = dataModel.getQueryModels();
    final int length = models.length;
    boolean panic = false;

    for (int i = 0; i < length; i++)
      if (models[i] == null) {
        System.out.println("ERROR: QueryModel with 0-based index " + i + " is null");
        panic = true;
      }
    else {
//        checkAttributeModels(models[i]);
//        checkRelationshipModels(models[i]);
      }
    if (panic)
      throw new RuntimeException("QueryModel(s) not defined");
  }

  private void checkAttributeModels(ObjectModel objectModel) {
    // Prüfen, ob ReationshipModels gesetzt
    final com.sdm.quasar.modelview.server.model.AttributeModel[] models = objectModel.getAttributeModels(true);
    final int length = models.length;
    boolean panic = false;

    for (int i = 0; i < length; i++)
      if (models[i] == null) {
        System.out.println("ERROR: AttributeModel with 0-based index " + i + " is null");
        panic = true;
      }

    if (panic)
      throw new RuntimeException("AttributgeModel(s) not defined in ObjectModel " + objectModel.getName());
  }

  private void checkRelationshipModels(ObjectModel objectModel) {
    // Prüfen, ob ReationshipModels gesetzt
    final com.sdm.quasar.modelview.server.model.RelationshipModel[] models = objectModel.getRelationshipModels(true);
    final int length = models.length;
    boolean panic = false;

    for (int i = 0; i < length; i++)
      if (models[i] == null) {
        System.out.println("ERROR: RelationshipModel with 0-based index " + i + " is null");
        panic = true;
      }

    if (panic)
      throw new RuntimeException("RelationshipModel(s) not defined in ObjectModel " + objectModel.getName());
  }

}
