package quasarusers.util.report;

import com.sdm.quasar.dataview.server.model.QueryModel;
import com.sdm.quasar.lang.Keywords;
import org.apache.fop.apps.Driver;
import org.xml.sax.InputSource;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

//todo dpk 23/01/2003 -> MC kommentieren

public class PdfReportGenerator extends AbstractReportGenerator {
    public Object generateReport(QueryModel queryModel, Keywords arguments) throws Exception {
        ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();

        generateReport(queryModel, arguments, pdfStream);

        pdfStream.close();

        return pdfStream.toByteArray();
    }

    public void generateReport(QueryModel queryModel, Keywords arguments, OutputStream pdfStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        writeQuery(outStream, queryModel, arguments);
        outStream.close();

        FileOutputStream dumper = new FileOutputStream("c:/print.xml");
        dumper.write(outStream.toByteArray());
        dumper.close();

        InputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
        InputStream style = getClass().getClassLoader().getResourceAsStream("de/sdm/sia/remis/util/report.xsl");
        ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream();

        processFormatting(inStream, style, fileOutputStream);

        fileOutputStream.close();
        inStream.close();

        inStream = new ByteArrayInputStream(fileOutputStream.toByteArray());

        processPDF(inStream, pdfStream);

        dumper = new FileOutputStream("c:/fo.xml");
        dumper.write(fileOutputStream.toByteArray());
        dumper.close();

        inStream.close();
    }

    public void processFormatting(InputStream in, InputStream style, OutputStream out) throws Exception {
        TransformerFactory tFactory = TransformerFactory.newInstance();

        Transformer transformer = tFactory.newTransformer(new StreamSource(style));

        transformer.transform(new StreamSource(in), new StreamResult(out));
    }

    public void processPDF(InputStream in, OutputStream out) throws Exception {
        Driver driver = new Driver();

        driver.setRenderer(Driver.RENDER_PDF);
        driver.setInputSource(new InputSource(in));
        driver.setOutputStream(out);

        driver.run();
    }
}
