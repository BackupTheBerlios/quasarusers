/*
 * Copyright (c) 2001, 2002. software design & management AG
 * All rights reserved.
 * This file is made available under the terms of the license
 * agreement that accompanies this distribution.
 *
 * $Revision: 1.1 $, last modified $Date: 2003/03/26 19:58:38 $ by $Author: schmickler $
 */

package quasarusers.util.modelview.server;

import com.sdm.quasar.component.ComponentException;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.modelview.server.ObjectState;
import com.sdm.quasar.modelview.server.ObjectViewServer;
import com.sdm.quasar.modelview.server.model.ObjectModel;

/**
 * @author Marco Schmickler
 */

//todo dpk 23/01/2003 -> MC kommentieren

public class MatchViewServer extends ObjectViewServer {
  public MatchViewServer(Keywords arguments) throws ComponentException {
    super(arguments);
  }

  protected ObjectState makeObjectState(Object object) {
    ObjectModel objectModel = getObjectModel(object);

    return new ObjectState(this, objectModel, object, makeObjectProxy(objectModel));
    // ACHTUNG: makeObjectProxy muss im Gegensatz zu sonst auch dann aufgerufen
    //  werden, wenn das object null ist. M. E. ist das ein Bug in der
    // Vaterklassenmethode
  }
}
