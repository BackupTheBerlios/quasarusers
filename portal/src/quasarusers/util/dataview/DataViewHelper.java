package quasarusers.util.dataview;

import com.sdm.quasar.dataview.server.model.SimpleQueryModel;
import com.sdm.quasar.dataview.server.model.QueryModel;
import com.sdm.quasar.dataview.model.SearchPart;
import com.sdm.quasar.dataview.model.CombinationPart;
import com.sdm.quasar.dataview.model.FilterPart;
import com.sdm.quasar.dataview.model.SearchModel;
import com.sdm.quasar.dataview.model.SimpleSearchModel;

//todo dpk 23/01/2003 -> MR kommentieren

public class DataViewHelper {
    public static QueryModel copyQueryModel(QueryModel querystandard, String name, String label, String documentation) {
      return new SimpleQueryModel(name, label, documentation,
                                  querystandard.getDataIteratorClassName(),
                                  querystandard.getDataControllerClassName(),
                                  querystandard.getColumnModels(),
                                  querystandard.getFilterModels(),
                                  querystandard.getSearchModels(),
                                  querystandard.getDefaultSearchModel());
//      return new SimpleQueryModel(name, label, documentation,  todo
//                                  querystandard.getDataIteratorClassName(),
//                                  querystandard.getControllerClassName(),
//                                  querystandard.getAttributeModels(true),
//                                  querystandard.getFilterModels(),
//                                  querystandard.getSearchModels(),
//                                  querystandard.getDefaultSearchModel());
    }

    public static SearchPart copySearchPart(SearchPart searchPart) {
      if (searchPart instanceof CombinationPart) {
        SearchPart[] searchParts = ((CombinationPart)searchPart).getSearchParts();
        SearchPart[] newSearchParts = new SearchPart[searchParts.length];

        for (int i = 0; i < searchParts.length; i++)
          newSearchParts[i] = copySearchPart(searchParts[i]);

        return new CombinationPart(newSearchParts, ((CombinationPart)searchPart).getOperator());
      }

      if (searchPart instanceof FilterPart) {
        FilterPart filterPart = (FilterPart)searchPart;

        int length = filterPart.getValues().length;

        Object[] values = new Object[length];
        System.arraycopy(filterPart.getValues(), 0, values, 0, length);

        return new FilterPart(filterPart.getFilterModelName(), values);
      }

      return null;
    }

    public static SearchModel copySearchModel(SearchModel searchModel) {
      return new SimpleSearchModel(searchModel.getName(), searchModel.getLabel(), searchModel.getDocumentation(), copySearchPart(searchModel.getSearchPart()));
    }

}
