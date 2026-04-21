package com.fbp.engine.protocol.modbus.frame;

import java.io.DataOutputStream;
import java.io.IOException;

@FunctionalInterface
interface ModbusPayloadWriter {
    void write(DataOutputStream out) throws IOException;
}
