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
import com.sdm.quasar.persistence.Persistent;
import com.sdm.quasar.persistence.PersistentNotFoundException;
import com.sdm.quasar.persistence.PersistentLockTimeoutException;
import com.sdm.quasar.persistence.LockMode;
import com.sdm.quasar.businessobject.BusinessObjectManager;
import com.sdm.quasar.newmodelview.server.persistence.PersistenceCollectionController;
import com.sdm.quasar.newmodelview.server.plugin.ObjectFactory;
import quasarusers.util.RepositoryLoader;

public class SimplePersistenceCollectionController extends PersistenceCollectionController implements ObjectFactory {

    public SimplePersistenceCollectionController() {
    }

    public Object make(Class type, Keywords arguments) {
        try {
            Pool pool = (Pool) BusinessObjectManager.getBusinessObjectManager().getSingleton(Pool.class);

            Object[] primaryKey = (Object[])arguments.getValue("primaryKey");

            if (primaryKey == null) {
                primaryKey = new Object[] { RepositoryLoader.getRepositoryLoader().getNextOID() };

                return prepareObject(pool.make(type, primaryKey));
            }
            else
                return pool.lookup(type, primaryKey);
        } catch (Exception e) {
            return null;
        }
    }

    protected Object prepareObject(Object object) throws Exception {
        return object;
    }

    public boolean delete(Object object) throws Exception {
        try {
            Persistent persistent = ((Persistent)object);

            persistent.lock(LockMode.DELETE);
            persistent.delete();

            return true;
        }
        catch (PersistentNotFoundException e) {
            return false;
        }
        catch (PersistentLockTimeoutException e) {
            return false;
        }
    }

    public Pool getPool() {
        return (Pool)BusinessObjectManager.getBusinessObjectManager().getSingleton(Pool.class);
    }
}
