package reisekosten.swing;

import java.text.DecimalFormat;
import java.text.ParseException;
import javax.swing.JPanel;
import javax.swing.JTextField;

import reisekosten.Money;

public class JMoneyField extends JPanel {
    private JTextField valueField;
    private JTextField currencyField;

    public JMoneyField() {
        valueField = new JTextField();
        valueField.setColumns(10);
        add(valueField);

        currencyField = new JTextField();
        currencyField.setColumns(3);
        add(currencyField);
    }

    public void setValue(Money money) {
        if (money == null)
            clear();
        else {
            valueField.setText(new DecimalFormat("######0.00").format(money.getValue()));
            currencyField.setText(money.getCurrency());
        }
    }

    public Money getValue() throws ParseException {
        return new Money(((Number)new DecimalFormat("######0.00").parseObject(valueField.getText())).doubleValue(),
                         currencyField.getText());
    }

    public void clear() {
        valueField.setText("");
        currencyField.setText("");
    }

    public void setEnabled(boolean enabled) {
        valueField.setEditable(enabled);
        currencyField.setEditable(enabled);
        super.setEnabled(enabled);
    }

}
