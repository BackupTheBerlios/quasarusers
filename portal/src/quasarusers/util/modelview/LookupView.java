/*
 * Copyright (c) 2001, 2002. software design & management AG
 * All rights reserved.
 * This file is made available under the terms of the license
 * agreement that accompanies this distribution.
 *
 * $Revision: 1.1 $, last modified $Date: 2003/03/26 19:58:38 $ by $Author: schmickler $
 */

package quasarusers.util.modelview;

import com.sdm.quasar.component.CommandAdapter;
import com.sdm.quasar.component.CommandEvent;
import com.sdm.quasar.component.ComponentException;
import com.sdm.quasar.component.NoSuchCommandException;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.modelview.ObjectView;
import com.sdm.quasar.modelview.proxy.ObjectProxy;
import com.sdm.quasar.view.ViewManager;
import com.sdm.quasar.view.ViewTransaction;
import com.sdm.quasar.view.ViewVisualizer;
import quasarusers.util.dataview.FilterExtentView;
import quasarusers.util.modelview.wings.LookupViewVisualizer;
import quasarusers.util.modelview.wings.WingsComboboxViewVisualizer;
import quasarusers.util.modelview.wings.WingsLookupViewVisualizer;
import org.wings.SComboBox;

import java.util.List;

/**
 * @author Marco Schmickler
 */
//todo dpk 23/01/2003 -> MC kommentieren

public class LookupView extends ObjectView {
  private FilterExtentView filterExtentView;

  public LookupView(Keywords arguments) throws ComponentException {
    super(arguments);
  }

  public void buildStructure(Keywords arguments) throws ComponentException {
    filterExtentView = (FilterExtentView) makeChildView(FilterExtentView.class, arguments);

    filterExtentView.lookupCommand(FilterExtentView.PARAMETER_SEARCH).addCommandListener(new CommandAdapter() {
      public void commandPerformed(CommandEvent event) {
        ((LookupViewVisualizer) getViewVisualizer()).updateEditMode();
      }
    });
  }

  public void unbindChildObjects(Keywords arguments) {

  }

  public void bindObject(Object object, Keywords arguments) throws ComponentException {
    ViewTransaction transaction = (ViewTransaction) arguments.getValue("viewTransaction");
    ViewManager viewManager = getViewManager();

    if (filterExtentView.getViewServer() == null) {
      boolean endTransaction = false;

      if (transaction == null) {
        transaction = viewManager.beginTransaction();

        endTransaction = true;
      }

      try {
        startFilterViewServer(new Keywords(arguments, "viewTransaction", transaction));
      } finally {
        if (endTransaction)
          viewManager.commitTransaction(transaction);
      }
    }

    super.bindObject(object, arguments);

    filterExtentView.bindChildObjects(null, arguments);

    Object value = getViewModel().getFieldModel("oid").getValue((ObjectProxy) object);

    if (value != null) {
      List listProxy = ((List) filterExtentView.getObject());

      if (listProxy != null) {
        for (int i = 0; i < listProxy.size(); i++) {
          Object[] objectProxy = (Object[]) listProxy.get(i);

          if (value.equals(objectProxy[0])) {
            Object view = filterExtentView.getViewVisualizer();

            if (view instanceof WingsComboboxViewVisualizer)
              ((SComboBox) ((WingsComboboxViewVisualizer) view).getTableUserInterfaceElement()).setSelectedIndex(i); // todo
          }
        }
      }
    }

  }

  private void startFilterViewServer(Keywords arguments) throws ComponentException {
    if (getViewServer() == null) {
      ObjectView parentView = (ObjectView) getParentComponent();
      Object remoteServer = null;

      arguments = (Keywords) arguments.clone();

      if (parentView == null) {
        Keywords serverArguments = startViewServer(arguments);

        updateState(serverArguments);
      } else {
        remoteServer = parentView.performCommand(GET_CHILD_VIEW_SERVER,
                                                 new Keywords("viewTransaction", arguments.getValue("viewTransaction"),
                                                              "link", getViewModelLink().getName()));

        updateState(new Keywords("server", remoteServer));
      }
    }
    Object[] result =
            (Object[]) performCommand(GET_CHILD_VIEW_SERVER,
                                      new Keywords("viewTransaction", arguments.getValue("viewTransaction")));

    filterExtentView.updateState((Keywords) result[1]);
    filterExtentView.bindChildObjects(null, arguments);
  }

  public boolean saveObject(Keywords arguments) throws ComponentException {
    updateViewObject(arguments);

    return true;
  }

  public ViewVisualizer makeViewVisualizer(Keywords arguments) {
    return new WingsLookupViewVisualizer(arguments);
  }

  public void updateCommandSetup() {
    super.updateCommandSetup();

    try {
      setCommandEnabled(LOOKUP_OBJECT, true);
    } catch (NoSuchCommandException e) {
      // can not happen
    }
  }
}
