package quasarusers.util.modelview.server;

import com.sdm.quasar.businessobject.AccessController;
import com.sdm.quasar.businessobject.BusinessObject;
import com.sdm.quasar.businessobject.BusinessObjectManager;
import com.sdm.quasar.businessobject.BusinessObjectNotFoundException;
import com.sdm.quasar.businessobject.BusinessObjectProperty;
import com.sdm.quasar.businessobject.BusinessObjectPropertyNotFoundException;
import com.sdm.quasar.continuation.AbstractContinuation;
import com.sdm.quasar.continuation.Continuation;
import com.sdm.quasar.continuation.StandardContinuationManager;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.modelview.server.model.AttributeModel;
import com.sdm.quasar.modelview.server.model.LockMode;
import com.sdm.quasar.modelview.server.model.ObjectModel;
import com.sdm.quasar.modelview.server.model.PropertyModelNotFoundException;
import com.sdm.quasar.modelview.server.model.RelationshipModel;
import com.sdm.quasar.modelview.server.persistence.PersistenceAttributeModel;
import com.sdm.quasar.modelview.server.persistence.PersistenceRelationshipModel;
import com.sdm.quasar.persistence.PersistenceException;
import com.sdm.quasar.persistence.Pool;
import com.sdm.quasar.persistence.TypeModel;
import com.sdm.quasar.persistence.query.PreparedQuery;
import com.sdm.quasar.persistence.query.QueryResult;
import com.sdm.quasar.persistence.query.SingleValuedResultExpression;
import com.sdm.quasar.persistence.query.implementation.StandardQueryManager;
import com.sdm.quasar.transaction.implementation.LocalTransactionManager;
import com.sdm.quasar.util.Assertion;
import com.sdm.quasar.util.LocalizedMessage;
import com.sdm.quasar.util.LocalizedStrings;
import com.sdm.quasar.validation.Validation;
import com.sdm.quasar.validation.ValidationController;
import com.sdm.quasar.validation.ValidationException;
import com.sdm.quasar.dataview.server.model.DataModel;
import quasarusers.util.modelview.validation.CrossValidation;

import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import javax.transaction.TransactionManager;
import java.sql.Date;
import java.util.Locale;

/**
 * Dies ist die REMIS-Basisklasse für EntityViews von modellbasierten Präsentationen.
 * Sie stellt Hilfsmittel bereit, die die Erstellung einer modellbasierten Präsentation
 * vereinfachen. Dies sind vor allem:<ul>
 * <li>1:n-Beziehungen für die Darstellung in Comboboxen
 * <li>Häufig auftretende Validierungen</ul>.
 *
 * @author Marco Schmickler
 * @author Matthias Rademacher
 */
public abstract class SimpleEntityView extends EntityView {
  /** Names des Resource Bundles mit den Fehlermeldungen. */
  private final static String mResourceBundleName = SimpleEntityView.class.getName();

  /** Die lokalisierten Fehlermeldungen für den ProjektPflegen. */
  private final static LocalizedStrings mMessages =
          new LocalizedStrings(mResourceBundleName);

  private final static StandardQueryManager queryManager = quasarusers.portal.ConfigurableRuntime.getQueryManager();

  private Pool pool;

  public SimpleEntityView(String name, String label, String documentation, String viewClassName) {
    super(name, label, documentation, viewClassName, null);
  }

  public void openForModify(Object object, Keywords arguments) {
    StandardContinuationManager continuationManager = StandardContinuationManager.getContinuationManager();

    // Erzeugung einer eigenen Continuation, die einen boolean als Rückgabewert liefert. Dieser wird
    // als Rückgabewert der modify-Funktion verwendet.
    Continuation continuation = new AbstractContinuation(continuationManager) {
      public void continueWithResult(Object result) {
        super.continueWithResult(Boolean.TRUE);
      }
    };

    try {
      if (object instanceof Object[])
        object = getPool().lookup(getObjectModel().getType(), (Object[]) object);
    } catch (PersistenceException e) {
      e.printStackTrace();
    }

    open(object, arguments, continuation);
  }

  public Pool getPool() {
    return (pool == null) ? (pool = (Pool) BusinessObjectManager.getBusinessObjectManager().getSingleton(Pool.class)) : pool;
  }

  public static StandardQueryManager getQueryManager() {
    return queryManager;
  }

  public static final RelationshipModel makeRelationshipModel(TypeModel typeModel, String name, ObjectModel objectModel, LockMode lockMode) throws PersistenceException {
    return new PersistenceRelationshipModel((com.sdm.quasar.persistence.RelationshipModel) typeModel.getPropertyModel(name),
                                            objectModel, false, lockMode);
  }

  public static final AttributeModel makeAttributeModel(TypeModel typeModel, String name) throws PersistenceException {
    return new PersistenceAttributeModel((com.sdm.quasar.persistence.AttributeModel) typeModel.getPropertyModel(name));
  }

  /**
   * Baut ein RelationshipModel (incl. dem Ziel-ObjectModel) für eine
   * 1:n-Beziehung, mit dessen Hilfe die referenzierten Objekte in einer
   * ComboBox dargestellt werden können.
   *
   * @param  persistentSourceType          die Klasse des persistenten Quellmodells
   * @param  persistentDestinationType     die Klasse des persistenten Zielmodells
   * @param  relationshipName              der Name der darzustellenden Beziehung
   * @param  columnName                    der Name der Spalte, der in der Combobox
   *                                       dargestellt werden soll.
   * @return das RelationshipModel, das vom Aufrufer an das Quell-ObjectModel
   *         angedockt werden muss
   */
  protected RelationshipModel makeRelationshipModel(Class persistentSourceType,
                                                    Class persistentDestinationType,
                                                    String relationshipName,
                                                    String columnName)
          throws PersistenceException {
    final TypeModel persistentTypeModel = getPool().getTypeModel(persistentSourceType);

    final com.sdm.quasar.persistence.RelationshipModel persistentRelationshipModel
            = (com.sdm.quasar.persistence.RelationshipModel) persistentTypeModel.getPropertyModel(relationshipName);

    final ObjectModel destinationObjectModel = new MatchObjectModel(
            relationshipName,
            persistentRelationshipModel.getLabel(),
            persistentDestinationType,
            columnName,
            persistentRelationshipModel.isRequired() ? MatchObjectModel.REQUIRED : MatchObjectModel.NOT_REQUIRED); // Leerauswahl möglich

    return new PersistenceRelationshipModel(
            persistentRelationshipModel,
            destinationObjectModel,
            false,
            LockMode.REFERENCE) {
      public boolean validateChildObjects() {
        return true; // Sorgt dafür, dass Pflichtbeziehung validiert wird
      }
    };
  }

  /**
   * Baut ein RelationshipModel (incl. dem Ziel-ObjectModel) für eine
   * 1:n-Beziehung, mit dessen Hilfe die referenzierten Objekte in einer
   * ComboBox dargestellt werden können.
   *
   * @param  persistentSourceType          die Klasse des persistenten Quellmodells
   * @param  persistentDestinationType     die Klasse des persistenten Zielmodells
   * @param  relationshipName              der Name der darzustellenden Beziehung
   * @param  query                         die Query, die in Spalte 0 die OId und in der/den
   *                                       folgenden Spalte(n) die Werte für die Combobox
   *                                       liefert
   * @return das RelationshipModel, das vom Aufrufer an das Quell-ObjectModel
   *         angedockt werden muss
   */
  protected RelationshipModel makeRelationshipModel(Class persistentSourceType,
                                                    Class persistentDestinationType,
                                                    String relationshipName,
                                                    PreparedQuery query)
          throws PersistenceException {
    final TypeModel persistentTypeModel = getPool().getTypeModel(persistentSourceType);

    final com.sdm.quasar.persistence.RelationshipModel persistentRelationshipModel
            = (com.sdm.quasar.persistence.RelationshipModel) persistentTypeModel.getPropertyModel(relationshipName);

    final ObjectModel destinationObjectModel = new MatchObjectModel(
            relationshipName,
            persistentRelationshipModel.getLabel(),
            persistentDestinationType,
            query,
            persistentRelationshipModel.isRequired() ? MatchObjectModel.REQUIRED : MatchObjectModel.NOT_REQUIRED); // Leerauswahl möglich

    return new PersistenceRelationshipModel(
            persistentRelationshipModel,
            destinationObjectModel,
            false,
            LockMode.REFERENCE) {
      public boolean validateChildObjects() {
        return true; // Sorgt dafür, dass Pflichtbeziehung validiert wird
      }
    };
  }

  /**
   * Baut ein RelationshipModel (incl. dem Ziel-ObjectModel) für eine
   * 1:n-Beziehung, mit dessen Hilfe die referenzierten Objekte in einer
   * ComboBox dargestellt werden können.
   *
   * @param  persistentSourceType          die Klasse des persistenten Quellmodells
   * @param  persistentDestinationType     die Klasse des persistenten Zielmodells
   * @param  relationshipName              der Name der darzustellenden Beziehung
   * @param  query                         die Query, die in Spalte 0 die OId und in der/den
   *                                       folgenden Spalte(n) die Werte für die Combobox
   *                                       liefert
   * @param  choiceClass                   die zu verwendende Spezialisierung von
   *                                       {@link MatchObjectModel.Choice}. Durch
   *                                       Spezialsieren von toString kann man
   *                                       die Darstellung der Combobox ändern.
   * @return das RelationshipModel, das vom Aufrufer an das Quell-ObjectModel
   *         angedockt werden muss
   */
  protected RelationshipModel makeRelationshipModel(Class persistentSourceType,
                                                    Class persistentDestinationType,
                                                    String relationshipName,
                                                    PreparedQuery query,
                                                    Class choiceClass)
          throws PersistenceException {
    final TypeModel persistentTypeModel = getPool().getTypeModel(persistentSourceType);

    final com.sdm.quasar.persistence.RelationshipModel persistentRelationshipModel
            = (com.sdm.quasar.persistence.RelationshipModel) persistentTypeModel.getPropertyModel(relationshipName);

    final ObjectModel destinationObjectModel = new MatchObjectModel(
            relationshipName,
            persistentRelationshipModel.getLabel(),
            persistentDestinationType,
            query,
            persistentRelationshipModel.isRequired() ? MatchObjectModel.REQUIRED : MatchObjectModel.NOT_REQUIRED,
            choiceClass);

    return new PersistenceRelationshipModel(
            persistentRelationshipModel,
            destinationObjectModel,
            false,
            LockMode.REFERENCE) {
      public boolean validateChildObjects() {
        return true; // Sorgt dafür, dass Pflichtbeziehung validiert wird
      }
    };
  }

  /**
   * Extrahiert aus einem Array von AttributeModels das erste mit dem angegebenen
   * Namen.
   *
   * @param  persistentModels die AttributeModels
   * @param  attributeName    der Name des gesuchten AttributeModels
   * @return das gesuchte AttributeModel
   */
  protected static com.sdm.quasar.persistence.AttributeModel extractAttributeModel(
          com.sdm.quasar.persistence.AttributeModel[] persistentModels,
          String attributeName) {
    int length = persistentModels.length;

    for (int i = 0; i < length; i++) {
      com.sdm.quasar.persistence.AttributeModel model = persistentModels[i];

      if (model.getName().equals(attributeName))
        return model;
    }

    return null;
  }

  /**
   * Extrahiert aus einem Array von AttributeModels das erste mit dem angegebenen
   * Namen.
   *
   * @param  objectModels     die AttributeModels
   * @param  attributeName    der Name des gesuchten AttributeModels
   * @return das gesuchte AttributeModel
   */
  protected static AttributeModel extractAttributeModel(
          AttributeModel[] objectModels,
          String attributeName) {
    int length = objectModels.length;

    for (int i = 0; i < length; i++) {
      AttributeModel model = objectModels[i];

      if (model.getName().equals(attributeName))
        return model;
    }

    return null;
  }


  /**
   * Stellt sicher, dass ein Attributwert eindeutig ist. Diese Validation muss im
   * {@link com.sdm.quasar.modelview.server.model.ValidationMode#OBJECT} angedockt werden.<p>
   *
   * <b>ACHTUNG</b>: Diese Validierung funktioniert nur dann, wenn das Attribut
   * DB-seitig nicht unique ist. Das Objekt wird nämlich erst nicht-committed
   * in die DB geschrieben, was dann fehlschlägt. Leider ist es aus auch keine
   * Lösung, die Validierung im {@link com.sdm.quasar.modelview.server.model.ValidationMode#PROXY} anzudocken, da
   * da ein leeres Objekt in die DB geschrieben wird, was noch schlimmer ist.
   *
   * @param  type               die Klasse des persistenten Objekts
   * @param  attributeModelName der Name des zu validierenden Objekts
   * @param  ignoreCase         falls <code>true</code>, werden Groß- und
   *                            Kleinbuchstaben als gleich angesehen
   * @return die Validierung
   * */
  protected Validation makeWertEindeutigValidation(final Class type,
                                                   final String attributeModelName,
                                                   final boolean ignoreCase) {
    return new CrossValidation("UNIQUE_VALUE",
                               mMessages,
                               attributeModelName) {
      private final String attributeModelKey = "oid";
      PreparedQuery query = null;

      {
        try {
          SingleValuedResultExpression nameExpression = queryManager.get("this").get(attributeModelName);

          query = queryManager.prepareQuery(getPool(),
                                            type,
                                            com.sdm.quasar.persistence.LockMode.REFERENCE,
                                            nameExpression.equal("?").and(
                                                    queryManager.get("this").get(attributeModelKey).notEqual("?")));
        } catch (PersistenceException e) {
          e.printStackTrace(); // TODO ???
        }
      }

      public boolean check(ValidationController validationController,
                           Object object) throws ValidationException {
        try {
          final AttributeModel attributeModel
                  = (AttributeModel) getObjectModel().getPropertyModel(attributeModelName);
          final AttributeModel oidAttributeModel
                  = (AttributeModel) getObjectModel().getPropertyModel(attributeModelKey);

          Object attributeValue = (String) attributeModel.getValue(object);

          if (ignoreCase)
            attributeValue = ((String) attributeValue).toLowerCase();
          //Vorsicht: mit toUpper geht's nicht, da Java ß anders umwandelt als Oracle

          long oid = ((Long) oidAttributeModel.getValue(object)).longValue();
          UserTransaction userTransaction = (LocalTransactionManager)BusinessObjectManager.getBusinessObjectManager().getSingleton(TransactionManager.class);

          userTransaction.begin();

          QueryResult objekteAusPool = query.execute(new Object[]{attributeValue, new Long(oid)});
          boolean existsObject = (objekteAusPool.hasNextResult());

          userTransaction.commit();

          return !existsObject;
        } catch (Exception e) {
          e.printStackTrace();

          throw new ValidationException(getGoal() + " validation failed because of " + e.toString()) {
          };
        }
      }

      protected String toString(Object object, Locale locale) {
        if (object instanceof LocalizedMessage) {
          try {
            final AttributeModel attributeModel = (AttributeModel) getObjectModel().getPropertyModel(attributeModelName);
            return ((LocalizedMessage) object).format(locale, new Object[]{attributeModel.getLabel(locale)});
          } catch (PropertyModelNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("attributeModel " + attributeModelName + " not found");
          }
        } else
          return super.toString(object, locale);
      }
    };
  }

  /**
   * Validiert einen Datumsbereich. Diese Validation muss im {@link com.sdm.quasar.modelview.server.model.ValidationMode#OBJECT}
   * an ein {@link ObjectModel} angedockt werden.
   *
   * @param  attributeModelNameVon  der Name des AttributeModels mit dem
   *                               'von'-Wert
   * @param  attributeModelNameBis  der Name des AttributeModels mit dem
   *                               'bis'-Wert
   * @param  allowEqual             die Angabe, ob Gleichheit der beiden
   *                                Werte akzuzeptiert wird
   * @return die Validierung
   */
  protected Validation makeDatumsbereichValidation(final String attributeModelNameVon,
                                                   final String attributeModelNameBis,
                                                   final boolean allowEqual) {
    return new CrossValidation("VALID_ZEITRAUM",
                               mMessages,
                               attributeModelNameVon) {
      public boolean check(ValidationController validationController,
                           Object object) throws ValidationException {
        try {
          final AttributeModel modelVon
                  = (AttributeModel) getObjectModel().getPropertyModel(attributeModelNameVon);
          final AttributeModel modelBis
                  = (AttributeModel) getObjectModel().getPropertyModel(attributeModelNameBis);
          Date von = (Date) modelVon.getValue(object);
          Date bis = (Date) modelBis.getValue(object);

          return (bis == null) ||
                  (von == null) ||
                  von.before(bis) ||
                  (allowEqual && bis.equals(von));
        } catch (Exception e) {
          e.printStackTrace();

          throw new ValidationException(getGoal() + " validation failed because of " + e.toString()) {
          };
        }
      }

      protected String toString(Object object, Locale locale) {
        if (object instanceof LocalizedMessage) {
          try {
            final AttributeModel modelVon
                    = (AttributeModel) getObjectModel().getPropertyModel(attributeModelNameVon);
            final AttributeModel modelBis
                    = (AttributeModel) getObjectModel().getPropertyModel(attributeModelNameBis);

            return ((LocalizedMessage) object).format(locale, new Object[]{modelVon.getLabel(locale),
                                                                           modelBis.getLabel(locale)});
          } catch (PropertyModelNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("attributeModel " + attributeModelNameVon + " or " +
                                       attributeModelNameBis + " not found");
          }
        } else
          return super.toString(object, locale);
      }

    };
  }

  /**
   * Prüft, ob der Benutzer das gewünschte Recht hat.
   *
   * @param  boName         der Name des {@link BusinessObject}s
   * @param  boPropertyName der Name der {@link BusinessObjectProperty}
   * @return <code>true</code>, falls der Benutzer das Recht hat
   */
  protected boolean hatRecht(String boName, String boPropertyName) {
    try {
      BusinessObjectManager manager = BusinessObjectManager.getBusinessObjectManager();
      AccessController accessController = manager.getAccessController();
      final BusinessObject mitarbeiterBC = manager.getObject(boName);

      BusinessObjectProperty boProperty = manager.getProperty(mitarbeiterBC, boPropertyName);

      return (!boProperty.isAccessControlled() ||
              accessController.isAccessPermitted(mitarbeiterBC, boProperty));
    } catch (BusinessObjectNotFoundException e) {
      e.printStackTrace();
      Assertion.fail("BusinessObjectNotFoundException");
    } catch (BusinessObjectPropertyNotFoundException e) {
      e.printStackTrace();
      Assertion.fail("BusinessObjectPropertyNotFoundException");
    } catch (SystemException e) {
      // Berechtigungen werden zu Beginn der Session vorgeladen. Daher kann
      // man hier eigentlich nicht hinkommen
      e.printStackTrace();
      Assertion.fail("SystemException");
    }

    return false;
  }

  protected RelationshipModel makeLookupRelationshipModel(Class persistentSourceType,
                                                          Class persistentDestinationType,
                                                          String relationshipName,
                                                          DataModel dataModel)
          throws PersistenceException {
    final TypeModel persistentTypeModel = getPool().getTypeModel(persistentSourceType);

    final com.sdm.quasar.persistence.RelationshipModel persistentRelationshipModel
            = (com.sdm.quasar.persistence.RelationshipModel) persistentTypeModel.getPropertyModel(relationshipName);

    final ObjectModel destinationObjectModel
            = new LookupObjectModel(relationshipName,
                                    persistentDestinationType, dataModel);

    return new PersistenceRelationshipModel(
            persistentRelationshipModel,
            destinationObjectModel,
            false,
            LockMode.REFERENCE) {
      public boolean validateChildObjects() {
        return true; // Sorgt dafür, dass Pflichtbeziehung validiert wird
      }
    };
  }
}
