package quasarusers.portal.businessobject;

/*
 * Copyright (c) 2001, 2002. software design & management AG
 * All rights reserved.
 * This file is made available under the terms of the license
 * agreement that accompanies this distribution.
 *
 * $Revision: 1.1 $, last modified $Date: 2003/03/26 19:58:36 $ by $Author: schmickler $
 */

import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.persistence.Pool;
import com.sdm.quasar.newmodelview.server.persistence.PersistenceObjectController;
import com.sdm.quasar.newmodelview.server.plugin.ObjectFactory;
import com.sdm.quasar.newmodelview.server.model.LockMode;
import com.sdm.quasar.businessobject.BusinessObjectManager;
import quasarusers.util.RepositoryLoader;

public class SimplePersistenceObjectController extends PersistenceObjectController implements ObjectFactory {
    public SimplePersistenceObjectController() {
    }

    public Object make(Class type, Keywords arguments) {
        try {
            Pool pool = getPool();

            Object[] primaryKey = (Object[])arguments.getValue("primaryKey");

            if (primaryKey == null) {
                return prepareObject(pool.make(type));
            }
            else
                return pool.lookup(type, primaryKey);

        } catch (Exception e) {
            return null;
        }
    }

    public Object load(Class type, Object object, LockMode lockMode, int timeout) throws Exception {
        return super.load(type, object, lockMode, timeout);
    }

    protected Object prepareObject(Object object) throws Exception {
        return object;
    }

    public Pool getPool() {
        return (Pool)BusinessObjectManager.getBusinessObjectManager().getSingleton(Pool.class);
    }
}
