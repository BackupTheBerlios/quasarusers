/*
 * Created by IntelliJ IDEA.
 * User: rademach
 * Date: 15.10.2002
 * Time: 12:41:21
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package quasarusers.util.modelview.server;

import com.sdm.quasar.component.ComponentException;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.modelview.server.ObjectViewServer;

/**
 * Dies ist die projektspezifische Klasse für ObjectViewServers.
 *
 * @author Matthias Rademacher
 */
public class RemisObjectViewServer extends ObjectViewServer {
  public RemisObjectViewServer(Keywords arguments) throws ComponentException {
    super(arguments);
  }
}
