/*
 * Created by IntelliJ IDEA.
 * User: rademach
 * Date: 11.07.2002
 * Time: 10:04:24
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package quasarusers.util.mapping;

import com.sdm.quasar.modelview.implementation.wings.WingsColumnMapping;
import com.sdm.quasar.modelview.mapping.ColumnMapping;
import com.sdm.quasar.modelview.mapping.ColumnMappingClassNotFoundException;
import com.sdm.quasar.modelview.mapping.ColumnMappingFactory;
import com.sdm.quasar.modelview.model.FieldModel;
import com.sdm.quasar.modelview.model.ViewModel;
import com.sdm.quasar.modelview.proxy.ObjectProxy;
import quasarusers.util.Today;

import java.sql.Date;
import java.text.DateFormat;
import java.util.List;

//todo dpk 23/01/2003 -> MR kommentieren

public class HtmlColumnMappingFactory implements ColumnMappingFactory {
  public HtmlColumnMappingFactory() {
  }

  public Class getColumnMappingClass(Class columnClass) throws ColumnMappingClassNotFoundException {
    return HtmlColumnMapping.class;
  }

  public ColumnMapping makeColumnMapping(Object table, Object column,
                                         ViewModel viewModel, List viewModelLinks,
                                         FieldModel fieldModel) throws ColumnMappingClassNotFoundException {
    return new HtmlColumnMapping(table, column, viewModel, viewModelLinks, fieldModel);
  }

  public static class HtmlColumnMapping extends WingsColumnMapping {
    public HtmlColumnMapping(Object table, Object column, ViewModel viewModel, List viewModelLinks, FieldModel fieldModel) {
      super(table, column, viewModel, viewModelLinks, fieldModel);
    }

    public Object getValue(ObjectProxy proxy) {
      Object value = super.getValue(proxy);

      if (value instanceof Date) {
        Date date = (Date) value;

        if (date.before(Today.UNDEF_DATE))
          value = DateFormat.getDateInstance().format(date);
        else
          value = "";
      }

      return value;
    }
  }
}
