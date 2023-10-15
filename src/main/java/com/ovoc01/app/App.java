package com.ovoc01.app;

import java.sql.Connection;

import com.ovoc01.app.core.connection.DtbConnexion;
import com.ovoc01.app.core.tools.Utils;

public class App 
{
    public static void main( String[] args ) throws Exception
    {
       Connection c = DtbConnexion.sessionConnection("web_service_tp");
       System.out.println(c);
    }
}
