package quasarusers.util;

import com.sdm.quasar.continuation.AbstractContinuation;
import com.sdm.quasar.continuation.StandardContinuationManager;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.lang.Symbol;
import com.sdm.quasar.util.Debug;
import com.sdm.quasar.util.TraceLevel;
import com.sdm.quasar.view.View;
import com.sdm.quasar.view.ViewManager;
import com.sdm.quasar.view.ViewTransaction;
import com.sdm.quasar.view.server.ViewServer;
import com.sdm.quasar.view.server.ViewServerManager;

/**
 * Diese Klasse vereinfacht das Ausführen eines Commandos in einer View.
 * Sie stellt einen ViewTransaction-Kontext bereit.
 *
 * @author Marco Schmickler
 */
//todo dpk 23.01.03 -> MC ??? kann wohl gelöscht werden
public class CommandHelper {
//  public static void performCommandWithTransaction(final View view, final Symbol command, final Keywords arguments) {
//    final ViewManager viewManager = ViewManager.getViewManager();
//    final ViewTransaction transaction = viewManager.beginTransaction();
//    final Keywords parameters = new Keywords(arguments, "viewtransaction", transaction);
//
//    final AbstractContinuation continuation = new AbstractContinuation(StandardContinuationManager.getContinuationManager()) {
//      public void continueWithResult(Object result) {
//        viewManager.commitTransaction(transaction);
//      }
//
//      public void continueWithException(Exception e) {
//        if (Debug.DEBUG && Debug.isTraced("com.sdm.quasar.view", TraceLevel.LOW)) {
//          Debug.trace("com.sdm.quasar.view", "Command " + command + " failed");
//          e.printStackTrace(Debug.getPrintStream());
//        }
//
//        viewManager.rollbackTransaction(transaction);
//      }
//    };
//
//    viewManager.getThreadPoolAdapter().start(
//            new Runnable() {
//              public void run() {
//                view.performCommand(command, parameters, continuation);
//              }
//            });
//  }
//
//  public static void performCommandWithTransaction(final ViewServer viewServer,
//                                                   final Symbol command,
//                                                   final Keywords arguments) {
//    final ViewServerManager viewServerManager = ViewServerManager.getViewServerManager();
//    final ViewTransaction transaction = viewServerManager.beginTransaction();
//
//    arguments.addValue("viewtransaction", transaction);
//
//    final AbstractContinuation continuation
//            = new AbstractContinuation(StandardContinuationManager.getContinuationManager()) {
//              public void continueWithResult(Object result) {
//                viewServerManager.commitTransaction(transaction);
//              }
//
//              public void continueWithException(Exception e) {
//                if (Debug.DEBUG && Debug.isTraced("com.sdm.quasar.view", TraceLevel.LOW)) {
//                  Debug.trace("com.sdm.quasar.view",
//                              "Command " + command + " failed");
//                  e.printStackTrace(Debug.getPrintStream());
//                }
//
//                viewServerManager.rollbackTransaction(transaction);
//              }
//            };
//
//    viewServerManager.getThreadPoolAdapter().start(
//            new Runnable() {
//              public void run() {
//                viewServer.performCommand(command, arguments, continuation);
//              }
//            });
//  }
}
