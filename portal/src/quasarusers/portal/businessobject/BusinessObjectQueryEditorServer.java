package quasarusers.portal.businessobject;

import com.sdm.quasar.queryeditor.server.QueryEditorServer;
import com.sdm.quasar.queryeditor.server.persistence.PersistenceQueryTransformer;
import com.sdm.quasar.queryeditor.server.persistence.PersistenceRepositoryAdapter;
import com.sdm.quasar.queryeditor.model.QueryEditorModel;
import com.sdm.quasar.queryeditor.model.StandardQueryEditorModel;
import com.sdm.quasar.queryeditor.model.ObjectModel;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.component.ComponentException;
import com.sdm.quasar.businessobject.BusinessObjectManager;
import com.sdm.quasar.businessobject.BusinessObjectProperty;
import com.sdm.quasar.businessobject.PropertySelector;
import com.sdm.quasar.businessobject.BusinessObject;
import com.sdm.quasar.businessobject.extentview.server.model.SimpleExtentDataModel;
import com.sdm.quasar.businessobject.extentview.server.model.ExtentDataModel;
import com.sdm.quasar.persistence.Pool;
import com.sdm.quasar.persistence.query.QueryManager;
import com.sdm.quasar.dataview.server.model.DataModel;
import com.sdm.quasar.view.server.ViewDescription;
import com.sdm.quasar.view.server.SimpleViewDescription;
import com.sdm.quasar.util.registry.RegistryObject;

import javax.transaction.TransactionManager;

/**
 * Created by IntelliJ IDEA.
 * User: schmickl
 * Date: Feb 14, 2003
 * Time: 6:00:55 PM
 * To change this template use Options | File Templates.
 */
public class BusinessObjectQueryEditorServer extends QueryEditorServer {
    public BusinessObjectQueryEditorServer(Keywords arguments) throws ComponentException {
        super(arguments);
    }

    protected void initialize(Keywords arguments) {
        BusinessObjectManager businessObjectManager = BusinessObjectManager.getBusinessObjectManager();

        Pool pool = (Pool)businessObjectManager.getSingleton(Pool.class);
        QueryManager queryManager = (QueryManager)businessObjectManager.getSingleton(QueryManager.class);
        TransactionManager transactionManager = (TransactionManager)businessObjectManager.getSingleton(TransactionManager.class);

        PersistenceQueryTransformer queryTransformer = new PersistenceQueryTransformer(pool,
                queryManager, transactionManager);

        PersistenceRepositoryAdapter repositoryAdapter = new PersistenceRepositoryAdapter(pool);

        arguments.addValue("queryTransformer", queryTransformer);
        arguments.addValue("repositoryAdapter", repositoryAdapter);

        Object type = arguments.getValue("objectModel");

        if (type instanceof ObjectModel)
            type = ((ObjectModel)type).getType().getName();

        arguments.addValue("object", new StandardQueryEditorModel((String)type));

        super.initialize(arguments);
    }

    public void openDataView(DataModel dataModel) throws Exception {
        BusinessObjectManager businessObjectManager = BusinessObjectManager.getBusinessObjectManager();

        BusinessObject businessObject = businessObjectManager.getObject("RemisBS.ReiseComponent");

        BusinessObjectProperty[] extentDataModels = businessObject.getProperties(new PropertySelector(ExtentDataModel.class));

        ExtentDataModel extentDataModel = (ExtentDataModel)extentDataModels[0];

        extentDataModel.getDataModel().getQueryModels()[0] = dataModel.getDefaultQueryModel();
        extentDataModel.getDataModel().setDefaultQueryModel(dataModel.getDefaultQueryModel());
    }

}
