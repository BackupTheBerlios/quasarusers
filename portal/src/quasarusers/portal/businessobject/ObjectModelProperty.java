package quasarusers.portal.businessobject;

import com.sdm.quasar.businessobject.BusinessObjectManager;
import com.sdm.quasar.businessobject.BusinessObjectProperty;
import com.sdm.quasar.newmodelview.server.model.ObjectModel;
import com.sdm.quasar.newmodelview.server.model.AttributeModel;
import com.sdm.quasar.newmodelview.server.model.RelationshipModel;
import com.sdm.quasar.newmodelview.server.model.LockMode;
import com.sdm.quasar.newmodelview.server.persistence.PersistenceAttributeModel;
import com.sdm.quasar.newmodelview.server.persistence.PersistenceRelationshipModel;
import com.sdm.quasar.newmodelview.server.persistence.PersistenceObjectModel;
import com.sdm.quasar.persistence.Pool;
import com.sdm.quasar.persistence.TypeModel;
import com.sdm.quasar.persistence.TypeModelNotFoundException;
import com.sdm.quasar.persistence.PersistenceException;
import com.sdm.quasar.util.IconSize;
import com.sdm.quasar.util.LocalizedString;

import javax.swing.*;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: schmickl
 * Date: Feb 13, 2003
 * Time: 8:50:26 PM
 * To change this template use Options | File Templates.
 */
public class ObjectModelProperty implements BusinessObjectProperty {
    private ObjectModel objectModel;
    private String propertyName;


    public ObjectModelProperty(String name) {
        super();

        this.propertyName = name;
        this.objectModel = makeObjectModel();
    }

    public ObjectModelProperty() {
        super();

        this.objectModel = makeObjectModel();
    }

    protected ObjectModel makeObjectModel() {
        return null;
    }

    public ObjectModelProperty(String name, ObjectModel objectModel) {
        super();

        this.objectModel = objectModel;
        this.propertyName = name;
    }

    public ObjectModel getObjectModel() {
        return objectModel;
    }

    public boolean isAccessControlled() {
        return false;
    }

    public ImageIcon getIcon(IconSize iconSize) {
        return null;
    }

    public boolean substitutes(BusinessObjectProperty property) {
        return getName().equalsIgnoreCase(property.getName());
    }

    public String getName() {
        if (propertyName != null)
            return propertyName;

        return objectModel.getName();
    }

    public LocalizedString getLabel() {
        return objectModel.getLabel();
    }

    public LocalizedString getDocumentation() {
        return objectModel.getDocumentation();
    }

    public String getLabel(Locale locale) {
        return objectModel.getLabel(locale);
    }

    public String getDocumentation(Locale locale) {
        return objectModel.getDocumentation(locale);
    }

    public static TypeModel getTypeModel(Class type) throws TypeModelNotFoundException {
        return ((Pool)BusinessObjectManager.getBusinessObjectManager().getSingleton(Pool.class)).getTypeModel(type);
    }

    public RelationshipModel makeRelationshipModel(TypeModel typeModel, String name, ObjectModel objectModel, LockMode lockMode) throws PersistenceException {
        return new PersistenceRelationshipModel((com.sdm.quasar.persistence.RelationshipModel) typeModel.getPropertyModel(name),
                                                objectModel, false, lockMode);
    }

    public AttributeModel makeAttributeModel(TypeModel typeModel, String name) throws PersistenceException {
        return new PersistenceAttributeModel((com.sdm.quasar.persistence.AttributeModel) typeModel.getPropertyModel(name));
    }

    public AttributeModel[] makeAttributeModels(TypeModel typeModel, boolean includeInherited) {
        return PersistenceObjectModel.makeAttributeModels(typeModel, includeInherited);
    }
}
