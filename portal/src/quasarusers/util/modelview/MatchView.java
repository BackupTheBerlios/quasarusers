/*
 * Copyright (c) 2001, 2002. software design & management AG
 * All rights reserved.
 * This file is made available under the terms of the license
 * agreement that accompanies this distribution.
 *
 * $Revision: 1.1 $, last modified $Date: 2003/03/26 19:58:38 $ by $Author: schmickler $
 */

package quasarusers.util.modelview;

import com.sdm.quasar.modelview.ObjectView;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.component.ComponentException;
import com.sdm.quasar.view.ViewVisualizer;
import quasarusers.util.modelview.wings.WingsMatchViewVisualizer;

/**
 * @author Marco Schmickler
 */

//todo dpk 23/01/2003 -> MC kommentieren

public class MatchView extends ObjectView {
  public MatchView(Keywords arguments) throws ComponentException {
    super(modifyArguments(arguments));
  }

  public ViewVisualizer makeViewVisualizer(Keywords arguments) {
    return new WingsMatchViewVisualizer(arguments);
  }

  private static Keywords modifyArguments(Keywords arguments) {
    final Keywords keywords = ((Keywords) arguments.clone());

    return keywords;
  }

  public boolean saveObject(Keywords arguments) throws ComponentException {
    return super.saveObject(arguments);
  }
}
