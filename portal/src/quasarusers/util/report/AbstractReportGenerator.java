package quasarusers.util.report;

import com.sdm.quasar.dataview.model.CombinationPart;
import com.sdm.quasar.dataview.model.FilterPart;
import com.sdm.quasar.dataview.model.ParameterModel;
import com.sdm.quasar.dataview.model.SearchModel;
import com.sdm.quasar.dataview.model.SearchPart;
import com.sdm.quasar.dataview.server.DataIterator;
import com.sdm.quasar.dataview.server.model.FilterModel;
import com.sdm.quasar.dataview.server.model.QueryModel;
import com.sdm.quasar.dataview.server.model.ColumnModel;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.modelview.server.model.AttributeModel;

import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.text.DateFormat;
import java.util.Locale;

//todo dpk 23/01/2003 -> MR kommentieren

public abstract class AbstractReportGenerator implements ReportGenerator {
  protected DataIterator makeDataIterator(QueryModel queryModel, Keywords arguments) {
    String className = queryModel.getDataIteratorClassName();

    try {
      Class iteratorClass = Class.forName(className);
      Constructor constructor = iteratorClass.getConstructor(new Class[]{QueryModel.class, Keywords.class});
      DataIterator dataIterator = (DataIterator) constructor.newInstance(new Object[]{queryModel, arguments});

      return dataIterator;
    } catch (InvocationTargetException e) {
      throw makeCannotInstantiateException(className);
    } catch (InstantiationException e) {
      throw makeCannotInstantiateException(className);
    } catch (IllegalAccessException e) {
      throw makeCannotInstantiateException(className);
    } catch (NoSuchMethodException e) {
      throw makeCannotInstantiateException(className);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("Class " + className + " not found");
    }
  }

  public void writeQuery(OutputStream output, QueryModel queryModel, Keywords arguments) throws Exception {
    PrintStream stream = new PrintStream(output);
    stream.println("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>");
    stream.println();

    stream.print("<query name=\"");
    escapeXmlChars(stream, queryModel.getName());

    stream.print("\" label=\"");
    escapeXmlChars(stream, queryModel.getLabel(Locale.getDefault()));

    stream.print("\" date=\"");
    String today = DateFormat.getDateInstance().format(new Date(System.currentTimeMillis()));
    escapeXmlChars(stream, today);

    stream.println("\" >");

    writeColumns(stream, queryModel, arguments);
    writeSearch(stream, queryModel, arguments);
    writeData(stream, queryModel, arguments);

    stream.println("</query>");
  }

  protected void writeData(PrintStream stream, QueryModel queryModel, Keywords arguments) throws Exception {
    ColumnModel[] columnModels = queryModel.getColumnModels();
    DataIterator dataIterator = makeDataIterator(queryModel, arguments);

    dataIterator.start();

    stream.println("  <data>");
    int count = 0;

    while (dataIterator.hasNext()) {
      count++;
      Object[] data = (Object[]) dataIterator.next();
      stream.print("    <row ");
      for (int i = 0; i < columnModels.length; i++) {
          final ColumnModel columnModel = columnModels[i];

          escapeXmlChars(stream, columnModel.getName());
        stream.print("=\"");

        Object value = columnModel.getValue(data);

        if (value != null) {
          if (value instanceof Date) {
            value = DateFormat.getInstance().format((Date)value);
          }
          escapeXmlChars(stream, value.toString());
        }

        stream.print("\" ");
      }
      stream.println("/>");
    }

    stream.println("  </data>");
    stream.println("<result count=\"" + count + "\" />");
  }

  private void writeColumns(PrintStream stream, QueryModel queryModel, Keywords arguments) {
    ColumnModel[] columnModels = queryModel.getColumnModels();

    stream.println("  <columns>");
    for (int i = 0; i < columnModels.length; i++) {
        final ColumnModel columnModel = columnModels[i];

        stream.print("    <column name=\"");
      escapeXmlChars(stream, columnModel.getName());

      stream.print("\" label=\"");
      escapeXmlChars(stream, columnModel.getLabel(Locale.getDefault()));

      stream.print("\" type=\"");
      escapeXmlChars(stream, columnModel.getType().getName());

      String width = (String) columnModel.getTypeArguments().getValue("printwidth", "2in");
      stream.print("\" width=\"");
      escapeXmlChars(stream, width);

      stream.print("\" visible=\"");
      stream.print(columnModel.isVisible() ? "true" : "false");

      stream.println("\" />");
    }
    stream.println("  </columns>");
    stream.println();
  }

  private void writeSearchPart(PrintStream stream, QueryModel queryModel, SearchPart searchPart) {
    if (searchPart instanceof FilterPart) {
      writeFilterPart(stream, queryModel, (FilterPart) searchPart);
    } else if (searchPart instanceof CombinationPart) {
      writeCombinationPart(stream, queryModel, (CombinationPart) searchPart);
    }
  }

  private void writeCombinationPart(PrintStream stream, QueryModel queryModel, CombinationPart combinationPart) {
    SearchPart[] searchParts = combinationPart.getSearchParts();
    String operator = (combinationPart.getOperator() == CombinationPart.AND) ? "and" : "or";

    stream.println("    <" + operator + ">");

    for (int i = 0; i < searchParts.length; i++)
      writeSearchPart(stream, queryModel, searchParts[i]);

    stream.println("    </" + operator + ">");
  }

  private void writeFilterPart(PrintStream stream, QueryModel queryModel, FilterPart filterPart) {
    FilterModel filterModel = queryModel.getFilterModel(filterPart.getFilterModelName());

    stream.print("    <filter name=\"");
    escapeXmlChars(stream, filterPart.getFilterModelName());

    stream.print("\" label=\"");
    escapeXmlChars(stream, filterModel.getLabel(Locale.getDefault()));

    stream.println("\" >");

    ParameterModel[] parameterModels = filterModel.getParameterModels();

    for (int i = 0; i < parameterModels.length; i++) {
      ParameterModel parameterModel = parameterModels[i];

      stream.print("      <parameter name=\"");
      escapeXmlChars(stream, parameterModel.getName());

      stream.print("\" label=\"");
      escapeXmlChars(stream, parameterModel.getLabel(Locale.getDefault()));

      stream.print("\" value=\"");
      escapeXmlChars(stream, filterPart.getValues()[i].toString());

      stream.println("\" />");
    }
    stream.println("    </filter>");
  }

  private void writeSearch(PrintStream stream, QueryModel queryModel, Keywords arguments) {
    SearchModel searchModel = queryModel.getDefaultSearchModel();

    stream.print("  <search name=\"");
    escapeXmlChars(stream, searchModel.getName());

    stream.print("\" label=\"");
    escapeXmlChars(stream, searchModel.getLabel());

    stream.println("\">");

    writeSearchPart(stream, queryModel, searchModel.getSearchPart());

    stream.println("  </search>");
    stream.println();
  }

  public static void escapeXmlChars(PrintStream stream, String text) {
    for (int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);
      switch (c) {
        case '&':
          stream.print("&amp;");
          break;
        case '<':
          stream.print("&lt;");
          break;
        case '>':
          stream.print("&gt;");
          break;
        case '"':
          stream.print("&quot;");
          break;
        default:
          stream.print(c);
      }
    }
  }

  private RuntimeException makeCannotInstantiateException(String className) {
    return new RuntimeException("Cannot instantiate class " + className);
  }
}
