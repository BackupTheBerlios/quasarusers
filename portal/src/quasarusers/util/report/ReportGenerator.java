package quasarusers.util.report;

import com.sdm.quasar.dataview.server.model.QueryModel;
import com.sdm.quasar.lang.Keywords;

//todo dpk 23/01/2003 -> MC kommentieren

public interface ReportGenerator {
  public Object generateReport(QueryModel queryModel, Keywords arguments) throws Exception;
}
