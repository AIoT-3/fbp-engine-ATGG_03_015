package com.fbp.engine.protocol.modbus.frame;

import com.fbp.engine.protocol.modbus.frame.request.ModbusRequest;
import com.fbp.engine.protocol.modbus.frame.request.ModbusRequestPdu;
import com.fbp.engine.protocol.modbus.frame.request.ReadHoldingRegistersRequestPdu;
import com.fbp.engine.protocol.modbus.frame.request.WriteSingleRegisterRequestPdu;
import com.fbp.engine.protocol.modbus.frame.response.ModbusExceptionResponsePdu;
import com.fbp.engine.protocol.modbus.frame.response.ModbusResponse;
import com.fbp.engine.protocol.modbus.frame.response.ModbusResponsePdu;
import com.fbp.engine.protocol.modbus.frame.response.ReadHoldingRegistersResponsePdu;
import com.fbp.engine.protocol.modbus.frame.response.WriteSingleRegisterResponsePdu;

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

    public static byte[] encode(ModbusResponse response) {
        return ModbusFrameSupport.bytes(out -> {
            ModbusMbapHeader header = response.header();

            out.writeShort(header.transactionId());
            out.writeShort(header.protocolId());
            out.writeShort(header.length());
            out.writeByte(header.unitId());

            encodeResponsePdu(out, response.pdu());
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

    private static void encodeResponsePdu(DataOutputStream out, ModbusResponsePdu pdu) throws IOException {
        switch (pdu) {
            case ReadHoldingRegistersResponsePdu read -> {
                out.writeByte(read.functionCode());
                out.writeByte(read.byteCount());
                for (int register : read.registers()) {
                    out.writeShort(register);
                }
            }
            case WriteSingleRegisterResponsePdu write -> {
                out.writeByte(write.functionCode());
                out.writeShort(write.address());
                out.writeShort(write.value());
            }
            case ModbusExceptionResponsePdu exception -> {
                out.writeByte(exception.functionCode());
                out.writeByte(exception.exceptionCode());
            }
        }
    }
}
