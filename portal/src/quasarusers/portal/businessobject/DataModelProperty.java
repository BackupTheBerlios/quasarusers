package quasarusers.portal.businessobject;

import com.sdm.quasar.businessobject.AbstractBusinessObjectProperty;
import com.sdm.quasar.businessobject.BusinessObjectManager;
import com.sdm.quasar.businessobject.extentview.server.model.ExtentDataModel;
import com.sdm.quasar.dataview.server.model.DataModel;
import com.sdm.quasar.dataview.server.model.QueryModel;
import com.sdm.quasar.dataview.server.model.SimpleDataModel;
import com.sdm.quasar.persistence.Pool;
import com.sdm.quasar.persistence.TypeModel;
import com.sdm.quasar.persistence.TypeModelNotFoundException;
import com.sdm.quasar.persistence.query.QueryManager;
import com.sdm.quasar.queryeditor.server.xml.XMLQueryEditorModelManager;
import com.sdm.quasar.queryeditor.server.persistence.PersistenceQueryTransformer;
import com.sdm.quasar.queryeditor.model.QueryEditorModel;

import javax.transaction.TransactionManager;

/**
 * Created by IntelliJ IDEA.
 * User: schmickl
 * Date: 22.02.2003
 * Time: 18:46:39
 * To change this template use Options | File Templates.
 */
public class DataModelProperty extends AbstractBusinessObjectProperty implements ExtentDataModel {
    private DataModel dataModel;
    private Class entityType;

    public DataModelProperty(String name, Class entityType, String query) {
        this(name, entityType, new String[] { query });
    }

    public DataModelProperty(String name, Class entityType, String[] query) {
        super(name, "", "");

        try {
            QueryModel[] queryModels = new QueryModel[query.length];

            BusinessObjectManager businessObjectManager = BusinessObjectManager.getBusinessObjectManager();

            Pool pool = (Pool)businessObjectManager.getSingleton(Pool.class);
            QueryManager queryManager = (QueryManager)businessObjectManager.getSingleton(QueryManager.class);
            TransactionManager transactionManager = (TransactionManager)businessObjectManager.getSingleton(TransactionManager.class);

            PersistenceQueryTransformer queryTransformer = new PersistenceQueryTransformer(pool,
                    queryManager, transactionManager);

            for (int i = 0; i < query.length; i++) {
                QueryEditorModel queryEditorModel = (QueryEditorModel)new XMLQueryEditorModelManager().parseXML(getClass().getClassLoader().getResourceAsStream(query[i]));
                queryModels[i] = queryTransformer.makeQueryModel(queryEditorModel);
            }

            this.dataModel = new SimpleDataModel(queryModels);

        } catch (Exception e) {
            e.printStackTrace();

            this.dataModel = makeDataModel();
        }

        this.entityType = entityType;
    }

    public DataModelProperty(String name, Class entityType) {
        super(name, "", "");

        this.entityType = entityType;
        this.dataModel = makeDataModel();
    }

    protected DataModel makeDataModel() {
        return new SimpleDataModel(new QueryModel[] { null });
    }

    public Class getEntityType() {
        return entityType;
    }

    public DataModel getDataModel() {
        return dataModel;
    }

    public static TypeModel getTypeModel(Class type) throws TypeModelNotFoundException {
        return ((Pool)BusinessObjectManager.getBusinessObjectManager().getSingleton(Pool.class)).getTypeModel(type);
    }
}
