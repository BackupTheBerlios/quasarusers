package quasarusers.util;

import com.sdm.quasar.view.ResultProcessor;
import com.sdm.quasar.lang.ThreadState;
import com.sdm.quasar.continuation.Continuation;
import com.sdm.quasar.continuation.StandardContinuationManager;
import com.sdm.quasar.continuation.ContinuationManager;

/**
 * Created by IntelliJ IDEA.
 * User: schmickl
 * Date: Dec 18, 2002
 * Time: 12:11:51 PM
 * To change this template use Options | File Templates.
 */
//todo dpk 23.01.03 -> MC kommentieren
public class ContinuationResultProcessor implements ResultProcessor {
  private ThreadState threadState;
  private ContinuationManager continuationManager = StandardContinuationManager.getContinuationManager();

  public ContinuationResultProcessor() {
    this(null);
  }

  public ContinuationResultProcessor(Continuation continuation) {
    if (continuation != null)
      continuationManager.pushContinuation(continuation);

    try {
      threadState = ThreadState.save();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void processResult(Object result) {
    try {
      threadState.restore();

      continuationManager.continueWithResult(result);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void processException(Exception exception) {
    try {
      threadState.restore();

      continuationManager.continueWithResult(exception);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
