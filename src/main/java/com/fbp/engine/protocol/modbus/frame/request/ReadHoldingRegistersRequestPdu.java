package com.fbp.engine.protocol.modbus.frame.request;

import com.fbp.engine.protocol.modbus.frame.ModbusFunctionCode;

public record ReadHoldingRegistersRequestPdu(
        int startAddress,
        int quantity
) implements ModbusRequestPdu {

    @Override
    public ModbusFunctionCode functionCode() {
        return ModbusFunctionCode.READ_HOLDING_REGISTERS;
    }

    @Override
    public int byteLength() {
        // functionCode(1) + startAddress(2) + quantity(2)
        return 5;
    }
}
