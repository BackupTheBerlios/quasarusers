/*
 * Created by IntelliJ IDEA.
 * User: rademach
 * Date: 17.07.2002
 * Time: 14:28:40
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package quasarusers.util;

import com.sdm.quasar.transaction.implementation.LocalTransactionManager;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.xa.XAResource;

/**
 * Dies ist eine Transaktion, die die laufende Transaktion zur�ckstellt
 * und eine neue Toplevel-Transaktion startet. Im commit und rollback
 * wird die urspr�ngliche Transaktion wiederhergestellt.<p>
 *
 * Diese Klasse eignet sich zum Umschiffen folgender transaktionstechnischer
 * Probleme:<ul>
 *
 * <li> Bevor eine Query ausgef�hrt wird, werden von der laufenden
 *      Transaktion ver�nderte Daten der betroffenen Tabellen in die DB
 *      geflusht. Wenn in der laufenden Transaktion noch nicht validierte
 *      Objekte existieren, k�nnen dabei von der DB Exceptions geliefert
 *      werden und die Query wird nicht ausgef�hrt. F�hrt man die Query
 *      in einer <code>ToplevelTransaction</code> aus, passiert dies nicht.
 *
 * <li> Wenn man sich aktuell in einer Subtransaktion befindet, sind die
 *      Daten nach dem commit noch nicht endg�ltig in der DB. Dieses Verhalten
 *      ist normalerweise gew�nscht. Falls man jedoch Daten committen muss
 *      (z. B. weil gleichzeitig irreversible �nderungen im Dateisystem
 *      durchgef�hrt werden), kann man dies in einer  <code>ToplevelTransaction</code>
 *      tun.
 *
 * </ul>
 *
 * @author Marco Schmickler
 */
public class ToplevelTransaction implements Transaction {
  Transaction oldTransaction;
  Transaction transaction;
  LocalTransactionManager transactionManager = quasarusers.portal.ConfigurableRuntime.getTransactionManager();

  public ToplevelTransaction() throws Exception {
    oldTransaction = transactionManager.suspend();

    try {
      transactionManager.begin();
      transaction = transactionManager.getTransaction();
    } catch (Exception e) {
      if (oldTransaction != null)
        transactionManager.resume(oldTransaction);

      throw e;
    }
  }

  public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, SystemException {
    transaction.commit();

    try {
      if (oldTransaction != null)
        transactionManager.resume(oldTransaction);
    } catch (Exception e) {
    }
  }

  public void rollback() throws IllegalStateException, SystemException {
    transaction.rollback();

    try {
      if (oldTransaction != null)
        transactionManager.resume(oldTransaction);
    } catch (Exception e) {
    }
  }

  public boolean delistResource(XAResource resource, int i) throws IllegalStateException, SystemException {
    return transaction.delistResource(resource, i);
  }

  public boolean enlistResource(XAResource resource) throws RollbackException, IllegalStateException, SystemException {
    return transaction.enlistResource(resource);
  }

  public int getStatus() throws SystemException {
    return transaction.getStatus();
  }

  public void registerSynchronization(Synchronization synchronization) throws RollbackException, IllegalStateException, SystemException {
    transaction.registerSynchronization(synchronization);
  }

  public void setRollbackOnly() throws IllegalStateException, SystemException {
    transaction.setRollbackOnly();
  }
}
