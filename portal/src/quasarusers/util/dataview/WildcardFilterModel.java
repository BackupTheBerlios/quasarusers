/*
 * Created by IntelliJ IDEA.
 * User: rademach
 * Date: 21.10.2002
 * Time: 17:35:36
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package quasarusers.util.dataview;

import com.sdm.quasar.dataview.model.ParameterModel;
import com.sdm.quasar.dataview.server.model.FilterModel;
import com.sdm.quasar.util.Arrays;

/**
 * Dies ist eine FilterModell für Teile von Stringattributen. Man
 * kann wählen, ob vorn und/oder hinten "%" angefügt wird. <p>
 *
 * Da in REMIS das Label im Dialog nie ausgegeben wird, wird
 * hier einfach für das Label der Name eingesetz.
 *
 * @author Matthias Rademacher
 */
public class WildcardFilterModel extends FilterModel {
  /** Überdeckt das Field der Vaterklasse! */
  ParameterModel[] parameterModels;

  /**
   * Erzeugt ein <code>WildcardFilterModel</code> mit
   * Wildcard vorn und hinten.
   *
   * @param name  der Name und das Label des FilterModels
   */
  public WildcardFilterModel(String name) {
    this(name, true, true);
  }

  /**
   * Erzeugt ein <code>WildcardFilterModel</code>.
   *
   * @param  name              der Name und das Label des FilterModels
   * @param  wildcardBeginning die Angabe, ob vorb ein Wildcard eingesrtzt
   *                            werden soll
   * @param  wildcardEnding die Angabe, ob hinten ein Wildcard eingesrtzt
   *                            werden soll
   */
  public WildcardFilterModel(String name,
                             boolean wildcardBeginning,
                             boolean wildcardEnding) {
    super(name,
          name,
          "",
          new ParameterModel[]{new WildcardParameterModel(name,
                                                          name,
                                                          "",
                                                          wildcardBeginning,
                                                          wildcardEnding)});
    this.parameterModels = super.getParameterModels();
  }

  /**
   * Returns the ParameterModels of the model.
   *
   * @return	the ParameterModels of the model
   */
  public ParameterModel[] getParameterModels() {
    return parameterModels;
  }

  /**
   * Fügt ein {@link ParameterModel} hinzu.
   *
   * @param	 parameterModel das zusätzliche {@link ParameterModel}
   */
  public void addParameterModel(ParameterModel parameterModel) {
    parameterModels = (ParameterModel[]) Arrays.add(ParameterModel.class,
                                                    parameterModels,
                                                    parameterModel);
  }
}
