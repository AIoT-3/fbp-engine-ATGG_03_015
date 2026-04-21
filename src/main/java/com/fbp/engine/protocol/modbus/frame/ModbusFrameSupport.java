package com.fbp.engine.protocol.modbus.frame;

import com.fbp.engine.protocol.modbus.exception.ModbusException;
import com.fbp.engine.protocol.modbus.exception.ModbusFailureType;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class ModbusFrameSupport {
    private ModbusFrameSupport() {}

    public static int readUnsignedShort(byte[] data, int offset) {
        return ((data[offset] & 0xFF) << 8) | (data[offset + 1] & 0xFF);
    }

    static byte[] bytes(ModbusPayloadWriter writer) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(buffer);
            writer.write(out);
            return buffer.toByteArray();
        } catch (IOException e) {
            throw new ModbusException(ModbusFailureType.ENCODING_FAILED, e);
        }
    }
}
