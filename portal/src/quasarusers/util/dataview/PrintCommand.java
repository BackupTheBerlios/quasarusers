/*
 * Created by IntelliJ IDEA.
 * User: rademach
 * Date: 24.06.2002
 * Time: 16:01:18
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package quasarusers.util.dataview;

import com.sdm.quasar.component.Command;
import com.sdm.quasar.component.ComponentException;
import com.sdm.quasar.component.ComponentInternalException;
import com.sdm.quasar.dataview.model.SearchModel;
import com.sdm.quasar.dataview.model.SimpleSearchModel;
import com.sdm.quasar.dataview.server.model.QueryModel;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.lang.Symbol;
import com.sdm.quasar.modelview.server.ModelViewServerTransactionException;
import com.sdm.quasar.modelview.server.model.OperationType;
import quasarusers.util.dataview.FilterExtentViewServer;
import quasarusers.util.report.ReportGenerator;

/**
 * Serverseitiges Kommando.
 * Erstellt und öffnet die Druckansicht für für einen
 * {@link FilterExtentViewServer}.
 *
 * @author Matthias Rademacher
 */
public class PrintCommand extends Command {
  private final FilterExtentViewServer mViewServer;

  public PrintCommand(Symbol name, FilterExtentViewServer viewServer) {
    super(name);

    mViewServer = viewServer;
  }

  public Object perform(Keywords arguments) throws ComponentException {
    return print(arguments);
  }

  /**
   * Erstellt und öffnet eine Druckansicht.
   *
   * @param  arguments die Argumente. Erwartet: <ul>
   *                   <li>{@link String} "reportGenerator": Der voll-qualifizierte
   *                                                         Name der Generatorklasse</ul>
   * @throws ComponentInternalException           falls der Report nicht erzeugt
   *                                              werden konnt
   * @throws ModelViewServerTransactionException  falls die Transaktion nicht
   *                                              beendet werden konnte
   */
  private Object print(Keywords arguments)
          throws ComponentInternalException, ModelViewServerTransactionException {
    try {
      mViewServer.beginTransaction(OperationType.LOAD);

      Class generatorClass = Class.forName((String) arguments.getValue("reportGenerator"));

      QueryModel queryModel = (QueryModel) mViewServer.getQueryModel();

      queryModel.setDefaultSearchModel((SearchModel) arguments.getValue("search", SimpleSearchModel.SELECT_ALL));

      ReportGenerator generator = (ReportGenerator) generatorClass.newInstance();

      return generator.generateReport(queryModel, arguments);
    } catch (Exception e) {
      throw new ComponentInternalException(e, "Report generation failed");
    } finally {
      mViewServer.endTransaction(false);
    }
  }

}
