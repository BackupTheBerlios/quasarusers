package quasarusers.util.modelview.wings;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.event.ListDataListener;

import com.sdm.quasar.dataview.DataView;
import com.sdm.quasar.dataview.DataViewVisualizer;
import com.sdm.quasar.dataview.implementation.wings.WingsDataViewVisualizer;
import com.sdm.quasar.dataview.server.model.ColumnModel;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.modelview.TableView;
import com.sdm.quasar.modelview.mapping.ColumnMapping;
import com.sdm.quasar.modelview.mapping.ColumnMappingClassNotFoundException;
import com.sdm.quasar.util.Assertion;
import com.sdm.quasar.util.LocalizedStrings;
import com.sdm.quasar.view.ViewManager;
import com.sdm.quasar.view.ViewTransaction;
import com.sdm.quasar.view.View;
import com.sdm.quasar.view.implementation.wings.CommandTrigger;
import com.sdm.quasar.view.implementation.wings.WingsViewVisualizer;
import org.wings.SButton;
import org.wings.SComboBox;
import org.wings.SComponent;
import org.wings.SIcon;
import org.wings.SImageIcon;
import org.wings.STable;

/**
 * @version 1.2
 */

//todo dpk 23/01/2003 -> MC kommentieren

public class WingsComboboxViewVisualizer extends WingsViewVisualizer implements DataViewVisualizer {
    protected static final LocalizedStrings DATA_VIEW_VISUALIZER_STRINGS =
        new LocalizedStrings("com.sdm.quasar.dataview.implementation.swing.SwingViewVisualizerStrings");

    private static final String[] emptyColumnNames = new String[0];
    private static final SIcon SEARCH_ICON = getIcon("search");
    private static final SIcon ACTIVE_SEARCH_ICON = getIcon("activeSearch");
    private SComboBox queryChooser;
    private CommandTrigger searchAction;
    private SButton searchButton;
    private SComboBox comboBox;
    private com.sdm.quasar.dataview.mapping.ColumnMappingFactory columnMappingFactory;

    public WingsComboboxViewVisualizer(Keywords arguments) {
        super(arguments);
    }

    protected void initialize(Keywords arguments) {
        if (arguments.getValue("columnNames") == null)
            arguments.addValue("columnNames", emptyColumnNames);

        columnMappingFactory = (com.sdm.quasar.dataview.mapping.ColumnMappingFactory)arguments.getValue("columnMappingFactory", columnMappingFactory);

        if (columnMappingFactory == null)
            makeColumnMappingFactory(arguments);

        super.initialize(arguments);
    }

    private DataView getDataView() {
        return (DataView)getView();
    }

    private List getViewObject() {
        return ((List)getView().getObject());
    }

  /**
   * This special <code>TableModel</code> is returned by the default implementation of <code>makeTableModel</code>.
   * It stores all proxies displayed by the table in an array.
   */
  protected class DataComboBoxModel implements ComboBoxModel {
      Object selected;

      public void setSelectedItem(Object anItem) {
          selected = anItem;
      }

      public Object getSelectedItem() {
          return selected;
      }

      public int getSize() {
          if (getViewObject() == null)
              return 0;

          return getViewObject().size();
      }

      public Object getElementAt(int index) {
          StringBuffer stringBuffer = new StringBuffer();

          final int columnCount = getDataView().getColumnModels().length;

          for (int i=0; i<columnCount; i++) {
              if (getDataView().getColumnModels()[i].isVisible()) {
                  stringBuffer.append(getValueAt(index, i));

                  if (i<columnCount-1) {
                      stringBuffer.append(", ");
                  }
              }
          }

          return stringBuffer.toString();
      }

      public void addListDataListener(ListDataListener l) {
      }

      public void removeListDataListener(ListDataListener l) {
      }

      /**
       * Returns the value at the given row and column.
       *
       * @param   row     the index of the row
       * @param   column  the index of the column
       * @return  the requested value
       */
      public Object getValueAt(int row, int column) {
          try {
              Object object = getViewObject().get(row);

              if (object == null)
                return "";

              return getDataView().getColumnModels()[column].getValue(object);
          }
          catch (Exception e) {
              e.printStackTrace();  //To change body of catch statement use Options | File Templates.

              return null;
          }
      }
  }

    private static final SIcon getIcon(String iconName) {
        URL url = WingsDataViewVisualizer.class.getResource(iconName + "_SMALL.png");

        if (url == null)
            return null;
        else
            return new SImageIcon(new ImageIcon(url));
    }

    private static final String getText(String key, Locale locale) {
        return DATA_VIEW_VISUALIZER_STRINGS.getString(key, locale);
    }

    public void updateQueryChooser() {
        if (queryChooser == null)
            return;

        DataView view = (DataView)getView();
        DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel)queryChooser.getModel();
        String[] queryNames = view.getQueryNames();
        String[] queryLabels = view.getQueryLabels();
        String selectedQueryName = view.getSelectedQueryName();
        int qLength = queryNames.length;

        comboBoxModel.removeAllElements();

        for (int i = 0; i < qLength; i++) {
            comboBoxModel.addElement(queryLabels[i]);

            if (queryNames[i].equals(selectedQueryName))
                comboBoxModel.setSelectedItem(queryLabels[i]);
        }
    }

    public void updateSearchTool() {
        if (searchButton == null)
            return;

        String searchName = ((DataView)getView()).getSelectedSearchModelName();

        if ((searchName == null) || searchName.equalsIgnoreCase("selectAll"))
            searchButton.setIcon(SEARCH_ICON);
        else
            searchButton.setIcon(ACTIVE_SEARCH_ICON);
    }

    public void unblockViewEvents() {
//        super.unblockViewEvents();  todo

        if (queryChooser != null)
            queryChooser.setEnabled(true);
    }

    public void blockViewEvents() {
//        super.blockViewEvents();        todo

        if (queryChooser != null)
            queryChooser.setEnabled(false);
    }

    /**
     * todo: The chooser in the toolbar must be activated for the currently focused DataView only.
     */
    public List buildLocalControls() {
        DataView view = (DataView)getView();
        List controls = super.buildLocalControls();

        Iterator userInterfaceElements = getUserInterfaceElements().iterator();

        while (userInterfaceElements.hasNext()) {
            SComponent component = (SComponent)userInterfaceElements.next();
            String name = component.getName();

            if (name != null) {
                if (name.equalsIgnoreCase("searchCommand")) {
                    searchButton = (SButton)component;
                    searchAction = new CommandTrigger(DataView.RUN_SEARCH, false);
                    controls.add(component);
                }
                else if (name.equalsIgnoreCase("queryCommand")) {
                    ((SButton)component).addActionListener(new ActionListener() {
                                                       public void actionPerformed(ActionEvent event) {
                                                           int index = queryChooser.getSelectedIndex();

                                                           if (index >= 0)
                                                               querySelected(index);
                                                       }
                                                   });
                    controls.add(component);
                }
                else if (name.equalsIgnoreCase("queryChooser")) {
                    queryChooser = (SComboBox)component;

                    controls.add(component);
                }
            }
        }

        return controls;
    }

    protected void querySelected(final int index) {
        final ViewManager viewManager = getView().getViewManager();
        final ViewTransaction transaction = viewManager.beginTransaction();

        viewManager.getThreadPoolAdapter().start(
            new Runnable() {
                public void run() {
                    try {
                        getView().performCommand(DataView.RUN_QUERY, new Keywords("viewTransaction", transaction, "index", new Integer(index)));
                    }
                    catch (Exception e) {
                        e.printStackTrace();  //To change body of catch statement use Options | File Templates.
                    }
                }
            });

        suspendReload();
    }

  public void bindViewObject(Object object, Keywords arguments) {
      getTableUserInterfaceElement();

      super.bindViewObject(object, arguments);
  }

    protected void initializeComboBox(final SComboBox box) {
        box.setModel(new DataComboBoxModel());
    }

    public Object getTableUserInterfaceElement() {
        if (comboBox == null) {
            String name = getView().getName();
            String nameTable = name + "Box";
            Iterator elements = getUserInterfaceElements().iterator();

            while (elements.hasNext()) {
                Object element = elements.next();

                if (element instanceof SComboBox) {
                    SComboBox tableElement = (SComboBox)element;
                    String elementName = tableElement.getName();

                    if (name.equalsIgnoreCase(elementName) || nameTable.equalsIgnoreCase(elementName)) {
                        comboBox = tableElement;

                        initializeComboBox(comboBox);

                        break;
                    }
                }
            }

            if (Assertion.CHECK && (comboBox == null))
                Assertion.fail("Cannot find STable with name " + name);
        }

        return comboBox;
    }

  public com.sdm.quasar.dataview.mapping.ColumnMappingFactory makeColumnMappingFactory(Keywords arguments) {
      return null;
  }

}


