package quasarusers.util.dataview;

import javax.transaction.TransactionManager;

import com.sdm.quasar.persistence.Pool;
import com.sdm.quasar.persistence.query.QueryManager;
import com.sdm.quasar.businessobject.BusinessObjectManager;
import com.sdm.quasar.dataview.server.model.QueryModel;
import com.sdm.quasar.dataview.server.persistence.PersistenceDataIterator;
import com.sdm.quasar.lang.Keywords;

//todo dpk 23/01/2003 -> MR kommentieren

public class PoolDataIterator extends PersistenceDataIterator {
    public PoolDataIterator(QueryModel queryModel, Keywords arguments) {
        super(queryModel, arguments);
    }

    public Pool getPool() {
        return (Pool)BusinessObjectManager.getBusinessObjectManager().getSingleton(Pool.class);
    }

    public QueryManager getQueryManager() {
        return (QueryManager)BusinessObjectManager.getBusinessObjectManager().getSingleton(QueryManager.class);
    }

    public TransactionManager getTransactionManager() {
        return (TransactionManager)BusinessObjectManager.getBusinessObjectManager().getSingleton(TransactionManager.class);
    }
}
