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
import com.sdm.quasar.continuation.StandardContinuationManager;
import com.sdm.quasar.dataview.server.DataViewServer;
import com.sdm.quasar.dataview.server.model.ColumnModel;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.modelview.server.persistence.PersistenceObjectController;
import com.sdm.quasar.persistence.Pool;

/**
 * @author Marco Schmickler
 */

//todo dpk 23/01/2003 -> MC kommentieren

public class LookupController extends PersistenceObjectController {
  public void lookup(Keywords values, Keywords arguments) {
    try {
      DataViewServer dataViewServer = ((DataViewServer) getObjectViewServer().getChildComponents()[0]);

      Object object = arguments.getValue("object");
      Object[] primaryKey = null;

      ColumnModel[] columnModels = dataViewServer.getQueryModel().getColumnModels();
      int cLength = columnModels.length;
      int pkLength = 0;

      for (int i = 0; i < cLength; i++)
        if (columnModels[i].belongsToPrimaryKey())
          pkLength += 1;

      primaryKey = new Object[pkLength];
      int index = 0;

      for (int i = 0; i < cLength; i++) {
        ColumnModel columnModel = columnModels[i];

        if (columnModel.belongsToPrimaryKey())
          primaryKey[index++] = columnModel.getValue(object);
      }

      Object result = null;

      Pool pool = ((Pool) BusinessObjectManager.getBusinessObjectManager().getSingleton(Pool.class));
      Class type = getObjectViewServer().getObjectModel().getType();
      result = pool.lookup(type, primaryKey);

      StandardContinuationManager.getContinuationManager().continueWithResult(result);
    } catch (Exception e) {
      StandardContinuationManager.getContinuationManager().continueWithException(e);
    }
  }
}
