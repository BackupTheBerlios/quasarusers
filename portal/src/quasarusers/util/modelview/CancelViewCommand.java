/*
 * Created by IntelliJ IDEA.
 * User: rademach
 * Date: 17.06.2002
 * Time: 13:22:19
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package quasarusers.util.modelview;

import com.sdm.quasar.component.Command;
import com.sdm.quasar.component.ComponentException;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.view.AbstractView;
import com.sdm.quasar.view.View;
import com.sdm.quasar.view.ViewContainer;

//todo dpk 23/01/2003 -> MC kommentieren

public class CancelViewCommand extends Command {
  View view;

  public CancelViewCommand(View view) {
    super(AbstractView.CANCEL_VIEW, false);
    this.view = view;
  }

  public Object perform(Keywords arguments) throws ComponentException {
    ViewContainer container = (ViewContainer) view.getContainer();

    if (view.getRootComponent() == container.getActiveView())
      return container.performCommand(ViewContainer.CANCEL_ACTIVE_VIEW,
                                      new Keywords(arguments, "force", Boolean.TRUE));
    else
      return Boolean.FALSE;
  }
}
