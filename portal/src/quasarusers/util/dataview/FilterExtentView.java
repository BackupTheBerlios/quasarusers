package quasarusers.util.dataview;

import com.sdm.quasar.businessobject.extentview.ExtentView;
import com.sdm.quasar.component.Command;
import com.sdm.quasar.component.CommandAdapter;
import com.sdm.quasar.component.CommandEvent;
import com.sdm.quasar.component.ComponentException;
import com.sdm.quasar.component.ComponentInternalException;
import com.sdm.quasar.component.NoSuchCommandException;
import com.sdm.quasar.dataview.DataViewVisualizer;
import com.sdm.quasar.dataview.FilterView;
import com.sdm.quasar.dataview.model.SearchModel;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.lang.Symbol;
import com.sdm.quasar.util.Assertion;
import com.sdm.quasar.view.UserInterfaceType;
import com.sdm.quasar.view.ViewVisualizer;
import quasarusers.util.dataview.swing.SwingExtentViewVisualizer;

public class FilterExtentView extends ExtentView {
  /** Das Kommando für die DB-Suche.  */
  public static final Symbol PARAMETER_SEARCH = Symbol.forName("parameterSearch");

  public static final Symbol C_PRINT = Symbol.forName("print");
  public static final Symbol C_EXPORTIEREN = Symbol.forName ("exportieren");

  /**  Das Kommando zum Löschen der Suchkriterien. */
  public final static Symbol C_CLEAR_OBJECT = Symbol.forName("clearObject");

  /**
   * Das Kommando zum Sortieren.<p>
   *
   * Erwartete Parameter:<ul>
   * <li>{@link String}<code>"query"</code>: Der name der Query (mit dem
   *                                         gewünschten Sortierkriterium)</ul>
   */
  public final static Symbol C_SORT = Symbol.forName("sort");

  /** Zeigt an, dass das Kommando {@link #PARAMETER_SEARCH} zu aktivieren ist. */
  private boolean allowSearch;

  /**
   * Die View für die Filterkriterien. Existiert nur, wenn
   * {@link #allowSearch} == <code>true</code>.
   */
  private FilterView filterView;

  private SearchModel userSearch;
  private boolean printListenerAdded = false;

  /**
   * Erzeugt eine BenutzerExtentView.
   *
   * @param  arguments die Argumente (siehe
   *                    {@link ExtentView#ExtentView})
   * @throws ComponentException (siehe
   *                    {@link ExtentView#ExtentView})
   *
   */
  public FilterExtentView(Keywords arguments) throws ComponentException {
    super(arguments);
  }

  protected void initialize(Keywords arguments) {
    if (arguments.getValue("allowFilter", null) == null)
      arguments.setValue("allowFilter", Boolean.FALSE);

    allowSearch = ((Boolean) arguments.getValue("allowSearch", Boolean.TRUE)).booleanValue();

    super.initialize(arguments);

      if (getViewManager().getUserInterfaceType() == UserInterfaceType.SWING)
          allowSearch = false;
  }

  public void buildStructure(Keywords arguments) throws ComponentException {
    if (allowSearch)
      addChildComponent(filterView = (FilterView)getViewManager().makeView(FilterView.class,new Keywords()));
  }

  public void bindObject(Object object) throws ComponentException {
    bindObject(object, new Keywords());
  }

  public void unbindObject() {
    unbindObject(new Keywords());
  }

  public void bindChildObjects(Object object, Keywords arguments) throws ComponentException {
    if (allowSearch) {
      Keywords filterArguments = new Keywords("searchModels", getSearchModels(),
                                              "filterModels", getFilterModels());

      if (userSearch == null) {
        userSearch = DataViewHelper.copySearchModel(getSearchModels()[0]);
      }

      filterView.updateState(filterArguments);
      filterView.bindObject(userSearch, filterArguments);
    }

    if (!printListenerAdded)
    try {
      lookupCommand(C_PRINT).addCommandListener(new CommandAdapter() {
        public void commandPerforming(CommandEvent event) {
          event.getArguments().addValue("search", getFilterView().getObject());
        }
      });
    } catch (NoSuchCommandException e) {
      printListenerAdded = true;

      System.out.println("no print command");
    }
  }

  public ViewVisualizer makeViewVisualizer(Keywords arguments) {
    if (getViewManager().getUserInterfaceType()== UserInterfaceType.SWING)
        return new SwingExtentViewVisualizer(arguments);

    if (arguments.getValue("viewVisualizer") != null)
      return (ViewVisualizer) arguments.getValue("viewVisualizer");
    else
      return super.makeViewVisualizer(arguments);
  }

  public String computeVisualizerClassName(Keywords arguments) {
    UserInterfaceType userInterfaceType = getViewManager().getUserInterfaceType();
    String className = null;

    if (userInterfaceType == UserInterfaceType.WINGS)
      className = (String) arguments.getValue("wingsVisualizer");

    if (userInterfaceType == UserInterfaceType.SWING)
      className = (String) arguments.getValue("swingVisualizer");

    if (className == null)
      className = super.computeVisualizerClassName(arguments);

    if (className == null)
      Assertion.fail("User interface type " + userInterfaceType + " not supported by " + this);

    return className;
  }

  /**
   * Führt die Suche durch, wobei die Argumente via Visualizer aus dem Panel
   * geholt werden.
   *
   * @param  arguments          Die Argumente des Kommandos (nicht genutzt)
   * @throws ComponentException falls das SearchModel nicht gebaut werden kann
   */
  public void runParameterSearch(Keywords arguments) throws ComponentException {
    Assertion.checkNotNull(filterView, "filterView");

    try {
      filterView.updateViewObject(arguments);
    } catch (Exception e) {
      throw new ComponentInternalException(e, "Can not build search model");
    }
    SearchModel searchModel = (SearchModel) filterView.getObject();

    arguments.setValue("search", searchModel);

    Object[] searchResult = (Object[]) performCommand(QUERY, arguments);

    if (searchResult != null) {
      unbindObject();

      updateState((Keywords)searchResult[1]);

      ((DataViewVisualizer) getViewVisualizer()).updateSearchTool();

      bindObject(searchResult[0], arguments);
    }
  }

  public void buildCommands() {
    super.buildCommands();

    addCommand(new Command(C_CLEAR_OBJECT, true) {
      public Object perform(Keywords arguments) throws ComponentException {
        clearObject(arguments);

        return null;
      }
    });

    addCommand(new Command(C_SORT, true) {
      public Object perform(Keywords arguments) throws ComponentException {
        arguments.removeValue("index");
        arguments.addValue("query", arguments.getValue("column"));
        System.out.println("Execute Query " + arguments.getValue("column"));

        return FilterExtentView.this.performCommand(PARAMETER_SEARCH, arguments);
      }
    });

    addCommand(new Command(PARAMETER_SEARCH, true) {
      public Object perform(Keywords arguments) throws ComponentException {
        runParameterSearch(arguments);

        return null;
      }
    });

    useViewServerCommand(C_PRINT);

    try {
      lookupCommand(EDIT).addCommandListener(new CommandAdapter() {
        public void commandPerformed(CommandEvent event) {
          FilterExtentView.this.showView();
        }
      });
    } catch (NoSuchCommandException e) {
      e.printStackTrace();
    }

    addExportListener(C_EXPORTIEREN);
  }

  public void clearObject(Keywords arguments) throws ComponentException {
//    DataViewModel viewModel = (DataViewModel) getViewModel();
//    userSearch = DataViewHelper.copySearchModel(viewModel.getSearchModels()[0]);
    userSearch = DataViewHelper.copySearchModel(getSearchModels()[0]); // todo
    getFilterView().bindObject(userSearch, arguments);
  }

  public void updateCommandSetup() {
    super.updateCommandSetup();

    try {
      setCommandEnabled(EDIT, true);
      setCommandEnabled(PARAMETER_SEARCH, allowSearch);
      setCommandEnabled(C_PRINT, true);
      setCommandEnabled(C_CLEAR_OBJECT, true);
      setCommandEnabled(C_SORT, true);
    } catch (NoSuchCommandException e) {
      e.printStackTrace();
      Assertion.fail("Unknown command");
    }
  }

  protected void addExportListener(Symbol exportCommand) {
    try {
      lookupCommand(exportCommand).addCommandListener(new CommandAdapter () {
          public void commandPerforming (CommandEvent event) {
              Keywords arguments = event.getArguments ();
              try {
                  // Durch das update View-Object werden die Werte aus den widgets verwendet, ansonsten
                  // die Werte der letzten Suche
                  getFilterView().updateViewObject (arguments);
                  arguments.setValue ("search", getFilterView ().getObject ());
              } catch (Exception e) {
                  // ignore, use default search instead
              }
          }
      });
    } catch (NoSuchCommandException e) {
      // can be safely ignored
    }
  }

  public FilterView getFilterView() {
    return filterView;
  }
}
