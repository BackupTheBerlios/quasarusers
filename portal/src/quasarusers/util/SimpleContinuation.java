package quasarusers.util;

import com.sdm.quasar.continuation.Continuation;
import com.sdm.quasar.continuation.ContinuationManager;
import com.sdm.quasar.continuation.StandardContinuationManager;
import com.sdm.quasar.util.Debug;
import com.sdm.quasar.util.TraceLevel;
import com.sdm.quasar.util.Arrays;

/**
 * Dies ist eine einfache Contunuation, die Features zum Unsterstützen
 * von Debugging liefert.
 *
 * @author Marco Schmickler
 */
public class SimpleContinuation implements Continuation {
    private Continuation nextContinuation = null;
    private String name;

    public SimpleContinuation(String name) {
        this.name = name;
    }

    public final Continuation getNextContinuation() {
        return nextContinuation;
    }

    public final void setNextContinuation(Continuation continuation) {
        nextContinuation = continuation;
    }

    public void continueWithResult(Object result) {
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer("Continuation ");
        buffer.append(name);
        Continuation continuation = getNextContinuation();

        while (continuation != null) {
            buffer.append(", ");
            buffer.append(continuation.toString());

            if (continuation instanceof SimpleContinuation)
                break;

            continuation = continuation.getNextContinuation();
        }

        return  buffer.toString();
    }

    public void continueWithException(Exception exception) {
        if (Debug.DEBUG && Debug.isTraced("com.sdm.quasar.continuation.ContinuationManager", TraceLevel.LOW)) {
            Debug.trace("com.sdm.quasar.continuation.ContinuationManager",
                        "Continue with exception for continuation " + name);
            exception.printStackTrace(Debug.getPrintStream());
        }
    }

    public static void showContinuationStack() {
        showContinuationStack(StandardContinuationManager.getContinuationManager());
    }

    public static void showContinuationStack(ContinuationManager continuationManager) {
        if (Debug.DEBUG && Debug.isTraced("com.sdm.quasar.continuation.ContinuationManager", TraceLevel.LOW)) {
            Continuation continuation = continuationManager.getContinuation();
            StringBuffer buffer = new StringBuffer("Continuation ");

            while (continuation != null) {
                buffer.append(continuation.toString());

                if (continuation instanceof SimpleContinuation)
                    break;

                continuation = continuation.getNextContinuation();
            }

            Debug.trace("com.sdm.quasar.continuation.ContinuationManager",
                        "Continuation Stack Trace " + buffer);
        }
    }

    public static Continuation[] getContinuationStack() {
        return getContinuationStack(StandardContinuationManager.getContinuationManager());
    }

    public static Continuation[] getContinuationStack(ContinuationManager continuationManager) {
        Continuation continuations[] = new Continuation[0];
        Continuation continuation = continuationManager.getContinuation();

        while (continuation != null) {
            continuations = (Continuation[])Arrays.add(Continuation.class, continuations, continuation);
            continuation = continuation.getNextContinuation();
        }

        return continuations;
    }
}
