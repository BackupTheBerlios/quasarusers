/*
 * Copyright (c) 2001, 2002. software design & management AG
 * All rights reserved.
 * This file is made available under the terms of the license
 * agreement that accompanies this distribution.
 *
 * $Revision: 1.1 $, last modified $Date: 2003/03/26 19:58:38 $ by $Author: schmickler $
 */

package quasarusers.util.modelview.server;

import com.sdm.quasar.component.Command;
import com.sdm.quasar.component.ComponentException;
import com.sdm.quasar.dataview.server.DataViewServer;
import com.sdm.quasar.dataview.server.model.DataModel;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.modelview.server.ObjectState;
import com.sdm.quasar.modelview.server.ObjectViewServer;
import com.sdm.quasar.modelview.server.model.ObjectModel;
import com.sdm.quasar.view.server.ViewServer;

/**
 * @author Marco Schmickler
 */

//todo dpk 23/01/2003 -> MC kommentieren

public class LookupViewServer extends ObjectViewServer {
  public LookupViewServer(Keywords arguments) throws ComponentException {
    super(arguments);
  }

  public void buildCommands() {
    super.buildCommands();

    addCommand(new Command(GET_CHILD_VIEW_SERVER) {
      public Object perform(Keywords arguments) throws ComponentException {
        DataModel dataModel = ((LookupObjectModel) getObjectModel()).getDataModel();

        ViewServer server = getViewServerManager().makeViewServer(DataViewServer.class, new Keywords("load", Boolean.TRUE, "dataModel", dataModel));

        addChildComponent(server);

        Keywords keywords = server.computeRemoteArguments(arguments);
        keywords.addValue("server", server);                // todo: fixme

        return new Object[]{server.getID(), keywords};
      }
    });
  }

  protected ObjectState makeObjectState(Object object) {
    ObjectModel objectModel = getObjectModel(object);

    return new ObjectState(this, objectModel, object, makeObjectProxy(objectModel));
    // ACHTUNG: makeObjectProxy muss im Gegensatz zu sonst auch dann aufgerufen
    //  werden, wenn das object null ist. M. E. ist das ein Bug in der
    // Vaterklassenmethode
  }
}
