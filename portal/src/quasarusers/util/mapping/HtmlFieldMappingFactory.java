package quasarusers.util.mapping;

import com.sdm.quasar.modelview.implementation.wings.WingsFieldMapping;
import com.sdm.quasar.modelview.implementation.wings.WingsFieldMappingFactory;
import com.sdm.quasar.modelview.mapping.FieldMapping;
import com.sdm.quasar.modelview.mapping.FieldMappingClassNotFoundException;
import com.sdm.quasar.modelview.model.FieldModel;
import com.sdm.quasar.modelview.model.ViewModel;
import com.sdm.quasar.util.Assertion;
import quasarusers.util.Today;
import org.wings.SAbstractButton;
import org.wings.SCheckBox;
import org.wings.SComboBox;
import org.wings.SComponent;
import org.wings.SLabel;

import javax.swing.*;
import java.sql.Date;
import java.text.DateFormat;

//todo dpk 23/01/2003 -> MR kommentieren
//todo dpk 23/01/2003 -> KOmmentar einheitlich deutsch?

public final class HtmlFieldMappingFactory extends WingsFieldMappingFactory {
  public Class getFieldMappingClass(Class fieldClass) throws FieldMappingClassNotFoundException {
    if (SLabel.class.isAssignableFrom(fieldClass))
      return WingsHtmlMapping.class;
    else if (SComboBox.class.isAssignableFrom(fieldClass)) {
      return WingsComboboxMapping.class;
    } else if (SAbstractButton.class.isAssignableFrom(fieldClass)) {
      if (!SCheckBox.class.isAssignableFrom(fieldClass)) {
        return WingsButtonMapping.class;
      }
    }

    return super.getFieldMappingClass(fieldClass);
  }

  public FieldMapping makeFieldMapping(Object field,
                                       ViewModel viewModel,
                                       FieldModel fieldModel) throws FieldMappingClassNotFoundException {
    if (fieldModel.getType() == Integer.class) {
      if (field instanceof SCheckBox)
        return new WingsIntegerCheckboxMapping(field, viewModel, fieldModel);
    }

    return super.makeFieldMapping(field, viewModel, fieldModel);
  }

  public static class WingsHtmlMapping extends WingsFieldMapping {
    public WingsHtmlMapping(Object field, ViewModel viewModel, FieldModel fieldModel) {
      super((SLabel) field, viewModel, fieldModel);

      ((SLabel) field).setEscapeSpecialChars(false);

      if (Assertion.CHECK)
        Assertion.check(isValidType(fieldModel),
                        "isValidType(fieldModel)");
    }

    public Object getFieldValue() {
      return ((SLabel) getField()).getText();
    }

    public void setFieldValue(Object value) {
      SLabel label = ((SLabel) getField());

      if (value == null)
        value = "";
      else if (value instanceof HtmlContent)
        value = ((HtmlContent) value).getHTML();
      else if (value instanceof Date) {
        Date date = (Date) value;

        if (date.before(Today.UNDEF_DATE))
          value = DateFormat.getDateInstance().format(date);
        else
          value = "";
      } else if (value instanceof Boolean)
        value = (((Boolean) value).booleanValue()) ? "Ja" : "Nein";

      label.setText(value.toString());
    }

    public boolean isValid() {
      return true;
    }

    public void clearField() {
      ((SLabel) getField()).setText("");
    }
  }

  public static class WingsIntegerCheckboxMapping extends WingsFieldMapping {
    public static final Integer FALSE = new Integer(0);
    public static final Integer TRUE = new Integer(1);

    /**
     * Constructs a <code>WingsIntegerCheckboxMapping/code>. Boolean values are represented as 0 (false) and 1 (true).
     *
     * @param	field           the <code>SCheckBox</code> which represents the field
     * @param	viewModel       the model of the view, for which this field mapping is defined
     * @param	fieldModel      the field model, which is used to get the value for the field of this field
     *                          mapping from the corresponding proxy
     */
    public WingsIntegerCheckboxMapping(Object field, ViewModel viewModel, FieldModel fieldModel) {
      super((SCheckBox) field, viewModel, fieldModel);

      if (Assertion.CHECK)
        Assertion.check(fieldModel.getType() == Integer.class,
                        "fieldModel.getType() == Integer.class");
    }

    public Object getFieldValue() {
      return ((SCheckBox) getField()).isSelected() ? TRUE : FALSE;
    }

    public void setFieldValue(Object value) {
      ((SCheckBox) getField()).setSelected(!FALSE.equals(value));
    }

    public boolean isValid() {
      return true;
    }

    public void clearField() {
      ((SCheckBox) getField()).setSelected(false);
    }
  }

  public static class WingsButtonMapping extends WingsFieldMapping {
    public WingsButtonMapping(Object field, ViewModel viewModel, FieldModel fieldModel) {
      super((SAbstractButton) field, viewModel, fieldModel);

      ((SAbstractButton) field).setShowAsFormComponent(false);

      if (Assertion.CHECK)
        Assertion.check(fieldModel.getType() == String.class,
                        "fieldModel.getType() == fieldModel.getType() == String.class");
    }

    public void closeField() {

    }

    public void openField() {

    }

    public Object getFieldValue() {
      return ((SAbstractButton) getField()).getText();
    }

    public void setFieldValue(Object value) {
      if (value != null)
        ((SAbstractButton) getField()).setText(value.toString());
    }

    public boolean isValid() {
      return true;
    }

    public void clearField() {
      ((SAbstractButton) getField()).setText("");
    }
  }

  public static class WingsComboboxMapping extends WingsFieldMapping {
    public WingsComboboxMapping(Object field, ViewModel viewModel, FieldModel fieldModel) {
      super((SComponent) field, viewModel, fieldModel);

      if (Assertion.CHECK)
        Assertion.check(fieldModel.getType() == String.class,
                        "fieldModel.getType() == fieldModel.getType() == String.class");
    }

    public void closeField() {

    }

    public void openField() {

    }

    public Object getFieldValue() {
      return ((SComboBox) getField()).getSelectedItem();
    }

    public void setFieldValue(Object value) {
      if (value != null) {
        DefaultComboBoxModel model = ((DefaultComboBoxModel) ((SComboBox) getField()).getModel());
        model.removeAllElements();
        model.addElement(value);
      }
    }

    public boolean isValid() {
      return true;
    }

    public void clearField() {
      setFieldValue("");
    }
  }

  /**
   * Gibt an, ob der Typ des Feldmodells von der Mapping Factory unterstützt wird.
   *
   * @param  fieldModel  das Modell für das Feld
   * @return <code>true</code>, falls der Typ des Felds unterstützt wird
   */
  private static boolean isValidType(FieldModel fieldModel) {
    return fieldModel.getType() == HtmlContent.class ||
            fieldModel.getType() == String.class ||
            fieldModel.getType() == Date.class ||
            fieldModel.getType() == Integer.class ||
            fieldModel.getType() == Boolean.class;
  }
}
