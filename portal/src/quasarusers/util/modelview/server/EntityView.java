package quasarusers.util.modelview.server;

import com.sdm.quasar.businessobject.AbstractView;
import com.sdm.quasar.businessobject.BusinessObjectManager;
import com.sdm.quasar.continuation.Continuation;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.modelview.server.model.AttributeModel;
import com.sdm.quasar.modelview.server.model.ObjectModel;
import com.sdm.quasar.modelview.server.model.RelationshipModel;
import com.sdm.quasar.util.Assertion;
import com.sdm.quasar.util.LocalizedString;
import com.sdm.quasar.view.server.ViewDescription;
import com.sdm.quasar.view.server.ViewServerManager;
import quasarusers.util.ContinuationResultProcessor;
import quasarusers.portal.ConfigurableRuntime;

/**
 * This class represents a combined implementation of the interfaces {@link com.sdm.quasar.businessobject.View}
 * in package <code>com.sdm.quasar.businessobject</code> and {@link com.sdm.quasar.view.View}. It therefore
 * represents a server side representation of a model based view for a business object. The view may be opened
 * by calling the <code>open</code> method on the corresponding <code>BusinessObjectController</code>.
 *
 * @author  Oliver Juwig
 * @version 1.0
 * @see     com.sdm.quasar.businessobject.BusinessObjectController#open
 */
public class EntityView extends AbstractView implements ViewDescription {
  private final String viewClassName;
  private ObjectModel objectModel;

  /**
   * Constructs an <code>EntityView</code>.
   *
   * @param	name			    the identifying name of the object view
   * @param	label			    the logical label of the object view as localized string
   * @param	documentation	    the localized documentation of the object view
   * @param	viewClassName       the qualified name of the Java class for the client side {@link com.sdm.quasar.modelview.ObjectView}
   * @param	objectModel         the <code>ObjectModel</code> describing the structure and behaviour of the objects presented by the object view
   * @see		com.sdm.quasar.modelview.ObjectView
   * @see		com.sdm.quasar.modelview.CollectionView
   * @see		com.sdm.quasar.modelview.server.model.ObjectModel
   * @see		com.sdm.quasar.modelview.server.ObjectViewServer
   * @see		com.sdm.quasar.modelview.server.CollectionViewServer
   * @see		com.sdm.quasar.modelview.server.ObjectController
   * @see		com.sdm.quasar.modelview.server.CollectionController
   */
  public EntityView(String name, LocalizedString label, LocalizedString documentation,
                    String viewClassName, ObjectModel objectModel) {
    super(name, label, documentation);

    if (Assertion.CHECK) {
      Assertion.checkNotNull(viewClassName, "viewClassName");
      // MA changed
      //Assertion.checkNotNull(objectModel, "objectModel");
    }

    this.viewClassName = viewClassName;
    this.objectModel = objectModel;
  }

  /**
   * Constructs an <code>EntityView</code>.
   *
   * @param	name			    the identifying name of the object view
   * @param	label			    the logical label of the object view
   * @param	documentation	    the documentation of the object view
   * @param	viewClassName       the qualified name of the Java class for the client side {@link com.sdm.quasar.modelview.ObjectView}
   * @param	objectModel         the <code>ObjectModel</code> describing the structure and behaviour of the objects presented by the object view
   * @see		com.sdm.quasar.modelview.ObjectView
   * @see		com.sdm.quasar.modelview.CollectionView
   * @see		com.sdm.quasar.modelview.server.model.ObjectModel
   * @see		com.sdm.quasar.modelview.server.ObjectViewServer
   * @see		com.sdm.quasar.modelview.server.CollectionViewServer
   * @see		com.sdm.quasar.modelview.server.ObjectController
   * @see		com.sdm.quasar.modelview.server.CollectionController
   */
  public EntityView(String name, String label, String documentation,
                    String viewClassName, ObjectModel objectModel) {
    super(name, label, documentation);

    if (Assertion.CHECK) {
      Assertion.checkNotNull(viewClassName, "viewClassName");
      // MA changed
      //Assertion.checkNotNull(objectModel, "objectModel");
    }

    this.viewClassName = viewClassName;
    this.objectModel = objectModel;
  }

  /**
   * Constructs an <code>EntityView</code>.
   *
   * @param	name			            the identifying name of the object view
   * @param	label			            the logical label of the object view as localized string
   * @param	documentation	            the localized documentation of the object view
   * @param	viewClassName               the qualified name of the Java class for the client side {@link com.sdm.quasar.modelview.ObjectView}
   * @param	viewVisualizerClassName     the qualified name of the Java class for the client side {@link com.sdm.quasar.modelview.ObjectViewVisualizer}
   * @param	objectModel                 the <code>ObjectModel</code> describing the structure and behaviour of the objects presented by the object view
   * @see		com.sdm.quasar.modelview.ObjectView
   * @see		com.sdm.quasar.modelview.ObjectViewVisualizer
   * @see		com.sdm.quasar.modelview.CollectionView
   * @see		com.sdm.quasar.modelview.server.model.ObjectModel
   * @see		com.sdm.quasar.modelview.server.ObjectViewServer
   * @see		com.sdm.quasar.modelview.server.CollectionViewServer
   * @see		com.sdm.quasar.modelview.server.ObjectController
   * @see		com.sdm.quasar.modelview.server.CollectionController
   */
  public EntityView(String name, LocalizedString label, LocalizedString documentation,
                    String viewClassName, String viewVisualizerClassName, ObjectModel objectModel) {
    super(name, label, documentation);

    if (Assertion.CHECK) {
      Assertion.checkNotNull(viewClassName, "viewClassName");
      // MA changed
      //Assertion.checkNotNull(objectModel, "objectModel");
    }

    this.viewClassName = viewClassName;
    this.objectModel = objectModel;
  }

  /**
   * Constructs an <code>EntityView</code>.
   *
   * @param	name			            the identifying name of the object view
   * @param	label			            the logical label of the object view
   * @param	documentation	            the documentation of the object view
   * @param	viewClassName               the qualified name of the Java class for the client side {@link com.sdm.quasar.modelview.ObjectView}
   * @param	objectModel                 the <code>ObjectModel</code> describing the structure and behaviour of the objects presented by the object view
   * @param	viewVisualizerClassName     the qualified name of the Java class for the client side {@link com.sdm.quasar.modelview.ObjectViewVisualizer}
   * @see		com.sdm.quasar.modelview.ObjectView
   * @see		com.sdm.quasar.modelview.ObjectViewVisualizer
   * @see		com.sdm.quasar.modelview.CollectionView
   * @see		com.sdm.quasar.modelview.server.model.ObjectModel
   * @see		com.sdm.quasar.modelview.server.ObjectViewServer
   * @see		com.sdm.quasar.modelview.server.CollectionViewServer
   * @see		com.sdm.quasar.modelview.server.ObjectController
   * @see		com.sdm.quasar.modelview.server.CollectionController
   */
  public EntityView(String name, String label, String documentation,
                    String viewClassName, String viewVisualizerClassName, ObjectModel objectModel) {
    super(name, label, documentation);

    if (Assertion.CHECK) {
      Assertion.checkNotNull(viewClassName, "viewClassName");
      // MA changed
      //Assertion.checkNotNull(objectModel, "objectModel");
    }

    this.viewClassName = viewClassName;
    this.objectModel = objectModel;
  }

  public final String getViewClassName() {
    return viewClassName;
  }

  public final String getViewServerClassName() {
    return getObjectModel().getServerClassName();
  }

  /**
   * Returns the <code>ObjectModel</code>, that describes the structure and behaviour of the objects
   * presented by the object view. This model is handled by the corresponding
   * {@link com.sdm.quasar.modelview.server.ObjectViewServer}.
   *
   * @return  the <code>ObjectModel</code> to be used for the object view
   */
  public final ObjectModel getObjectModel() {
    if (objectModel == null) {
      objectModel = makeObjectModel();

      checkAttributeModels();
      checkRelationshipModels();
    }

    return objectModel;
  }

  protected ObjectModel makeObjectModel() {
    return null;
  }

  protected void computeValidations() {
    return;
  }

  public final boolean hasViewServer() {
    return true;
  }

  public void open(Object object, Keywords arguments, Continuation continuation) {
    arguments = (Keywords) arguments.clone();

    Object isNew = arguments.getValue("new", this);

    if (isNew == this)
      isNew = (object == null) ? Boolean.TRUE : Boolean.FALSE;

    if (Boolean.TRUE.equals(isNew))
      arguments.addValue("new", isNew);
    else {
      if (arguments.getValue("load", this) == this)
        arguments.addValue("load", Boolean.TRUE);

      if (arguments.getValue("lock", this) == this)
        arguments.addValue("lock", Boolean.TRUE);

      if (arguments.getValue("timeout", this) == this)
        arguments.addValue("timeout", new Integer(1000));
    }

    arguments.addValue("objectModel", getObjectModel());

    try {
      arguments.addValue("transaction", BusinessObjectManager.getBusinessObjectManager().getTransactionManager().getTransaction());

      ConfigurableRuntime.getViewServerManager().openView(this, object, arguments, new ContinuationResultProcessor(continuation));
    } catch (Exception e) {
      BusinessObjectManager.getBusinessObjectManager().getContinuationManager().continueWithException(e);
    }
  }

  private void checkAttributeModels() {
    // Prüfen, ob Attributemodels gesetzt
    final AttributeModel[] models = objectModel.getAttributeModels(true);
    final int length = models.length;
    boolean panic = false;

    for (int i = 0; i < length; i++)
      if (models[i] == null) {
        System.out.println("ERROR: Attribute model with 0-based index " + i + " is null");
        panic = true;
      }

    if (panic)
      throw new RuntimeException("AttributeModel(s) not defined in ObjectModel " + objectModel.getName());
  }

  private void checkRelationshipModels() {
    // Prüfen, ob ReationshipModels gesetzt
    final RelationshipModel[] models = objectModel.getRelationshipModels(true);
    final int length = models.length;
    boolean panic = false;

    for (int i = 0; i < length; i++)
      if (models[i] == null) {
        System.out.println("ERROR: RelationshipModel with 0-based index " + i + " is null");
        panic = true;
      }

    if (panic)
      throw new RuntimeException("RelationshipModel(s) not defined in ObjectModel " + objectModel.getName());
  }

}
