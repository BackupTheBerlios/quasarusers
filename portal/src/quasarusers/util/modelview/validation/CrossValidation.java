/*
 * Created by IntelliJ IDEA.
 * User: rademach
 * Date: 22.03.2002
 * Time: 12:53:39
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package quasarusers.util.modelview.validation;

import com.sdm.quasar.modelview.server.model.AttributeModel;
import com.sdm.quasar.modelview.server.model.ObjectModel;
import com.sdm.quasar.modelview.server.model.PropertyModel;
import com.sdm.quasar.modelview.server.model.RelationshipModel;
import com.sdm.quasar.util.LocalizedMessage;
import com.sdm.quasar.util.LocalizedStrings;
import com.sdm.quasar.validation.StandardValidation;
import com.sdm.quasar.validation.StandardValidationController;
import com.sdm.quasar.validation.ValidationController;

/**
 * Eine Validierung für Kreuzplausis, d. h. für Plausis, die mehrere
 * Attribute eines Objekts betreffen.
 */
public abstract class CrossValidation extends StandardValidation {
  /**
   * Erzeugt eine <code>CrossValidation</code> für einen Fehler.
   *
   * @param  validationName   der Name der Validierung
   * @param  mMessages        die Fehlermeldungen
   * @param navigateAttribute das Attribut, auf das der Cursor gesetzt
   *                           werden soll,wenn der Fehler auftritt
   */
  public CrossValidation(String validationName,
                         LocalizedStrings mMessages,
                         String navigateAttribute) {
    this(validationName, false, mMessages, navigateAttribute);
  }

  /**
   * Erzeugt eine <code>CrossValidation</code> für einen Fehler oder eine
   * Warnung.
   *
   * @param  validationName   der Name der Validierung
   * @param  isWarning        die Angabe, ob es sich um eine Warnung handelt
   * @param  mMessages        die Fehlermeldungen
   * @param navigateAttribute das Attribut, auf das der Cursor gesetzt
   *                           werden soll,wenn der Fehler auftritt
   */
  public CrossValidation(String validationName,
                         boolean isWarning,
                         LocalizedStrings mMessages,
                         String navigateAttribute) {
    super(validationName,
          isWarning,
          new LocalizedMessage(mMessages,
                               validationName + "_MESSAGE"),
          new LocalizedMessage(mMessages,
                               validationName + "_EXPLANATION"),
          navigateAttribute);
  }

  /**
   * Ermittelt das AttributeModel für die angegebene Feld im aktuellen
   * Validierungskontext.
   *
   * @param   validationController    der {@link ValidationController} für
   *                                  den aktuellen Validierungskontext
   * @param   feldname                der Name des Felds, zu dem das AttributeModel
   *                                  ermittelt werden soll
   * @return  das {@link AttributeModel}
   */
  public final AttributeModel getAttributeModel(ValidationController validationController,
                                                String feldname) {
    return (AttributeModel) getPropertyModel(validationController, feldname);
  }

  /**
   * Ermittelt das RelationshipModel für die angegebene Beziehung im aktuellen
   * Validierungskontext.
   *
   * @param   validationController    der {@link ValidationController} für
   *                                  den aktuellen Validierungskontext
   * @param   beziehungsname          der Name der Eigenschaft, zu dem das
   *                                  RelationshipModel ermittelt werden soll
   * @return  das {@link RelationshipModel}
   */
  public final RelationshipModel getRelationshipModel(ValidationController validationController,
                                                      String beziehungsname) {
    return (RelationshipModel) getPropertyModel(validationController, beziehungsname);
  }

  protected final PropertyModel getPropertyModel(ValidationController validationController,
                                                 String eigenschaftsname) {
    try {
      StandardValidationController.ValidationContext validationContext =
              ((StandardValidationController) validationController).getCurrentValidationContext();

      return ((ObjectModel) validationContext.getValidationNode()).getPropertyModel(eigenschaftsname);
    }
    catch (Exception e) {
      e.printStackTrace();

      return null;
    }
  }
}
