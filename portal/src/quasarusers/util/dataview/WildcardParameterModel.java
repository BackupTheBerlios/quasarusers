package quasarusers.util.dataview;

import com.sdm.quasar.dataview.model.LiteralParameterModel;
import com.sdm.quasar.util.LocalizedString;

/**
 * Diese Klasse stellt ein {@link com.sdm.quasar.dataview.model.ParameterModel}
 * zur Bearbeitung von Parametern mit Wildcards bereit. Das bedeutet,
 * dass der gesuchte String im gesuchten Feld an beliebiger Stelle enthalten
 * sein muss.
 *
 * @author Matthias Rademacher
 */
public class WildcardParameterModel extends LiteralParameterModel {
  boolean wildcardBeginning;
  boolean wildcardEnding;

  public WildcardParameterModel(String name, String label, String documentation) {
    this(name, label, documentation, true, true);
  }

  public WildcardParameterModel(String name, String label, String documentation,
                                boolean wildcardBeginning, boolean wildcardEnding) {
    super(name, label, documentation, String.class);

    this.wildcardBeginning = wildcardBeginning;
    this.wildcardEnding = wildcardEnding;
  }

  public WildcardParameterModel(String name,
                                LocalizedString label,
                                LocalizedString documentation) {
    this(name, label, documentation, true, true);
  }

  public WildcardParameterModel(String name,
                                LocalizedString label,
                                LocalizedString documentation,
                                boolean wildcardBeginning,
                                boolean wildcardEnding) {
    super(name, label, documentation, String.class);

    this.wildcardBeginning = wildcardBeginning;
    this.wildcardEnding = wildcardEnding;
  }

  public Object processValue(Object value) {
    if (value == null || value.equals(""))
      throw new IllegalArgumentException("Filter is ignored");

    return (wildcardBeginning ? "%" : "") + value + (wildcardEnding ? "%" : "");
  }

  public Object prepareValue(Object value) {
    if (value == null)
      return "";

    String string = (String) value;

    if (wildcardBeginning && string.startsWith("%"))
      string = string.substring(1);

    if (wildcardEnding && string.endsWith("%"))
      string = string.substring(0, string.length() - 1);

    return string;
  }
}
