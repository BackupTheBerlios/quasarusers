/*
 * Created by IntelliJ IDEA.
 * User: rademach
 * Date: 21.10.2002
 * Time: 17:26:06
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package quasarusers.util.dataview;

import com.sdm.quasar.dataview.server.model.FilterModel;
import com.sdm.quasar.dataview.model.ParameterModel;

/**
 * Dies ist eine FilterModell für Datumsangaben, die als String
 * eingegegeben werden.<p>
 *
 * Da in REMIS das Label im Dialog nie ausgegeben wird, wird
 * hier einfach für das Label der Name eingesetz.
 *
 * @author Matthias Rademacher
 */
public class DateFilterModel extends FilterModel {
  public DateFilterModel(String name) {
    super(name, name, "", new ParameterModel[]{
                                            new DateParameterModel(name,
                                                                   name,
                                                                   "",
                                                                   String.class)});
  }
}
