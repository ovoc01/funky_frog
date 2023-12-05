package com.ovoc01.funkyfrog;

import java.sql.Connection;
import java.sql.Timestamp;

import com.ovoc01.funkyfrog.core.connection.FunkyFrogConnexion;
import com.ovoc01.funkyfrog.core.mapping.spec.TypedQuery;
import com.ovoc01.funkyfrog.temp.Employe;

public class App {
    // private static final Logger LOGGER = Logger.getLogger(App.class.getName());

    public static void main(String[] args) throws Exception {
        /*
         * Gson gson = new Gson();
         * JsonReader jsonReader = new JsonReader(new
         * FileReader("datasource.config.json"));
         * ConnectionConfiguration configuration =
         * gson.fromJson(jsonReader,ConnectionConfiguration.class);
         */

        Connection c = FunkyFrogConnexion.sessionConnection("connection1");
        System.out.println(c);

        TypedQuery<Timestamp> query = new TypedQuery<>(Timestamp.class);
        Timestamp[] idEmploye = query.createNativeQuery("select dateCreation from besoins").fetchResults(c);
        System.out.println(idEmploye);

        // BesoinTransactionLog besoinTransactionLog = new BesoinTransactionLog();

        // Field[] fields = Utils.selectableFields(besoinTransactionLog);
        // System.out.println("Hello worlds");

        // LOGGER.info(Utils.currentLocation());
        // LOGGER.info(configuration.toString());

        // Brand brand = new Brand();
        // Brand d = brand.someFunction("BRAN0001", c);
        // d.objectOverview();
        c.close();
    }
}
