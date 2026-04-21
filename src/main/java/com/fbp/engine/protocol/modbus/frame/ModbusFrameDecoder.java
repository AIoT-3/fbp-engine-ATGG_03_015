package com.fbp.engine.protocol.modbus.frame;

import com.fbp.engine.protocol.modbus.exception.ModbusException;
import com.fbp.engine.protocol.modbus.exception.ModbusFailureType;
import com.fbp.engine.protocol.modbus.frame.request.ModbusRequest;
import com.fbp.engine.protocol.modbus.frame.request.ModbusRequestPdu;
import com.fbp.engine.protocol.modbus.frame.request.ReadHoldingRegistersRequestPdu;
import com.fbp.engine.protocol.modbus.frame.request.WriteSingleRegisterRequestPdu;
import com.fbp.engine.protocol.modbus.frame.response.ModbusExceptionResponsePdu;
import com.fbp.engine.protocol.modbus.frame.response.ModbusResponse;
import com.fbp.engine.protocol.modbus.frame.response.ModbusResponsePdu;
import com.fbp.engine.protocol.modbus.frame.response.ReadHoldingRegistersResponsePdu;
import com.fbp.engine.protocol.modbus.frame.response.WriteSingleRegisterResponsePdu;

public final class ModbusFrameDecoder {
    private ModbusFrameDecoder() {}

    public static ModbusRequest decodeRequest(byte[] request) {
        if (request.length < ModbusMbapHeader.HEADER_LENGTH) {
            throw new ModbusException(ModbusFailureType.RESPONSE_INVALID);
        }

        ModbusMbapHeader header = new ModbusMbapHeader(
                ModbusFrameSupport.readUnsignedShort(request, 0),
                ModbusFrameSupport.readUnsignedShort(request, 2),
                ModbusFrameSupport.readUnsignedShort(request, 4),
                request[6] & 0xFF
        );

        int expectedRequestLength = 6 + header.length();
        if (request.length != expectedRequestLength) {
            throw new ModbusException(ModbusFailureType.RESPONSE_INVALID);
        }

        ModbusRequestPdu pdu = decodeRequestPdu(
                request,
                ModbusMbapHeader.HEADER_LENGTH,
                request[ModbusMbapHeader.HEADER_LENGTH] & 0xFF);

        return new ModbusRequest(header, pdu);
    }

    public static ModbusResponse decodeResponse(byte[] response) {
        if (response.length < ModbusMbapHeader.HEADER_LENGTH) {
            throw new ModbusException(ModbusFailureType.RESPONSE_INVALID);
        }

        ModbusMbapHeader header = new ModbusMbapHeader(
                ModbusFrameSupport.readUnsignedShort(response, 0),
                ModbusFrameSupport.readUnsignedShort(response, 2),
                ModbusFrameSupport.readUnsignedShort(response, 4),
                response[6] & 0xFF
        );

        int expectedPduLength = 6 + header.length();
        if (response.length != expectedPduLength) {
            throw new ModbusException(ModbusFailureType.RESPONSE_INVALID);
        }

        ModbusResponsePdu pdu = decodeResponsePdu(
                response,
                ModbusMbapHeader.HEADER_LENGTH,
                response[ModbusMbapHeader.HEADER_LENGTH] & 0xFF);

        return new ModbusResponse(header, pdu);
    }

    public static int frameLength(byte[] mbapHeader) {
        if (mbapHeader.length != ModbusMbapHeader.HEADER_LENGTH) {
            throw new ModbusException(ModbusFailureType.RESPONSE_INVALID);
        }

        int length = ModbusFrameSupport.readUnsignedShort(mbapHeader, 4);
        return 6 + length;
    }

    private static ModbusRequestPdu decodeRequestPdu(byte[] request, int pduOffset, int functionCode) {
        ModbusFunctionCode modbusFunctionCode = ModbusFunctionCode.fromCode(functionCode);
        return switch (modbusFunctionCode) {
            case READ_HOLDING_REGISTERS -> decodeFC03RequestPdu(request, pduOffset);
            case WRITE_SINGLE_REGISTER -> decodeFC06RequestPdu(request, pduOffset);
            case READ_INPUT_REGISTERS, WRITE_MULTIPLE_REGISTERS ->
                    throw new ModbusException(ModbusFailureType.UNSUPPORTED_FUNCTION_CODE, functionCode);
        };
    }

    private static ModbusResponsePdu decodeResponsePdu(byte[] response, int pduOffset, int functionCode) {
        if ((functionCode & ModbusExceptionResponsePdu.EXCEPTION_FUNCTION_CODE_MASK) != 0) {
            int originalFunctionCode = functionCode & ~ModbusExceptionResponsePdu.EXCEPTION_FUNCTION_CODE_MASK;
            int exceptionCode = response[pduOffset + 1] & 0xFF;

            return new ModbusExceptionResponsePdu(
                    ModbusFunctionCode.fromCode(originalFunctionCode),
                    exceptionCode
            );
        }

        ModbusFunctionCode modbusFunctionCode = ModbusFunctionCode.fromCode(functionCode);
        return switch (modbusFunctionCode) {
            case READ_HOLDING_REGISTERS -> decodeFC03ResponsePdu(response, pduOffset);
            case WRITE_SINGLE_REGISTER -> decodeFC06ResponsePdu(response, pduOffset);
            case READ_INPUT_REGISTERS, WRITE_MULTIPLE_REGISTERS ->
                    throw new ModbusException(ModbusFailureType.UNSUPPORTED_FUNCTION_CODE, functionCode);
        };
    }

    private static ReadHoldingRegistersRequestPdu decodeFC03RequestPdu(
            byte[] request,
            int pduOffset
    ) {
        int actualPduLength = request.length - pduOffset;
        if (actualPduLength != 5) {
            throw new ModbusException(ModbusFailureType.RESPONSE_INVALID);
        }

        return new ReadHoldingRegistersRequestPdu(
                ModbusFrameSupport.readUnsignedShort(request, pduOffset + 1),
                ModbusFrameSupport.readUnsignedShort(request, pduOffset + 3)
        );
    }

    private static WriteSingleRegisterRequestPdu decodeFC06RequestPdu(
            byte[] request,
            int pduOffset
    ) {
        int actualPduLength = request.length - pduOffset;
        if (actualPduLength != 5) {
            throw new ModbusException(ModbusFailureType.RESPONSE_INVALID);
        }

        return new WriteSingleRegisterRequestPdu(
                ModbusFrameSupport.readUnsignedShort(request, pduOffset + 1),
                ModbusFrameSupport.readUnsignedShort(request, pduOffset + 3)
        );
    }

    private static ReadHoldingRegistersResponsePdu decodeFC03ResponsePdu(
            byte[] response,
            int pduOffset
    ) {
        int byteCount = response[pduOffset + 1] & 0xFF;
        if (byteCount % 2 != 0) {
            throw new ModbusException(ModbusFailureType.RESPONSE_INVALID);
        }

        int expectedPduLength = 2 + byteCount;
        int actualPduLength = response.length - pduOffset;
        if (actualPduLength != expectedPduLength) {
            throw new ModbusException(ModbusFailureType.RESPONSE_INVALID);
        }

        int registerCount = byteCount / 2;
        int[] registers = new int[registerCount];

        int dataOffset = pduOffset + 2;
        for (int i = 0; i < registerCount; i++) {
            registers[i] = ModbusFrameSupport.readUnsignedShort(response, dataOffset + i * 2);
        }

        return new ReadHoldingRegistersResponsePdu(registers);
    }

    private static WriteSingleRegisterResponsePdu decodeFC06ResponsePdu(
            byte[] response,
            int pduOffset
    ) {
        int actualPduLength = response.length - pduOffset;
        if (actualPduLength != 5) {
            throw new ModbusException(ModbusFailureType.RESPONSE_INVALID);
        }

        return new WriteSingleRegisterResponsePdu(
                ModbusFrameSupport.readUnsignedShort(response, pduOffset + 1),
                ModbusFrameSupport.readUnsignedShort(response, pduOffset + 3)
        );
    }
}
