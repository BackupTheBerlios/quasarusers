package quasarusers.util.dataview;

/**
 * Created by IntelliJ IDEA.
 * User: schmickl
 * Date: Jan 25, 2003
 * Time: 12:28:48 AM
 * To change this template use Options | File Templates.
 */
public class IgnorableEnumerationParameterModel extends EnumerationParameterModel {
    public IgnorableEnumerationParameterModel(String name, String label, String documentation, String[] names, Object[] values) {
        super(name, label, documentation, names, values);
    }

    public Object processValue(Object value) {
      value = super.processValue(value);

      if ((value == null) || value.equals(""))
        throw new IllegalArgumentException("Ignore");

      return value;
    }
}
