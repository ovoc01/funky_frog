package com.ovoc01.funkyfrog.core.connection.configuration;

import com.ovoc01.funkyfrog.core.connection.ConnectionObject;

import lombok.Data;


@Data
public class ConnectionConfiguration {
    private transient String workspace;
    private ConnectionObject[] connections;
}
