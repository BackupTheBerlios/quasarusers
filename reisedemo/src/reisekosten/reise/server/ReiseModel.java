package reisekosten.reise.server;

import com.sdm.quasar.newmodelview.server.model.ObjectModel;
import com.sdm.quasar.newmodelview.server.model.RelationshipModel;
import com.sdm.quasar.newmodelview.server.model.AttributeModel;
import com.sdm.quasar.newmodelview.server.model.LockMode;
import com.sdm.quasar.persistence.TypeModel;
import com.sdm.quasar.persistence.PersistenceException;
import com.sdm.quasar.newmodelview.server.persistence.PersistenceObjectModel;
import quasarusers.portal.businessobject.ObjectModelProperty;
import com.sdm.quasar.lang.Keywords;


    import reisekosten.*;
    

public class ReiseModel extends ObjectModelProperty {
    public ReiseModel() {
        super("ReiseObjectModel");
    }
     
		

			
			
			
			
				
					
					
					
				
        public static class KostenpunktCollectionController extends quasarusers.portal.businessobject.SimplePersistenceCollectionController {
        }
    
            private ObjectModel makeKostenpunktObjectModel() throws PersistenceException {
        TypeModel typeModel = getTypeModel(Kostenpunkt.class);

        return new PersistenceObjectModel(new Keywords(
                        "typeModel", typeModel,
                        "type", Kostenpunkt.class,
                        "controllerClassName", KostenpunktCollectionController.class.getName(),
                        
                        "attributeModels", makeAttributeModels(typeModel, true)
                        
                        ));

    }
    
			
		
        public static class ReiseController extends quasarusers.portal.businessobject.SimplePersistenceObjectController {
        }
        
            private ObjectModel makeReiseObjectModel() throws PersistenceException {
        TypeModel typeModel = getTypeModel(Reise.class);

        return new PersistenceObjectModel(new Keywords(
                        "typeModel", typeModel,
                        "type", Reise.class,
                        "controllerClassName", ReiseController.class.getName(),
                        
                        "attributeModels", makeAttributeModels(typeModel, true)
                        
                    , "relationshipModels", new RelationshipModel[] {
                        makeRelationshipModel(typeModel, "kostenpunkte", makeKostenpunktObjectModel(), LockMode.REFERENCE),
                         }
                    
                        ));

    }
    
	    protected ObjectModel makeObjectModel() {
        try {
            return makeReiseObjectModel();

        } catch (PersistenceException e) {
            throw new RuntimeException("Exception " + e + " encountered while defining  object model");
        }
    }
}
       