/*
 * Copyright (c) 2001, 2002. software design & management AG
 * All rights reserved.
 * This file is made available under the terms of the license
 * agreement that accompanies this distribution.
 *
 * $Revision: 1.1 $, last modified $Date: 2003/03/26 19:58:39 $ by $Author: schmickler $
 */

package quasarusers.util.modelview.wings;

import com.sdm.quasar.component.ComponentException;
import com.sdm.quasar.dataview.FilterView;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.modelview.ObjectView;
import com.sdm.quasar.modelview.implementation.wings.WingsObjectViewVisualizer;
import com.sdm.quasar.view.View;
import com.sdm.quasar.view.ViewVisualizer;
import com.sdm.quasar.view.implementation.wings.ViewPanel;
import quasarusers.util.dataview.FilterExtentView;
import org.wings.SButton;
import org.wings.SComboBox;
import org.wings.SDefaultListCellRenderer;
import org.wings.SListCellRenderer;
import org.wings.SPanel;

import javax.swing.*;
import java.util.List;

/**
 * @author Marco Schmickler
 */

//todo dpk 23/01/2003 -> MC kommentieren

public class WingsLookupViewVisualizer extends WingsObjectViewVisualizer implements LookupViewVisualizer {
  private final static Long LONG_0 = new Long(0);

  private SComboBox mComboBox;
  private SPanel suchenPanel;
  private SPanel ergebnisPanel;
  private SComboBox mLabel;

  public WingsLookupViewVisualizer(Keywords arguments) {
    super(arguments);
  }

  protected ViewVisualizer provideChildVisualizer(View child, Keywords arguments) {
    if (child instanceof FilterExtentView) {
      return child.buildViewVisualizer(new Keywords(
              "viewVisualizer",
              new WingsComboboxViewVisualizer(new Keywords(arguments,
                                                           "view", child,
                                                           "visualRepresentation", ergebnisPanel)) {
                protected ViewVisualizer provideChildVisualizer(View child, Keywords arguments) {
                  if (child instanceof FilterView) {
                    return super.provideChildVisualizer(child,
                                                        new Keywords(arguments,
                                                                     "visualRepresentation", suchenPanel));
                  } else
                    return super.provideChildVisualizer(child, arguments);
                }
              }));
    } else
      return super.provideChildVisualizer(child, arguments);
  }

  public Object makeVisualRepresentation() {
    ViewPanel viewPanel = new ViewPanel();

    mComboBox = new SComboBox() {
      public void setVisible(boolean newVisible) {
        super.setVisible(newVisible);

        mLabel.setVisible(!newVisible);
      }

      public void setRenderer(SListCellRenderer newRenderer) {
        super.setRenderer(newRenderer);
      }
    };

    mComboBox.setName("ExtentBox");
    mComboBox.setMaximumRowCount(20);
    mComboBox.setRenderer(new SDefaultListCellRenderer());

    mLabel = new SComboBox(new DefaultComboBoxModel(new Object[]{""}));
    mLabel.setName("nachnameField");

    suchenPanel = new SPanel();
    ergebnisPanel = new SPanel();

    ergebnisPanel.add(mComboBox);
    ergebnisPanel.add(suchenPanel);

    SButton sButton = new SButton("Suchen");

    sButton.setName("parameterSearchCommand");
    ergebnisPanel.add(sButton);

    viewPanel.add(mLabel);
    viewPanel.add(ergebnisPanel);

    return viewPanel;
  }

  public void bindViewObject(Object object, Keywords arguments) {
    updateEditMode();

    super.bindViewObject(object, arguments);
  }

  public void updateEditMode() {
    boolean editMode = (mComboBox.getModel().getSize() > 0);

    mComboBox.setVisible(editMode);

    ((ObjectView) getView()).getViewModel().setVisible(!editMode);
  }

  public Object[] updateViewObject(Keywords arguments) {
    try {
      final int selectedIndex = mComboBox.getSelectedIndex();

      if (selectedIndex >= 0) {
        View view = (View) getView().getChildComponents()[0];
        Object object = ((List) view.getObject()).get(selectedIndex);

        getView().performCommand(ObjectView.LOOKUP_OBJECT, new Keywords("object", object));
      }
    } catch (ComponentException e) {
      e.printStackTrace();  //To change body of catch statement use Options | File Templates.
    }
    return super.updateViewObject(arguments);
  }

//  public List buildLocalControls() {
//    List controls = super.buildLocalControls();
//
//    CommandTrigger commandTrigger = new CommandTrigger(ObjectView.LOOKUP_OBJECT) {
//      public Keywords computeCommandArguments(ViewCommandController controller, Keywords arguments) {
//        arguments.addValue("index", new Integer(mComboBox.getSelectedIndex()));
//
//        return arguments;
//      }
//
//      public void associateElement(Object userInterfaceElement) {
//        try {
//          super.associateElement(userInterfaceElement);
//        } catch (ClassCastException e) {
//          // CommandTrigger castet userInterfaceElement (z. Zt.) fälschlicherweise auf
//          // SAbstractButton. Daher hier als "Work around":
//
//          SComboBox widget = (SComboBox) userInterfaceElement;
//
//          WingsCommandActivator.setCommandActivator(widget, this);
//          widget.addActionListener(this);
//        }
//      }
//    };
//
//    commandTrigger.associateElement(mComboBox);
//
//    controls.add(mComboBox);
//
//    return controls;
//  }
}

