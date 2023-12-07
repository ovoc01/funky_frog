package com.ovoc01.funkyfrog;

import java.sql.Connection;
import java.sql.Timestamp;

import com.ovoc01.funkyfrog.core.connection.FunkyFrogConnexion;
import com.ovoc01.funkyfrog.core.mapping.spec.TypedQuery;
import com.ovoc01.funkyfrog.temp.Brand;
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
        Brand brand = new Brand();
        Brand brand2 = new Brand();
        System.out.println(brand.getInitializationProperty());
        System.out.println(brand2.getInitializationProperty());

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
