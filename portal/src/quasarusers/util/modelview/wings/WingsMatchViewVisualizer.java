/*
 * Copyright (c) 2001, 2002. software design & management AG
 * All rights reserved.
 * This file is made available under the terms of the license
 * agreement that accompanies this distribution.
 *
 * $Revision: 1.1 $, last modified $Date: 2003/03/26 19:58:39 $ by $Author: schmickler $
 */

package quasarusers.util.modelview.wings;

import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.modelview.ObjectView;
import com.sdm.quasar.modelview.implementation.wings.WingsObjectViewVisualizer;
import com.sdm.quasar.modelview.model.ViewModel;
import com.sdm.quasar.modelview.proxy.ObjectProxy;
import com.sdm.quasar.util.Assertion;
import com.sdm.quasar.view.View;
import com.sdm.quasar.view.ViewCommandController;
import com.sdm.quasar.view.implementation.wings.CommandTrigger;
import com.sdm.quasar.view.implementation.wings.WingsCommandActivator;
import quasarusers.util.modelview.server.MatchObjectModel;
import org.wings.SComboBox;
import org.wings.SComponent;

import javax.swing.*;
import java.util.Iterator;
import java.util.List;

/**
 * @author Marco Schmickler
 */

//todo dpk 23/01/2003 -> MC kommentieren

public class WingsMatchViewVisualizer extends WingsObjectViewVisualizer {
  private final static Long LONG_0 = new Long(0);

  private SComboBox mComboBox;

  public WingsMatchViewVisualizer(Keywords arguments) {
    super(arguments);
  }

  public List buildLocalControls() {
    List controls = super.buildLocalControls();

    final SComponent widget = getWidget();

    if (widget instanceof SComboBox) {
      new CommandTrigger(ObjectView.LOOKUP_OBJECT) {
        public Keywords computeCommandArguments(ViewCommandController controller, Keywords arguments) {
          final SComponent widget = getWidget();

          if (widget instanceof SComboBox) {
            Object selectedValue = ((MatchObjectModel.Choice) ((SComboBox) widget).getSelectedItem()).getKey();

            arguments.addValue("oid", selectedValue);
          }

          return arguments;
        }

        public void associateElement(Object userInterfaceElement) {
          try {
            super.associateElement(userInterfaceElement);
          } catch (ClassCastException e) {
            // CommandTrigger castet userInterfaceElement (z. Zt.) fälschlicherweise auf
            // SAbstractButton. Daher hier als "Work around":

            SComboBox widget = (SComboBox) userInterfaceElement;

            WingsCommandActivator.setCommandActivator(widget, this);
            widget.addActionListener(this);
          }
        }
      }.associateElement(getWidget());

      controls.add(widget);
    }

    return controls;
  }

  public void bindViewObject(Object object, Keywords arguments) {
    ViewModel viewModel = ((ObjectView) getView()).getViewModel();

    MatchObjectModel.Choice[] values = (MatchObjectModel.Choice[]) viewModel.getFieldModel("choices").getValue((ObjectProxy) object);
    Object value = viewModel.getFieldModel("oid").getValue((ObjectProxy) object);
    final SComponent widget = getWidget();

    if (widget instanceof SComboBox) {
      SComboBox cbx = (SComboBox) widget;

      cbx.setModel(new DefaultComboBoxModel(values));

      if (!value.equals(LONG_0)) {
        for (int i = 0; i < values.length; i++)
          if (values[i].getKey() != null && values[i].getKey().equals(value))
            getWidget().setSelectedIndex(i);
      }
    }
    super.bindViewObject(object, arguments);
  }

  public SComboBox getWidget() {
    if (mComboBox == null) {
      View view = getView();
      String nameCbx = view.getName();
      Iterator elements = getUserInterfaceElements().iterator();

      while (elements.hasNext()) {
        Object element = elements.next();

        if (element instanceof SComboBox) {
          SComboBox widget = (SComboBox) element;
          String elementName = widget.getName();

          if (nameCbx.equalsIgnoreCase(elementName)) {
            mComboBox = widget;

            break;
          }
        }
      }

      if (Assertion.CHECK && (mComboBox == null))
        Assertion.fail("Cannot find ComboBox with name " + nameCbx);
    }

    return mComboBox;
  }
}

