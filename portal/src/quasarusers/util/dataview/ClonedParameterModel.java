package quasarusers.util.dataview;

import com.sdm.quasar.dataview.model.AbstractParameterModel;
import com.sdm.quasar.dataview.model.FilterPart;

/**
 * Erzeugt ein Parametermodell, dass den Wert des ersten Parameters clont.
 *
 *@author Matthias Rademacher
 */
public class ClonedParameterModel extends AbstractParameterModel {
  /** Der Inde der Kopiervorlage. */
  private int mIndex;

  /**
   * Erstellt einen Clone des nullten Parameters.
   *
   * @param  name  der Name des Parametermodells
   * @param  label das Label des Parametermodells
   */
  public ClonedParameterModel(String name,
                              String label) {
    this(name, label, 0);
  }

  /**
   * Erstellt einen Clone des nullten Parameters.
   *
   * @param  name  der Name und das Label des Parametermodells
   */
  public ClonedParameterModel(String name) {
    this(name, name, 0);
  }

  /**
   * Erstellt einen Clone des Parameters <code>index</code>.
   *
   * @param  name  der Name des Parametermodells
   * @param  label das Label des Parametermodells
   * @param  index der Index der Kopiervorlage
   */
  public ClonedParameterModel(String name,
                              String label,
                              int index) {
    super(name,
          label,
          "",
          Object.class /*Dummy-Wert*/);

    mIndex = index;
  }

  public Object processValue(Object value,
                             FilterPart filterPart) {
    return filterPart.getValues()[mIndex];
    // Gibt den Wert des Parameters zurück, der als Kopiervorlage dient
  }
}

