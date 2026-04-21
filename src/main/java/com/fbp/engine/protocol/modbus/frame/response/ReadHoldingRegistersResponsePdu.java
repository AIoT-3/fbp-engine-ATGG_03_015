package com.fbp.engine.protocol.modbus.frame.response;

import com.fbp.engine.protocol.modbus.frame.ModbusFunctionCode;

public record ReadHoldingRegistersResponsePdu(
        int[] registers
) implements ModbusResponsePdu {
    public ReadHoldingRegistersResponsePdu {
        registers = registers != null ? registers.clone() : new int[0];
    }

    @Override
    public int[] registers() {
        return registers != null ? registers.clone() : new int[0];
    }

    @Override
    public int functionCode() {
        return ModbusFunctionCode.READ_HOLDING_REGISTERS.getCode();
    }

    @Override
    public int byteLength() {
        // functionCode(1) + byteCount(1) + byteCountValue(n*2)
        return 2 + byteCount();
    }

    public int byteCount() {
        return registers.length * 2;
    }
}
