/*
 * Copyright (c) 2001, 2002. software design & management AG
 * All rights reserved.
 * This file is made available under the terms of the license
 * agreement that accompanies this distribution.
 *
 * $Revision: 1.1 $, last modified $Date: 2003/03/26 19:58:38 $ by $Author: schmickler $
 */

package quasarusers.util.modelview.server;

import com.sdm.quasar.businessobject.BusinessObjectManager;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.modelview.proxy.ObjectProxy;
import com.sdm.quasar.modelview.server.model.AccessMode;
import com.sdm.quasar.modelview.server.model.AttributeModel;
import com.sdm.quasar.modelview.server.model.RelationshipModel;
import com.sdm.quasar.modelview.server.model.SimpleAttributeModel;
import com.sdm.quasar.modelview.server.model.TransformedAttributeModel;
import com.sdm.quasar.modelview.server.persistence.PersistenceAttributeModel;
import com.sdm.quasar.modelview.server.persistence.PersistenceObjectModel;
import com.sdm.quasar.persistence.PersistenceException;
import com.sdm.quasar.persistence.Pool;
import com.sdm.quasar.persistence.PropertyModelNotFoundException;
import com.sdm.quasar.persistence.TypeModel;
import com.sdm.quasar.persistence.query.PreparedQuery;
import com.sdm.quasar.persistence.query.QueryResult;
import com.sdm.quasar.persistence.query.ResultExpression;
import com.sdm.quasar.persistence.query.SingleValuedResultExpression;
import com.sdm.quasar.persistence.query.implementation.StandardQueryManager;
import com.sdm.quasar.util.Arrays;
import com.sdm.quasar.util.Assertion;
import com.sdm.quasar.util.LocalizedString;
import com.sdm.quasar.validation.Validation;
import quasarusers.util.ToplevelTransaction;
import quasarusers.portal.ConfigurableRuntime;

import javax.transaction.Transaction;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

/**
 * @author Marco Schmickler
 */

//todo dpk 23/01/2003 -> MC kommentieren

public class MatchObjectModel extends PersistenceObjectModel {
  /** Diese Klasse repräsentiert eine Auswahlmöglichkeit. */
  public static class Choice {
    private final Object[] mValue;

    /** Der String wird beim ersten Zugriff gepuffert. **/
    private String mString;

    public Choice(Object[] value) {
      this.mValue = value;
    }

    public Object getKey() {
      return mValue[0];
    }

    public String toString() {
      if (mString == null) {
        StringBuffer buffer = new StringBuffer();

        for (int i = 1; i < mValue.length; i++) {
          if (i > 1)
            buffer.append(", ");

          Object value = mValue[i];

          buffer.append((value != null) ? value : "-");
        }

        mString = buffer.toString();
      }

      return mString;
    }

    public final String getString() {
      return mString;
    }

    public final void setString(String string) {
      mString = string;
    }

    public final Object[] getValue() {
      return mValue;
    }
  }

  public static final class RequiredMode {
    protected RequiredMode() {
    }
  }

  public static final RequiredMode REQUIRED = new RequiredMode();
  public static final RequiredMode REQUIRED_WITH_DEFAULT = new RequiredMode();
  public static final RequiredMode NOT_REQUIRED = new RequiredMode();

  private static final Class CHOICE_CLASS = (new Choice[0]).getClass();
  private static final Class OBJECT_ARRAY_CLASS = (new Object[0]).getClass();
  private static final Class DEFAULT_CHOICE_CLASS = Choice.class;
  private static final Long LONG_0 = new Long(0);
  private static final RelationshipModel[] EMPTY_RELATIONSHIPS = new RelationshipModel[0];

  /** Der Name des Modells (muss der Name der Relationship sein!). */
  private final String mName;

  RequiredMode mRequiredMode;

  /**
   * MatchObjectModel für persistentente Klassen.
   *
   * @param  name         der Name des Modells (muss der Name der Relationship
   *                      sein!)
   * @param  label        das Label des Feldes
   * @param  instanceType die persistente Klasse
   * @param  identifier   der Name der darzustellenden Spalte
   * @param  requiredMode der RequiredMode für das ObjectModel
   */
  public MatchObjectModel(String name,
                          LocalizedString label,
                          Class instanceType,
                          String identifier,
                          RequiredMode requiredMode) throws PersistenceException {
    this(name, label, instanceType, new String[]{identifier}, requiredMode);
  }

  /**
   * MatchObjectModel für persistentente Klassen.
   *
   * @param  name         der Name des Modells (muss der Name der Relationship
   *                      sein!)
   * @param  label        das Label des Feldes
   * @param  instanceType die persistente Klasse
   * @param  identifiers  der Name der darzustellenden Spalten
   * @param  requiredMode der RequiredMode für das ObjectModel
   */
  public MatchObjectModel(String name,
                          LocalizedString label,
                          Class instanceType,
                          String[] identifiers,
                          RequiredMode requiredMode) throws PersistenceException {
    super(getPool().getTypeModel(instanceType),
          instanceType,
          MatchController.class.getName(),
          MatchViewServer.class.getName(),
          makeAttributeModels(getPool().getTypeModel(instanceType),
                              buildQuery(instanceType, identifiers),
                              requiredMode,
                              label,
                              DEFAULT_CHOICE_CLASS),
          EMPTY_RELATIONSHIPS);

    mName = name;
    mRequiredMode = requiredMode;
  }

  /**
   * MatchObjectModel für persistentente Klassen.
   *
   * @param  name         der Name des Modells (muss der Name der Relationship
   *                      sein!)
   * @param  label        das Label des Feldes
   * @param  instanceType die persistente Klasse
   * @param  identifiers  die Namen der darzustellenden Spalten
   * @param  requiredMode der RequiredMode für das ObjectModel
   * @param  choiceClass  die Spezialisierung von {@link Choice}
   */
  public MatchObjectModel(String name,
                          LocalizedString label,
                          Class instanceType,
                          String[] identifiers,
                          RequiredMode requiredMode,
                          Class choiceClass) throws PersistenceException {
    super(getPool().getTypeModel(instanceType),
          instanceType,
          MatchController.class.getName(),
          MatchViewServer.class.getName(),
          makeAttributeModels(getPool().getTypeModel(instanceType),
                              buildQuery(instanceType, identifiers),
                              requiredMode,
                              label,
                              choiceClass),
          EMPTY_RELATIONSHIPS);

    mName = name;
    mRequiredMode = requiredMode;
  }

  /**
   * MatchObjectModel für beliebige Querys.
   *
   * @param  name         der Name des Modells (muss der Name der Relationship
   *                      sein!)
   * @param  label        das Label des Feldes
   * @param  instanceType die persistente Klasse
   * @param  query        die Query für die darzustellenden Daten (Spalte 0: OID)
   * @param  requiredMode der RequiredMode für das ObjectModel
   */
  public MatchObjectModel(String name,
                          LocalizedString label,
                          Class instanceType,
                          PreparedQuery query,
                          RequiredMode requiredMode) throws PersistenceException {
    super(getPool().getTypeModel(instanceType),
          instanceType,
          MatchController.class.getName(),
          MatchViewServer.class.getName(),
          makeAttributeModels(getPool().getTypeModel(instanceType),
                              query,
                              requiredMode,
                              label,
                              DEFAULT_CHOICE_CLASS),
          EMPTY_RELATIONSHIPS);

    mName = name;
    mRequiredMode = requiredMode;
  }

  /**
   * MatchObjectModel für beliebige Querys.
   *
   * @param  name         der Name des Modells (muss der Name der Relationship
   *                      sein!)
   * @param  label        das Label des Feldes
   * @param  instanceType die persistente Klasse
   * @param  query        die Query für die darzustellenden Daten (Spalte 0: OID)
   * @param  requiredMode der RequiredMode für das ObjectModel
   * @param  choiceClass  die Spezialisierung von {@link Choice}
   */
  public MatchObjectModel(String name,
                          LocalizedString label,
                          Class instanceType,
                          PreparedQuery query,
                          RequiredMode requiredMode,
                          Class choiceClass) throws PersistenceException {
    super(getPool().getTypeModel(instanceType),
          instanceType,
          MatchController.class.getName(),
          MatchViewServer.class.getName(),
          makeAttributeModels(getPool().getTypeModel(instanceType),
                              query,
                              requiredMode,
                              label,
                              choiceClass),
          EMPTY_RELATIONSHIPS);

    mName = name;
    mRequiredMode = requiredMode;
  }

  public String getName() {
    return mName;
  }

  private static AttributeModel[] makeAttributeModels(final TypeModel typeModel,
                                                      final PreparedQuery preparedQuery,
                                                      RequiredMode requiredMode,
                                                      LocalizedString label,
                                                      Class choiceClass)
          throws PropertyModelNotFoundException {
    return new AttributeModel[]{
      makeOidModel(typeModel, label),
      makeChoiceModel(typeModel, preparedQuery, requiredMode, choiceClass)
    };
  }

  private static AttributeModel makeOidModel(final TypeModel typeModel,
                                             final LocalizedString label)
          throws PropertyModelNotFoundException {
    return new TransformedAttributeModel(new PersistenceAttributeModel(
            (com.sdm.quasar.persistence.AttributeModel) typeModel.getPropertyModel("oid")) {
      public LocalizedString getLabel() {
        return label; // Label (wird beim Validieren ausgegeben)
      }

      public Object getValue(Object object) throws Exception {
        if (object == null)
          return LONG_0;
        else
          return super.getValue(object);
      }
    }) {
    };
  }

  private static AttributeModel makeChoiceModel(final TypeModel typeModel,
                                                final PreparedQuery preparedQuery,
                                                final RequiredMode requiredMode,
                                                final Class choiceClass) {
    Assertion.checkNotNull(typeModel, "typeModel");
    Assertion.checkNotNull(preparedQuery, "preparedQuery");

    final SimpleAttributeModel choicesModel = new SimpleAttributeModel("choices",
                                                                       "Choices",
                                                                       "",
                                                                       CHOICE_CLASS,
                                                                       new Keywords(),
                                                                       null,
                                                                       null,
                                                                       false) {
      public Object getValue(Object object) throws Exception {
        if (object instanceof ObjectProxy)
          return super.getValue(object);
        else {
          Transaction transaction = new ToplevelTransaction();
          QueryResult queryResult = preparedQuery.execute(new Object[0]);  // TODO: Diese Query wird viel zu häufig ausgeführt!!!

          ArrayList choices = new ArrayList();

          if (requiredMode != REQUIRED_WITH_DEFAULT) {
            choices.add(makeChoice(choiceClass, new Object[]{LONG_0, ""}));
          }

          while (queryResult.hasNext()) {
            Object[] result = (Object[]) queryResult.next();

            choices.add(makeChoice(choiceClass, result));
          }

          transaction.rollback();

          return (Choice[]) choices.toArray(new Choice[choices.size()]);
        }
      }
    };

    choicesModel.setAccessPermitted(AccessMode.WRITE, false);

    return choicesModel;
  }

  public static PreparedQuery buildQuery(final Class type,
                                         final String[] identifiers) throws PersistenceException {
    StandardQueryManager queryManager = ConfigurableRuntime.getQueryManager();
    SingleValuedResultExpression expression = queryManager.get(type);

    final int length = identifiers.length;
    ResultExpression[] projection = new ResultExpression[length + 1];

    projection[0] = expression.get("oid");

    for (int i = 0; i < length; i++)
      projection[i + 1] = expression.get(identifiers[i]);

    PreparedQuery preparedQuery = queryManager.prepareQuery(getPool(), projection, null);

    return preparedQuery;
  }

  protected Validation[] computeRelationshipValidations(RelationshipModel[] relationshipModels) {
    Validation[] validations = super.computeRelationshipValidations(relationshipModels);

    try {
      if (mRequiredMode != NOT_REQUIRED)
        validations = (Validation[]) Arrays.add(Validation.class,
                                                validations,
                                                new MatchRequiredValidation(getAttributeModel("oid")));
    } catch (Exception e) {
      e.printStackTrace();

      Assertion.fail("Validation cannot be added");
    }

    return validations;
  }

  protected static Pool getPool() {
    return ((Pool) BusinessObjectManager.getBusinessObjectManager().getSingleton(Pool.class));
  }

  protected static Choice makeChoice(final Class choiceClass, Object[] args) {
    if (DEFAULT_CHOICE_CLASS == choiceClass)
      return new Choice(args);
    else {
      try {
        final Constructor constructor = choiceClass.getConstructor(new Class[]{OBJECT_ARRAY_CLASS});

        return (Choice) constructor.newInstance(new Object[]{args});
      } catch (Exception e) {
        e.printStackTrace();
        Assertion.fail("Fatal error using reflection");
      }
    }

    return null;
  }
}
