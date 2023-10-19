package com.ovoc01.app;
import java.sql.Connection;

import com.ovoc01.app.core.connection.DtbConnexion;
import com.ovoc01.app.core.tools.Utils;

public class App {
    public static void main(String[] args) throws Exception {
        Connection c = DtbConnexion.sessionConnection("connection2");
        System.out.println(c);
        System.out.println(Utils.uniqueUUID());
    }
}
