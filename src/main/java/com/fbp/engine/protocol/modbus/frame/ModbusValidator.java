package com.fbp.engine.protocol.modbus.frame;

import com.fbp.engine.protocol.modbus.exception.ModbusException;
import com.fbp.engine.protocol.modbus.exception.ModbusFailureType;
import com.fbp.engine.protocol.modbus.frame.request.ModbusRequest;
import com.fbp.engine.protocol.modbus.frame.response.ModbusExceptionResponsePdu;
import com.fbp.engine.protocol.modbus.frame.response.ModbusResponse;

public final class ModbusValidator {
    private static final int MAX_UNIT_ID = 0xFF;
    private static final int MAX_REGISTER_ADDRESS = 0xFFFF;
    private static final int MAX_REGISTER_VALUE = 0xFFFF;
    private static final int MAX_READ_REGISTER_COUNT = 125;

    private ModbusValidator() {}

    // Request 검증
    public static void validateUnitId(int unitId) {
        if (unitId < 0 || unitId > MAX_UNIT_ID) {
            throw new ModbusException(ModbusFailureType.RESPONSE_INVALID);
        }
    }

    public static void validateRegisterAddress(int address) {
        if (address < 0 || address > MAX_REGISTER_ADDRESS) {
            throw new ModbusException(ModbusFailureType.REGISTER_ADDRESS_INVALID, address);
        }
    }

    public static void validateRegisterValue(int value) {
        if (value < 0 || value > MAX_REGISTER_VALUE) {
            throw new ModbusException(ModbusFailureType.REGISTER_VALUE_INVALID, value);
        }
    }

    public static void validateReadRegisterCount(int count) {
        if (count < 1 || count > MAX_READ_REGISTER_COUNT) {
            throw new ModbusException(ModbusFailureType.READ_REGISTER_QUANTITY_INVALID, count);
        }
    }


    // Response 검증
    public static void validateResponse(ModbusRequest request, ModbusResponse response) {
        // Transaction ID 검증
        if (request.header().transactionId() != response.header().transactionId()) {
            throw new ModbusException(
                    ModbusFailureType.TRANSACTION_ID_MISMATCH,
                    request.header().transactionId(),
                    response.header().transactionId()
            );
        }

        // Protocol ID 검증
        if (response.header().protocolId() != ModbusMbapHeader.PROTOCOL_ID) {
            throw new ModbusException(ModbusFailureType.RESPONSE_INVALID);
        }

        // Unit ID 검증
        if (request.header().unitId() != response.header().unitId()) {
            throw new ModbusException(ModbusFailureType.RESPONSE_INVALID);
        }

        // 예외 응답 검증
        if (response.pdu() instanceof ModbusExceptionResponsePdu exceptionPdu) {
            ModbusExceptionCode.fromCode(exceptionPdu.exceptionCode());
            throw new ModbusException(
                    ModbusFailureType.EXCEPTION_RESPONSE,
                    request.functionCode().getCode(),
                    exceptionPdu.exceptionCode()
            );
        }

        // Function Code 검증
        if (request.pdu().functionCode().getCode() != response.pdu().functionCode()) {
            throw new ModbusException(ModbusFailureType.RESPONSE_INVALID);
        }
    }
}
