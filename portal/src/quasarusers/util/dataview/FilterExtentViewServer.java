package quasarusers.util.dataview;

import com.sdm.quasar.businessobject.extentview.server.ExtentViewServer;
import com.sdm.quasar.component.ComponentException;
import com.sdm.quasar.continuation.StandardContinuationManager;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.modelview.server.model.OperationType;
import com.sdm.quasar.transaction.implementation.LocalTransactionManager;
import quasarusers.portal.ConfigurableRuntime;
import quasarusers.util.SimpleContinuation;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;

/**
 * Created by IntelliJ IDEA.
 * User: schmickl
 * Date: Jan 23, 2003
 * Time: 11:36:06 AM
 * To change this template use Options | File Templates.
 */

//todo dpk 23/01/2003 -> Mc kommentieren

public class FilterExtentViewServer extends ExtentViewServer {
    public FilterExtentViewServer(Keywords arguments) throws ComponentException {
        super(arguments);
    }

    public void edit(Keywords arguments) {
        final LocalTransactionManager transactionManager = ConfigurableRuntime.getTransactionManager();

        try {
            transactionManager.suspend();
            transactionManager.begin();

            final Transaction transaction = transactionManager.getTransaction();

            StandardContinuationManager.getContinuationManager().pushContinuation(new SimpleContinuation("edit") {
                public void continueWithResult(Object result) {
                    try {
                        transactionManager.suspend();
                        transactionManager.resume(transaction);
                        transaction.commit();
                        transactionManager.suspend();
                    }
                    catch (SystemException e) {
                        e.printStackTrace();  //To change body of catch statement use Options | File Templates.
                    }
                    catch (IllegalStateException e) {
                        e.printStackTrace();  //To change body of catch statement use Options | File Templates.
                    }
                    catch (SecurityException e) {
                        e.printStackTrace();  //To change body of catch statement use Options | File Templates.
                    }
                    catch (InvalidTransactionException e) {
                        e.printStackTrace();  //To change body of catch statement use Options | File Templates.
                    }
                    catch (RollbackException e) {
                        e.printStackTrace();  //To change body of catch statement use Options | File Templates.
                    }
                    catch (HeuristicMixedException e) {
                        e.printStackTrace();  //To change body of catch statement use Options | File Templates.
                    }
                    catch (HeuristicRollbackException e) {
                        e.printStackTrace();  //To change body of catch statement use Options | File Templates.
                    }
                }

                public void continueWithException(Exception exception) {
                    try {
                        transactionManager.suspend();
                        transactionManager.resume(transaction);
                        transaction.rollback();
                        transactionManager.suspend();
                    }
                    catch (SystemException e) {
                        e.printStackTrace();  //To change body of catch statement use Options | File Templates.
                    }
                    catch (InvalidTransactionException e) {
                        e.printStackTrace();  //To change body of catch statement use Options | File Templates.
                    }
                }
            });

            super.edit(arguments);

        }
        catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
    }

    public void beginTransaction(OperationType type) {  // todo
        final LocalTransactionManager transactionManager = ConfigurableRuntime.getTransactionManager();

        try {
            transactionManager.suspend();
            transactionManager.begin();
        }
        catch (NotSupportedException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
        catch (IllegalStateException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
        catch (SystemException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
    }

    public void endTransaction(boolean b) { // todo
        final LocalTransactionManager transactionManager = ConfigurableRuntime.getTransactionManager();

        try {
            if (b)
                transactionManager.commit();
            else
                transactionManager.rollback();

            transactionManager.suspend();
        }
        catch (RollbackException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
        catch (IllegalStateException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
        catch (SystemException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
        catch (HeuristicRollbackException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
        catch (SecurityException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
        catch (HeuristicMixedException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
    }
}
