/*
 * Created by IntelliJ IDEA.
 * User: rademach
 * Date: 26.03.2002
 * Time: 16:21:59
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package quasarusers.util.modelview.server;

import com.sdm.quasar.continuation.ContinuationManager;
import com.sdm.quasar.continuation.StandardContinuationManager;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.modelview.server.CollectionController;
import com.sdm.quasar.modelview.server.ObjectViewServer;
import com.sdm.quasar.modelview.server.model.LockMode;
import com.sdm.quasar.modelview.server.persistence.PersistenceObjectController;
import com.sdm.quasar.persistence.Persistent;
import com.sdm.quasar.persistence.Pool;
import com.sdm.quasar.util.Assertion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * {@link CollectionController} für transiente {@link Collection}s.
 * Implementiert nur die Methoden, die bisher für sia gebraucht wurden.
 */

//todo dpk 23/01/2003 -> MR kommentieren

public class SimpleCollectionController implements CollectionController {
  ObjectViewServer mObjectViewServer;

  public class SimpleCollectionIterator implements CollectionController.CollectionIterator {
    Collection mCollection;
    Iterator mIterator;

    private SimpleCollectionIterator(Collection collection) {
      mCollection = collection;

      mIterator = collection.iterator();
    }

    public boolean hasNextObject() throws Exception {
      return mIterator.hasNext();
    }

    public Object nextObject() throws Exception {
      return mIterator.next();
    }
  }

  public SimpleCollectionController() {
  }

  /**
   * Erzeugt einen <code>SimpleCollectionController</code>. Wird von jeff
   * zur benötigt, um Controllers via Reflection zu erzeugen.
   */
  public SimpleCollectionController(Keywords k) {
  }

  public boolean isObject(Object object) {
    return true;
  }

  public void make(Keywords arguments) {
    ContinuationManager continuationManager = StandardContinuationManager.getContinuationManager();

    try {
      continuationManager.continueWithResult(new ArrayList(7));
    } catch (Exception e) {
      continuationManager.continueWithException(e);
    }
  }

  public void lookup(Keywords values, Keywords arguments) {
    Assertion.fail("lookup not yet implemented");
  }

  public Object load(Object object, LockMode lockMode, int timeout) throws Exception {
    if (object instanceof Persistent) {
      Persistent persistent = (Persistent) object;
      Pool pool = persistent.getPool();

      return pool.lookup(pool.getInstanceType(persistent.getTypeModel()),
                         persistent.getPrimaryKey(),
                         PersistenceObjectController.toPersistentLockMode(lockMode), timeout);
    }

    return object;
  }

  public Object lock(Object object, LockMode lockMode, int timeout) throws Exception {
    return null;
  }

  public void save(Object object) throws Exception {
  }

  public void revert(Object object) throws Exception {
  }

  public boolean delete(Object object) throws Exception {
    return true;
  }

  public void setObjectViewServer(ObjectViewServer objectViewServer) {
    mObjectViewServer = objectViewServer;
  }

  public boolean isCollection(Object object) {
    return object instanceof Collection;
  }

  public CollectionController.CollectionIterator iterator(Object collection, LockMode lockMode, int timeout) throws Exception {
    return new SimpleCollectionIterator((Collection) collection);
  }

  public void addObject(Object collection, Object object) throws Exception {
    ((Collection) collection).add(object);
  }

  public void removeObject(Object collection, Object object) throws Exception {
    ((Collection) collection).remove(object);
  }
}
