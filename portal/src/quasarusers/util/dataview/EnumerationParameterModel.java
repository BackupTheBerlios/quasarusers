package quasarusers.util.dataview;


/**
 * Workaround um Jeff-Bug
 * in {@link com.sdm.quasar.dataview.model.EnumerationParameterModel}
 *
 * @author      Matthias Rademacher
 */
public class EnumerationParameterModel extends com.sdm.quasar.dataview.model.EnumerationParameterModel {
  public EnumerationParameterModel(String name, String label, String documentation, String[] names, Object[] values) {
    super(name, label, documentation, names, values);
  }

  public Object processValue(Object value) {
    String[] parameterNames = getParameterNames(); // Change gegenüber Jeff-Klasse
    Object[] parameterValues = getParameterValues(); // Change gegenüber Jeff-Klasse

    for (int i = 0; i < parameterNames.length; i++) {
      if (parameterNames[i].equals(value))
        return parameterValues[i];
    }

    return null;
  }

  public Object prepareValue(Object value) {
    String[] parameterNames = getParameterNames(); // Change gegenüber Jeff-Klasse
    Object[] parameterValues = getParameterValues(); // Change gegenüber Jeff-Klasse

    for (int i = 0; i < parameterValues.length; i++) {
      if (value == null) {
        if (parameterValues[i] == null)
          return parameterNames[i];
      } else {
        if (value.equals(parameterValues[i]))
          return parameterNames[i];
      }
    }

    return null;
  }
}
