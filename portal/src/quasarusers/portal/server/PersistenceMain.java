package quasarusers.portal.server;

import reisekosten.Reise;
import reisekosten.Kostenpunkt;
import reisekosten.Money;
import com.sdm.quasar.session.SessionType;
import com.sdm.quasar.persistence.query.QueryResult;
import com.sdm.quasar.persistence.query.SingleValuedResultExpression;
import com.sdm.quasar.persistence.query.ResultExpression;


public class PersistenceMain {
    public static void main(String[] args) throws Exception {
        QuasarServer quasarServer = new QuasarServer();

        quasarServer.getSessionManager().open("system", "test", SessionType.BACKGROUND);

        quasarServer.getTransactionManager().begin();

        Reise reise = (Reise)quasarServer.getPool().make(Reise.class);
        reise.setBeschreibung("neu");
        reise.setMitarbeiter("test");

        Kostenpunkt kostenPunkt = (Kostenpunkt)quasarServer.getPool().make(Kostenpunkt.class);
        kostenPunkt.setReise(reise);
        kostenPunkt.setBetrag(new Money(100, "EUR"));

        oqlQuery(quasarServer);
        standardQuery(quasarServer);

        quasarServer.getTransactionManager().commit();
    }

    private static void oqlQuery(QuasarServer quasarServer) throws Exception {
        printResult(quasarServer.getQueryManager().queryAll(quasarServer.getPool(),
                "select x.beschreibung from extent(\"reisekosten.Reise\") x", new Object[]{}));

        printResult(quasarServer.getQueryManager().queryAll(quasarServer.getPool(),
                "select x.reise.beschreibung, x.oid from extent(\"reisekosten.Kostenpunkt\") x", new Object[]{}));
    }

    private static void standardQuery(QuasarServer quasarServer) throws Exception {
        SingleValuedResultExpression reiseExpr = quasarServer.getQueryManager().get(Reise.class);

        QueryResult queryResult = quasarServer.getQueryManager().queryAll(quasarServer.getPool(),
                        new ResultExpression[] {
                            reiseExpr.get("beschreibung"),
                            reiseExpr.get("mitarbeiter")
                        }, null, new Object[]{});

        printResult(queryResult);
    }

    public static void printResult(QueryResult result) throws Exception {
        System.out.println("==RESULT==");
        int i = 0;
        while (result.hasNextResult()) {
            System.out.print("### " + i + ".) ");

            Object[] results = (Object[]) result.nextResult();

            for ( int j = 0; j < results.length; j++) {
                if ( j > 0 )
                    System.out.print(", ");

                System.out.print(results[j]);
            } // for

            System.out.println("");
        } // while
    }
}
