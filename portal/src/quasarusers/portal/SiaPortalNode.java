package quasarusers.portal;

import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.session.Session;
import com.sdm.quasar.session.implementation.LocalSessionManager;
import com.sdm.quasar.util.HashMapSize;
import com.sdm.quasar.view.View;
import com.sdm.quasar.view.ViewContainer;
import com.sdm.quasar.view.ResultProcessor;
import com.sdm.quasar.view.implementation.wings.StackingContainer;
import com.sdm.quasar.view.implementation.wings.WingsApplication;
import com.sdm.quasar.view.quickstart.PortalNode;
import com.sdm.quasar.view.quickstart.PortalViewVisualizer;
import com.sdm.quasar.view.quickstart.server.ServerPortalNode;
import com.sdm.quasar.view.server.ViewServerManager;
import com.sdm.quasar.continuation.StandardContinuationManager;
import com.sdm.quasar.continuation.Continuation;
import quasarusers.portal.menue.MenueView;
import quasarusers.util.SimpleContinuation;
import quasarusers.util.businessobject.ViewProperty;

import javax.transaction.SystemException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Dies ist der {@link PortalNode} für REMIS. Er beschreibt, was passiert, wenn
 * der Anwender einen Eintrag im Menübaum betätigt.
 *
 * @author Marco Schmickler
 */
public final class SiaPortalNode extends ServerPortalNode {
  private ViewProperty viewProperty;

  /**
   * Erzeugt einen {@link SiaPortalNode}.
   *
   * @param  viewProperty  die zu rufenden {@link ViewProperty} (optional)
   */
  public SiaPortalNode(ViewProperty viewProperty) {
    super(viewProperty.getLabel(Locale.getDefault()), null, ConfigurableRuntime.getViewServerManager(), null);

    this.viewProperty = viewProperty;
  }

  public boolean isActive() {
    return (viewProperty != null);
  }

  /**
   * Öffnet die View für diesen <code>SiaPortalNode</code>.
   * Normalerweise wird die View geöffnet für das von {@link #getObject}
   * gelieferte Objekt.
   *
   * @param arguments  die Argumente, mit:<ul>
   *                  <li>{@link String}(?) <code>portalNode</code>:
   *                        Der Name des {@link PortalNode}s
   *                  </ul>
   *                  Die Argumente werden an die gerufene {@link ViewProperty}
   *                  durchgereicht.
   */
  public void openView(Keywords arguments) {
      StandardContinuationManager continuationManager = StandardContinuationManager.getContinuationManager();

      Continuation continuation = continuationManager.suspend();

//      try {
////          viewProperty.open(getObject(), arguments, new ResultProcessor() {
////              public void processResult(Object result) {
////              }
////
////              public void processException(Exception exception) {
////              }
////          });
//      }
//      catch (Exception e) {
//          e.printStackTrace();
//      }

      if (continuation != null) {
          continuationManager.suspend();
          continuationManager.resume(continuation);
      }
  }
}
