package com.fbp.engine.protocol.modbus.frame;

import com.fbp.engine.protocol.modbus.frame.request.ModbusRequest;
import com.fbp.engine.protocol.modbus.frame.request.ModbusRequestPdu;
import com.fbp.engine.protocol.modbus.frame.request.ReadHoldingRegistersRequestPdu;
import com.fbp.engine.protocol.modbus.frame.request.WriteSingleRegisterRequestPdu;

import java.io.DataOutputStream;
import java.io.IOException;

public final class ModbusFrameEncoder {
    private ModbusFrameEncoder() {}

    public static byte[] encode(ModbusRequest request) {
        return ModbusFrameSupport.bytes(out -> {
            ModbusMbapHeader header = request.header();

            out.writeShort(header.transactionId());
            out.writeShort(header.protocolId());
            out.writeShort(header.length());
            out.writeByte(header.unitId());

            encodeRequestPdu(out, request.pdu());
        });
    }

    private static void encodeRequestPdu(DataOutputStream out, ModbusRequestPdu pdu) throws IOException {
        switch (pdu) {
            case ReadHoldingRegistersRequestPdu read -> {
                out.writeByte(read.functionCode().getCode());
                out.writeShort(read.startAddress());
                out.writeShort(read.quantity());
            }
            case WriteSingleRegisterRequestPdu write -> {
                out.writeByte(write.functionCode().getCode());
                out.writeShort(write.address());
                out.writeShort(write.value());
            }
        }
    }
}
