package quasarusers.util.dataview;

import com.sdm.quasar.component.ComponentException;
import com.sdm.quasar.component.CommandController;
import com.sdm.quasar.dataview.DataView;
import com.sdm.quasar.dataview.model.FilterPart;
import com.sdm.quasar.dataview.model.SearchModel;
import com.sdm.quasar.dataview.model.SearchPart;
import com.sdm.quasar.dataview.model.SimpleSearchModel;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.modelview.ObjectView;
import com.sdm.quasar.modelview.model.FieldModel;
import com.sdm.quasar.modelview.model.ViewModel;
import com.sdm.quasar.modelview.proxy.ObjectProxy;
import com.sdm.quasar.view.AbstractView;
import com.sdm.quasar.view.View;
import com.sdm.quasar.view.ViewVisualizer;
import com.sdm.quasar.view.implementation.wings.ViewPanel;
import com.sdm.quasar.view.implementation.wings.WingsViewVisualizer;
import org.wings.SComponent;

/**
 * Dies ist eine generische View zur Einbettung einer DataView in eine
 * View.
 *
 * @author Marco Schmickler
 */
public class EmbeddedDataView extends AbstractView {
    private DataView dataView;
    private FieldModel[] linkedFields;
    private String linkedFilter;
    private String[] linkedFieldNames;
    private String entityType;
    private String businessObject;
    private String serverClass;
    private String viewPanel;
    private String viewPanelTemplate;

    public EmbeddedDataView(Keywords arguments) throws ComponentException {
        super(arguments);
    }

    public DataView getDataView() {
        return dataView;
    }

    private class EmbeddedViewVisualizer extends WingsViewVisualizer {
        public EmbeddedViewVisualizer(Keywords arguments) {
            super(arguments);
        }

        protected ViewVisualizer provideChildVisualizer(View child, Keywords arguments) {
            ViewVisualizer viewVisualizer = null;
            Keywords childArguments = (Keywords)arguments.clone();

            try {
              if (viewPanel != null) {
                Class aClass = Class.forName(viewPanel);
                ViewPanel panel = (ViewPanel) aClass.getConstructor(new Class[]{View.class}).newInstance(new Object[]{child});

                childArguments.addValue("visualRepresentation", panel);
              }

              if (viewPanelTemplate != null) {
                childArguments.addValue("viewPanelTemplate", viewPanelTemplate);
              }

              viewVisualizer = super.provideChildVisualizer(child, childArguments);

              ((ViewPanel) getVisualRepresentation()).add((SComponent) viewVisualizer.getVisualRepresentation());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return viewVisualizer;
        }

        public Object makeVisualRepresentation() {
            return new ViewPanel();
        }
    }

    protected void initialize(Keywords arguments) {
        super.initialize(arguments);

        linkedFieldNames = (String[]) arguments.getValue("links");
        linkedFilter = (String) arguments.getValue("filter");
        entityType = (String) arguments.getValue("entityType");
        businessObject = (String) arguments.getValue("businessObject");
        viewPanel = (String) arguments.getValue("viewPanel");
        viewPanelTemplate = (String) arguments.getValue("viewPanelTemplate", "null");
    }

    public void buildStructure(Keywords arguments) throws ComponentException {
        String viewName = (String) arguments.getValue("viewClass");
        arguments = (Keywords)arguments.clone();
        arguments.setValue("object", null);

        dataView = (DataView) getViewManager().makeView(viewName, arguments);

        addChildComponent(dataView);
    }

    public ViewVisualizer makeViewVisualizer(Keywords arguments) {
        return new EmbeddedViewVisualizer(arguments);
    }

    public FieldModel[] getLinkedFields() {
        if (linkedFields == null) {
            ViewModel viewModel = ((ObjectView) getParentComponent()).getViewModel();
            linkedFields = new FieldModel[linkedFieldNames.length];

            for (int i = 0; i < linkedFieldNames.length; i++)
                linkedFields[i] = viewModel.getFieldModel(linkedFieldNames[i]);
        }

        return linkedFields;
    }

    private CommandController getParentServer() {
        View parent = (View)getParentComponent();
        CommandController server = null;

        while (parent != null) {
            server = parent.getViewServer();

            if (server != null)
                break;

            parent = (View)parent.getParentComponent();
        }

        return server;
    }

    public void bindObject(Object object, Keywords arguments) throws ComponentException {
        if (object instanceof ObjectProxy) {
            FieldModel[] linkedFields = getLinkedFields();

            Object[] values = new Object[linkedFields.length];

            for (int i = 0; i < linkedFields.length; i++)
                values[i] = linkedFields[i].getValue((ObjectProxy) object);

            SearchPart constraint = new FilterPart(linkedFilter, values);

            if (dataView.getViewServer() == null) {
                arguments = new Keywords(new Object[] {
                        "entityType", entityType,
                        "businessObject", businessObject,
                        "name", dataView.getName(),
                        "parent", getParentServer(),
                        "serverClass", dataView.getViewServerClass(),
                        "constraint", constraint,
                        "search", computeSearchModel(object, arguments) });

                dataView.updateState(getViewManager().startViewServer(dataView, arguments));
            } else {
                arguments.addValue("constraint", constraint);
                arguments.addValue("search", computeSearchModel(object, arguments));

                dataView.performCommand(DataView.RUN_QUERY, arguments);
                System.out.println("query performed");
            }
        }
    }

    public SearchModel computeSearchModel(Object object, Keywords arguments) {
        return SimpleSearchModel.SELECT_ALL;
    }

    protected Keywords computeViewServerArguments(Keywords arguments) {
        return arguments;
    }
}
