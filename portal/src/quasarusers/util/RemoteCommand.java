package quasarusers.util;

import com.sdm.quasar.component.Command;
import com.sdm.quasar.component.DisabledCommandException;
import com.sdm.quasar.component.ComponentException;
import com.sdm.quasar.lang.Symbol;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.view.View;
import com.sdm.quasar.view.AbstractView;

/**
 * Created by IntelliJ IDEA.
 * User: schmickl
 * Date: Dec 19, 2002
 * Time: 8:01:38 PM
 * To change this template use Options | File Templates.
 */
//todo dpk 23.01.03 -> MC kommentieren
public class RemoteCommand extends Command {
  View view;
  Symbol remoteCommand;

  public RemoteCommand(View view, Symbol name) {
    super(name);

    view.addCommand(this);
    this.view = view;
    remoteCommand = Symbol.forName("remote"+getName());
  }

  protected Object perform(Keywords arguments) throws ComponentException {
    return view.performCommand(remoteCommand, arguments);
  }

  public Symbol getRemote() {
    return remoteCommand;
  }
}
