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
import com.sdm.quasar.dataview.model.FilterPart;
import com.sdm.quasar.dataview.model.ParameterModel;
import com.sdm.quasar.dataview.model.SearchModel;
import com.sdm.quasar.dataview.model.SimpleSearchModel;
import com.sdm.quasar.dataview.server.DataController;
import com.sdm.quasar.dataview.server.model.DataModel;
import com.sdm.quasar.dataview.server.model.FilterModel;
import com.sdm.quasar.dataview.server.model.QueryModel;
import com.sdm.quasar.dataview.server.model.SimpleDataModel;
import com.sdm.quasar.dataview.server.model.SimpleQueryModel;
import com.sdm.quasar.dataview.server.persistence.PersistenceQuery;
import com.sdm.quasar.modelview.server.model.AttributeModel;
import com.sdm.quasar.modelview.server.model.RelationshipModel;
import com.sdm.quasar.modelview.server.persistence.PersistenceAttributeModel;
import com.sdm.quasar.modelview.server.persistence.PersistenceObjectModel;
import com.sdm.quasar.persistence.PersistenceException;
import com.sdm.quasar.persistence.Pool;
import com.sdm.quasar.persistence.PropertyModelNotFoundException;
import com.sdm.quasar.persistence.TypeModel;
import com.sdm.quasar.persistence.query.Expression;
import quasarusers.util.dataview.ColumnModel;
import quasarusers.util.dataview.PoolDataIterator;
import quasarusers.util.dataview.WildcardParameterModel;

/**
 * @author Marco Schmickler
 */

//todo dpk 23/01/2003 -> MC kommentieren

public class LookupObjectModel extends PersistenceObjectModel {
  private DataModel dataModel;
  private String mName;

  public LookupObjectModel(String name,
                           Class instanceType,
                           DataModel dataModel) throws PersistenceException {
    super(getPool().getTypeModel(instanceType),
          instanceType,
          LookupController.class.getName(),
          LookupViewServer.class.getName(),
          makeAttributeModels(getPool().getTypeModel(instanceType)),
          new RelationshipModel[0]);

    mName = name;
    this.dataModel = dataModel;
  }

  public static DataModel makeDataModel(PersistenceQuery query, ColumnModel[] columnModels, Expression filter) {
    SearchModel defaultSearchModel = new SimpleSearchModel("de/sdm/sia/remis/util/search.html",
                                                           "default",
                                                           "",
                                                           new FilterPart("filter",
                                                                          new Object[]{""}));
    FilterModel filterModel = new FilterModel("filter",
                                              "filter",
                                              "",
                                              new ParameterModel[]{new WildcardParameterModel("value",
                                                                                              "value",
                                                                                              "")});
    filterModel.setDataIteratorFilter(filter);

    QueryModel queryModel = new SimpleQueryModel("lookup",
                                                 "lookup",
                                                 "",
                                                 PoolDataIterator.class.getName(),
                                                 DataController.class.getName(),
                                                 columnModels,
                                                 new FilterModel[]{filterModel},
                                                 new SearchModel[]{defaultSearchModel},
                                                 defaultSearchModel);

    queryModel.setDataIteratorQuery(query);

    return new SimpleDataModel(new QueryModel[]{queryModel});
  }

  public String getName() {
    return mName;
  }

  public DataModel getDataModel() {
    return dataModel;
  }

  private static AttributeModel[] makeAttributeModels(final TypeModel typeModel)
          throws PropertyModelNotFoundException {
    return new AttributeModel[]{
      makeOidModel(typeModel),
      makeNameModel(typeModel),
    };
  }

  private static PersistenceAttributeModel makeOidModel(final TypeModel typeModel)
          throws PropertyModelNotFoundException {
    return new PersistenceAttributeModel(
            (com.sdm.quasar.persistence.AttributeModel) typeModel.getPropertyModel("oid")) {
      public Object getValue(Object object) throws Exception {
        if (object == null)
          return null;
        else
          return super.getValue(object);
      }
    };
  }

  private static PersistenceAttributeModel makeNameModel(final TypeModel typeModel)
          throws PropertyModelNotFoundException {
    return new PersistenceAttributeModel(
            (com.sdm.quasar.persistence.AttributeModel) typeModel.getPropertyModel("nachname")) { // TODO: "Nachname" ausbauen!
      public Object getValue(Object object) throws Exception {
        if (object == null)
          return null;
        else
          return super.getValue(object);
      }
    };
  }

  protected static Pool getPool() {
    return ((Pool) BusinessObjectManager.getBusinessObjectManager().getSingleton(Pool.class));
  }
}
