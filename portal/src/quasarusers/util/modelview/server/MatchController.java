/*
 * Copyright (c) 2001, 2002. software design & management AG
 * All rights reserved.
 * This file is made available under the terms of the license
 * agreement that accompanies this distribution.
 *
 * $Revision: 1.1 $, last modified $Date: 2003/03/26 19:58:38 $ by $Author: schmickler $
 */

package quasarusers.util.modelview.server;

import com.sdm.quasar.modelview.server.persistence.PersistenceObjectController;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.continuation.StandardContinuationManager;
import com.sdm.quasar.businessobject.BusinessObjectManager;
import com.sdm.quasar.persistence.Pool;

/**
 * @author Marco Schmickler
 */

//todo dpk 23/01/2003 -> MC kommentieren

public class MatchController extends PersistenceObjectController {
    private final static Long LONG_0 = new Long(0);

    public void lookup(Keywords values, Keywords arguments) {
        try {
            Object result = null;
            Pool pool = ((Pool)BusinessObjectManager.getBusinessObjectManager().getSingleton(Pool.class));
            Class type = getObjectViewServer().getObjectModel().getType();
            final Object oid = arguments.getValue("oid");

            result = (oid == null || oid.equals(LONG_0)) ? null : pool.lookup(type, new Object[]{ oid });

            StandardContinuationManager.getContinuationManager().continueWithResult(result);
        }
        catch (Exception e) {
            StandardContinuationManager.getContinuationManager().continueWithException(e);
        }
    }
}
