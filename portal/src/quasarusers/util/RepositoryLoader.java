/*
* Created by IntelliJ IDEA.
* User: pannier
* Date: Mar 4, 2002
* Time: 4:21:18 PM
* To change template for new class use
* Code Style | Class Templates options (Tools | IDE Options).
*/
package quasarusers.util;

import com.sdm.quasar.persistence.implementation.ConnectionManager;
import com.sdm.quasarx.configuration.Configuration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Diese Klasse l‰dt die Modellbeschreibungen aus XML-Dateien.
 * Auﬂerdem enth‰lt sie die Funktionalit‰t zum Erzeugen von OIDs.
 *
 * @author Marco Schmickler
 */
public class RepositoryLoader {
    // static

    static RepositoryLoader repositoryLoader = null;

    // instance data

    private PreparedStatement mNextOIDStatement;

    // static

    public static Connection getConnection() throws Exception {
      return ((ConnectionManager)Configuration.getConfiguration().getSingleton(ConnectionManager.class)).getConnection();
      //return DriverManager.getConnection(repositoryLoader.mDatabase, repositoryLoader.mUser, repositoryLoader.mPassword);
    }

    public static RepositoryLoader getRepositoryLoader() {
        if (repositoryLoader == null)
          repositoryLoader = new RepositoryLoader();
        return repositoryLoader;
    }

    // constructor
    public RepositoryLoader() {
        repositoryLoader = this;

        try {
            mNextOIDStatement = getConnection().prepareStatement("select OIDS.NEXTVAL from dual");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // initialize repository


    public Long getNextOID() throws SQLException {
        ResultSet result = mNextOIDStatement.executeQuery();

        result.next();
        long oid = result.getLong(1);

        result.close();

System.out.println("next oid " + oid);

        return new Long(oid);
    }
}
