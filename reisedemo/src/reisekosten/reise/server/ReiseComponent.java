package reisekosten.reise.server;

import com.sdm.quasar.businessobject.*;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.persistence.PersistenceException;
import com.sdm.quasar.persistence.Pool;
import com.sdm.quasar.persistence.implementation.PersistenceManager;
import com.sdm.quasar.util.registry.Registry;
import quasarusers.portal.businessobject.FunctionViewProperty;
import quasarusers.portal.businessobject.InteractiveCreateFunction;
import quasarusers.portal.businessobject.DataModelProperty;
import quasarusers.util.businessobject.ViewProperty;
import reisekosten.Reise;


public class ReiseComponent extends AbstractBusinessObject {

    public ReiseComponent() {
        super("ReiseComponent", "", "", new Class[] {
            Reise.class
        });
    }

    public ReiseComponent(String objectName, String objectLabel, String objectDocumentation, Class[] classes) {
        super(objectName, objectLabel, objectDocumentation, classes);
    }

    /**
    * Defines whether the access for this business object is controlled by
    * the Authorization Manager (only for optimization purposes)
    */
    public boolean isAccessControlled() {
        return false;
    }

    public void registerProperties(Registry registry) {

        registry.registerProperty(this, new TestdatenAnlegen("ReisenAnlegen", 0, 9));
        registry.registerProperty(this, new TestdatenAnlegen("neueReisenAnlegen", 10, 19));

        registry.registerProperty(this, new FunctionViewProperty("anlegenTestdaten", "Testdaten anlegen", "", "RemisBS.ReiseComponent", TestdatenAnlegen.class, null));

        registry.registerProperty(this, new ViewProperty("queryBearbeiten", "Reise Query bearbeiten", "", new Keywords(
                "view", "com.sdm.quasar.queryeditor.QueryEditorView",
                "serverClass", "de.sdm.sia.remis.portal.businessobject.BusinessObjectQueryEditorServer",
                "objectModel", "reisekosten.Reise"), true));

        DataModelProperty dataModel = new DataModelProperty("reisenDataModel", Reise.class, new String[] {"ReiseQuery1.xml", "ReiseQuery2.xml" });
        registry.registerProperty(this, dataModel);

        registry.registerProperty(this, new ViewProperty("auswaehlenReise", "auswählen", "",
                new Keywords("view", "de.sdm.sia.remis.reise.ReiseDataView", "object", "default") , true));

        registerReiseBearbeiten(registry);
    }

    private void registerReiseBearbeiten(Registry registry) {
        registry.registerProperty(this, new ReiseModel());

        registry.registerProperty(this, new InteractiveCreateFunction(Reise.class, "createReise", "", "",
                  "de.sdm.sia.remis.reise.ReiseView"));

        registry.registerProperty(this, new FunctionViewProperty("anlegenReiseView", "Reise anlegen", "", "RemisBS.ReiseComponent", CreateFunction.class, Reise.class));
    }


    public static class TestdatenAnlegen extends AbstractUseCase {
        private int start;
        private int ende;

        public TestdatenAnlegen(String name, int start, int ende) {
            super(Reise.class, name, "Reisen anlegen", "", TransactionMode.NEW_TOPLEVEL, ReturnMode.DIRECT, true);

            this.start = start;
            this.ende = ende;
        }

        public void run(Keywords arguments) throws BusinessObjectException {
            try {
                Pool pool = PersistenceManager.getPool("reisekosten");

                for (int i=start; i<=ende; i++) {
                    Reise reise = (Reise)pool.make(Reise.class);

                    reise.setMitarbeiter("Mitarbeiter " + i);
                    reise.setBeschreibung("Beschreibung" + i);
                }
            } catch (PersistenceException e) {
                e.printStackTrace();
            }
        }
    }
}
