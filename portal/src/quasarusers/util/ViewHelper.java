package quasarusers.util;

import com.sdm.quasar.lang.Symbol;
import com.sdm.quasar.view.View;
import com.sdm.quasar.view.AbstractView;
import com.sdm.quasar.component.CommandAdapter;
import com.sdm.quasar.component.CommandEvent;
import com.sdm.quasar.component.NoSuchCommandException;
import com.sdm.quasar.component.ComponentException;
import com.sdm.quasar.dataview.DataView;
import de.sdm.sia.remis.berechtigung.praesentation.benutzer.suchen.BenutzerDataView;

/**
 * Created by IntelliJ IDEA.
 * User: schmickl
 * Date: Dec 19, 2002
 * Time: 5:25:20 PM
 * To change this template use Options | File Templates.
 */
//todo dpk 23.01.03 -> MC kommentieren
public class ViewHelper {
  public static void addUpdateListener(final View view, Symbol command) {
    try {
      view.lookupCommand(command).addCommandListener(new CommandAdapter() {
        public void commandPerformed(CommandEvent event) {
          view.showView();
        }
      });
    } catch (NoSuchCommandException e) {
      e.printStackTrace();
    }
  }

  public static void addCloseListener(final View view, Symbol command) {
    try {
      view.lookupCommand(command).addCommandListener(new CommandAdapter() {
        public void commandPerformed(CommandEvent event) {
          try {
            view.performCommand(AbstractView.CANCEL_VIEW,
                                                   event.getArguments());
          } catch (ComponentException e) {
            e.printStackTrace();
          }
        }
      });
    } catch (NoSuchCommandException e) {
      e.printStackTrace();
    }
  }

  public static void addQueryListener(final DataView view, Symbol command) {
    try {
      view.lookupCommand(command).addCommandListener(new CommandAdapter() {
        public void commandPerformed(CommandEvent event) {
          try {
            view.performCommand(DataView.RUN_QUERY, event.getArguments());
          } catch (ComponentException e) {
            e.printStackTrace();
          }
        }
      });
    } catch (NoSuchCommandException e) {
      e.printStackTrace();
    }
  }
}
