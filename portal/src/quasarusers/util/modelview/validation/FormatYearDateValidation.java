/*
 * Created by IntelliJ IDEA.
 * User: kerstjen
 * Date: 29.11.2002
 * Time: 12:03:42
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package quasarusers.util.modelview.validation;

import com.sdm.quasar.util.LocalizedStrings;
import com.sdm.quasar.validation.ValidationController;
import com.sdm.quasar.validation.ValidationException;

import java.sql.Date;

/**
 *  Dies ist eine Validierung für die ModelView. Sie validiert, ob ein Datum mit vierstelliger
 *  Jahresangabe eingegeben worden ist.<p>
 *
 *  Dies ist Workaround für das Problem, dass in Java sowohl die Kurzeingabe
 *  "00" und "01" auf das Jahr 0001 gemappt werden.
 *
 * @author Dominic Kerstjens
 */
public class FormatYearDateValidation extends AttributeValidation {
  private String attributeModelName;

  public FormatYearDateValidation(String validationName,
                                  LocalizedStrings mMessages,
                                  String lastPathPart,
                                  String attributeModelName) {
    super(validationName, mMessages, lastPathPart);
    this.attributeModelName = attributeModelName;
  }

  public FormatYearDateValidation(String validationName,
                                  boolean isWarning,
                                  LocalizedStrings mMessages,
                                  String lastPathPart,
                                  String attributeModelName) {
    super(validationName, isWarning, mMessages, lastPathPart);
    this.attributeModelName = attributeModelName;
  }

  public boolean check(ValidationController validationController,
                       Object object) throws ValidationException {

    boolean returnValue = false;

    if (object instanceof Date) {
      returnValue = ((Date) object).after(Date.valueOf("1899-12-31"));
    } else if (object == null) {
      returnValue = true;
    }

    return returnValue;
  }

}
