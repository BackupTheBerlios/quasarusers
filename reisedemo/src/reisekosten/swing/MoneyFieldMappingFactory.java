package reisekosten.swing;

import java.text.ParseException;
import java.util.List;
import javax.swing.JComponent;

import reisekosten.Money;
import com.sdm.quasar.newmodelview.implementation.swing.SwingFieldMappingFactory;
import com.sdm.quasar.newmodelview.implementation.swing.SwingFieldMapping;
import com.sdm.quasar.newmodelview.mapping.FieldMapping;
import com.sdm.quasar.newmodelview.mapping.FieldMappingClassNotFoundException;
import com.sdm.quasar.newmodelview.model.ViewModel;
import com.sdm.quasar.newmodelview.model.FieldModel;
import com.sdm.quasar.util.Assertion;

public class MoneyFieldMappingFactory extends SwingFieldMappingFactory {


    public FieldMapping makeFieldMapping(Object field,
                                         ViewModel viewModel,
                                         List viewModelLinks,
                                         FieldModel fieldModel) throws FieldMappingClassNotFoundException {
        if (field instanceof JMoneyField) {
            if (fieldModel.getType() == Money.class) {
                return new SwingFieldMapping((JComponent) field, viewModel, viewModelLinks,  fieldModel) {
                    public Object getFieldValue() {
                        try {
                            return ((JMoneyField) getField()).getValue();
                        } catch (ParseException e) {
                            return null;
                        }
                    }

                    public void setFieldValue(Object value) {
                        ((JMoneyField) getField()).setValue((Money) value);
                    }

                    public boolean isValid() {
                        return true;
                    }

                    public void clearField() {
                        ((JMoneyField) getField()).clear();
                    }
                };
            }
            else {
                Assertion.fail("Wrong type for MoneyField detected");
            }
        }
        return super.makeFieldMapping(field, viewModel, viewModelLinks, fieldModel);
    }
}
