package quasarusers.util.report;

import com.sdm.quasar.dataview.server.model.QueryModel;
import com.sdm.quasar.lang.Keywords;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

//todo dpk 23/01/2003 -> MC kommentieren

public class HtmlReportGenerator extends AbstractReportGenerator {
  public Object generateReport(QueryModel queryModel, Keywords arguments) throws Exception {
    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    writeQuery(outStream, queryModel, arguments);
    outStream.close();

    InputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
    String stylesheetName = (String) arguments.getValue("style", "de/sdm/sia/remis/util/htmlreport.xsl");
    InputStream style = getClass().getClassLoader().getResourceAsStream(stylesheetName);
    outStream = new ByteArrayOutputStream();

    processFormatting(inStream, style, outStream);

    inStream.close();
    outStream.close();

    return ((ByteArrayOutputStream) outStream).toByteArray();
  }

  public void processFormatting(InputStream in, InputStream style, OutputStream out) throws Exception {
    TransformerFactory tFactory = TransformerFactory.newInstance();

    Transformer transformer = tFactory.newTransformer(new StreamSource(style));

    transformer.transform(new StreamSource(in), new StreamResult(out));
  }
}
