/*
 * Created by IntelliJ IDEA.
 * User: rademach
 * Date: 22.03.2002
 * Time: 13:07:34
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package quasarusers.util.modelview.validation;

import com.sdm.quasar.util.LocalizedStrings;
import com.sdm.quasar.validation.ValidationAttributeAccessException;
import com.sdm.quasar.validation.ValidationController;
import com.sdm.quasar.validation.ValidationException;

public abstract class AttributeValidation extends CrossValidation {
  /**
   * Erzeugt eine <code>AttributeValidation</code> für einen Fehler.
   *
   * @param  validationName    der Name der Validierung
   * @param  mMessages         die Fehlermeldungen
   * @param  lastPathPart      das Attribut, auf das der Cursor gesetzt
   *                            werden soll,wenn der Fehler auftritt
   */
  public AttributeValidation(String validationName,
                             LocalizedStrings mMessages,
                             String lastPathPart) {
    super(validationName, mMessages, lastPathPart);
  }

  /**
   * Erzeugt eine <code>AttributeValidation</code> für einen Fehler oder eine
   * Warnung.
   *
   * @param  validationName   der Name der Validierung
   * @param  isWarning        die Angabe, ob es sich um eine Warnung handelt
   * @param  mMessages        die Fehlermeldungen
   * @param lastPathPart      das Attribut, auf das der Cursor gesetzt
   *                           werden soll,wenn der Fehler auftritt
   */
  public AttributeValidation(String validationName,
                             boolean isWarning,
                             LocalizedStrings mMessages,
                             String lastPathPart) {
    super(validationName, isWarning, mMessages, lastPathPart);
  }

  /**
   * Ermittelt das zu validierende Attribut.
   *
   * @param  validationController  der aktuelle Validierungskontext
   * @param   object               das validierte Objekt
   * @return  das validierte Attribut
   */
  public Object getValidationObject(ValidationController validationController,
                                    Object object) throws ValidationException {
    try {
      return getPropertyModel(validationController, (String) getLastPathPart()).getValue(object);
    } catch (ValidationException e) {
      throw e;
    } catch (Exception e) {
      throw new ValidationAttributeAccessException(e,
                                                   "Exception while accessing attribute" + getLastPathPart());
    }
  }
}