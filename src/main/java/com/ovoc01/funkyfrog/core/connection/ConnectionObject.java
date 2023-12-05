package com.ovoc01.funkyfrog.core.connection;

import lombok.Data;

@Data
public class ConnectionObject {
    private String connection_id;
    private String type;
    private String datasource_url;
    private String jdbc_driver;
    private String user;
    private String pwd;   
}
