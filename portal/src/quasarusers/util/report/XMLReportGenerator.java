package quasarusers.util.report;

import com.sdm.quasar.dataview.server.model.QueryModel;
import com.sdm.quasar.lang.Keywords;

import java.io.ByteArrayOutputStream;

//todo dpk 23/01/2003 -> MC kommentieren

public class XMLReportGenerator extends AbstractReportGenerator {
  public Object generateReport(QueryModel queryModel, Keywords arguments) throws Exception {
    ByteArrayOutputStream outStream = new ByteArrayOutputStream(); //new FileOutputStream("k:/daten/remis/print.pdf");

    writeQuery(outStream, queryModel, arguments);

    outStream.close();

    return outStream.toByteArray();
  }
}
