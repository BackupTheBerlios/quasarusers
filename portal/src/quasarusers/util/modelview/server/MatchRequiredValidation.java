/*
 * Created by IntelliJ IDEA.
 * User: rademach
 * Date: 07.11.2002
 * Time: 12:07:42
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package quasarusers.util.modelview.server;

import com.sdm.quasar.validation.AttributeModel;
import com.sdm.quasar.validation.AttributeValidation;
import com.sdm.quasar.validation.Fact;
import com.sdm.quasar.validation.ValidationController;
import com.sdm.quasar.validation.ValidationException;

import java.text.MessageFormat;
import java.util.Locale;

/**
 * A special {@link AttributeValidation} which checks for a MatchObjectModel
 * whether a required attribute has a non-<code>null</code> value.
 *
 * @author  Matthias Rademacher
 */
public class MatchRequiredValidation extends AttributeValidation {
  private final String mPrecondition;

  /**
   * Constructs a <code>RequiredAttributeValidation</code>.
   *
   * @param   attributeModel  the <code>AttributeModel</code>, for which the
   *                          validation is defined
   */
  public MatchRequiredValidation(AttributeModel attributeModel) {
    super(attributeModel, "CHECK_MATCH_REQUIRED(" + attributeModel.getName() + ")");

    mPrecondition = "CHECK_TYPE(" + attributeModel.getName() + ")";
  }

  public boolean check(ValidationController validationController,
                       Object object) throws ValidationException {
    return ((Long) object).longValue() != 0; // Oid 0 is used to indicate "no value"
  }

  public Fact[] getPreconditions(Object object) {
    return new Fact[]{new Fact(mPrecondition, object)};
  }

  protected String computeMessage(ValidationController validationController)
          throws ValidationException {
    Locale locale = validationController.getLocale();

    return MessageFormat.format(getMessages().getString("REQUIRED_MESSAGE", locale),
                                new Object[]{getAttributeModel(validationController).getLabel(locale)});
  }

  protected String computeExplanation(ValidationController validationController)
          throws ValidationException {
    Locale locale = validationController.getLocale();

    return MessageFormat.format(getMessages().getString("REQUIRED_EXPLANATION", locale),
                                new Object[]{getAttributeModel(validationController).getLabel(locale)});
  }
}
