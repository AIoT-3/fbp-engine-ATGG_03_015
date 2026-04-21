package com.fbp.engine.protocol.modbus.support;

public final class ModbusConfig {
    public static final String HOST_KEY = "host";
    public static final String PORT_KEY = "port";
    public static final String SLAVE_ID_KEY = "slaveId";

    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_PORT = 502;
    public static final int DEFAULT_SLAVE_ID = 1;

    private ModbusConfig() {
    }
}
