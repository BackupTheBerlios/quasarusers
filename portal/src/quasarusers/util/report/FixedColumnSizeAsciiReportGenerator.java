/*
 * Created by IntelliJ IDEA.
 * User: rademach
 * Date: 05.07.2002
 * Time: 15:17:00
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package quasarusers.util.report;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.Date;
import java.text.DateFormat;

import com.sdm.quasar.dataview.server.DataIterator;
import com.sdm.quasar.dataview.server.model.QueryModel;
import com.sdm.quasar.dataview.server.model.ColumnModel;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.util.Assertion;

/**
 * Dies ist ein ReportGenerator, der pro Datenzeile eine Zeile ausgibt.
 * Die Spaltenbreiten sind fix und werden über das Keyword
 * "spaltenbreiten" (int[]) gesetzt.
 *
 * @author Matthias Rademacher
 */
public class FixedColumnSizeAsciiReportGenerator extends AbstractReportGenerator {
  private final static String FILL_UP = "                                                                                                         ";

  public Object generateReport(QueryModel queryModel, Keywords arguments) throws Exception {
    ByteArrayOutputStream outStream = new ByteArrayOutputStream();

    writeQuery(outStream, queryModel, arguments);
    outStream.close();

    return outStream.toByteArray();
  }

  public void writeQuery(OutputStream output,
                         QueryModel queryModel,
                         Keywords arguments) throws Exception {
    PrintStream stream = new PrintStream(output);

    writeData(stream, queryModel, arguments);
  }

  protected void writeData(PrintStream stream, QueryModel queryModel, Keywords arguments) throws Exception {
    int[] spaltenbreiten = (int[]) arguments.getValue("spaltenbreiten", null);

    Assertion.checkNotNull(spaltenbreiten, "spaltenbreiten");

    ColumnModel[] columnModels = queryModel.getColumnModels();

    DataIterator dataIterator = makeDataIterator(queryModel, arguments);

    dataIterator.start();

    int count = 0;

    while (dataIterator.hasNext()) {
      count++;
      Object[] data = (Object[]) dataIterator.next();

      String string = "";

      for (int i = 0; i < columnModels.length; i++) {
        ColumnModel columnModel = columnModels[i];

        Object value = filter(columnModel.getValue(data));

        string += formatColumn(value,  spaltenbreiten[i]);
      }

      string = string.trim();

      println(stream, string);

    }
  }

  /**
   * Filtert eine Ausgabespalte. Ersetzt Zeilenvorschübe durch ", ".
   *
   * @param  data  der auszugebende Wert
   * @return der gefilterte Wert
   */
  public Object filter(Object data) {
    if (data instanceof String) {
      String string = (String) data;

      string = string.replace((char) 13, ',');
      string = string.replace((char) 10, ' ');

      return string;
    }

    return data;
  }

  protected String formatColumn(Object value, int spaltenbreite) {
     String result = "";

     if (value != null)
       if (value instanceof Date)
         result = DateFormat.getInstance().format((Date) value);
       else
        result = value.toString();

     if (result.length() >= spaltenbreite)
      return result.substring(0, spaltenbreite);
     else
      return result + FILL_UP.substring(0, spaltenbreite - result.length());
   }

  private void println(PrintStream stream, String string) {
    stream.print(string);
    stream.print((char) 13);
    stream.print((char) 10);
  }
}
