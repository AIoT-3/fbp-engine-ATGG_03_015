package com.fbp.engine.protocol.modbus.exception;

import com.fbp.engine.common.exception.FailureType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ModbusFailureType implements FailureType {
    // Connection
    CONNECTION_FAILED("MODBUS TCP 연결에 실패했습니다: %s:%s"),
    DISCONNECTION_FAILED("MODBUS TCP 연결 해제에 실패했습니다."),
    NOT_CONNECTED("MODBUS TCP 클라이언트가 연결되어 있지 않습니다."),

    // I/O and response
    IO_FAILED("MODBUS TCP 통신 중 I/O 실패가 발생했습니다"),
    ENCODING_FAILED("MODBUS 요청 프레임 인코딩에 실패했습니다"),
    RESPONSE_INVALID("MODBUS 응답 프레임이 올바르지 않습니다"),
    TRANSACTION_ID_MISMATCH("MODBUS transaction id가 일치하지 않습니다: expected=%s, actual=%s"),
    UNKNOWN_FUNCTION_CODE("알 수 없는 MODBUS function code입니다: code=%s"),
    UNKNOWN_EXCEPTION_CODE("알 수 없는 MODBUS exception code입니다: code=%s"),
    UNSUPPORTED_FUNCTION_CODE("지원되지 않는 MODBUS function code입니다: code=%s"),
    EXCEPTION_RESPONSE("MODBUS exception 응답을 수신했습니다: function=%s, code=%s"),

    // Register
    REGISTER_ADDRESS_INVALID("MODBUS 레지스터 주소가 올바르지 않습니다: address=%s"),
    REGISTER_VALUE_INVALID("MODBUS 레지스터 값이 16비트 범위를 벗어났습니다: value=%s"),
    READ_REGISTER_QUANTITY_INVALID("MODBUS 레지스터 개수가 올바르지 않습니다: quantity=%s"),

    // Node message
    MESSAGE_FIELD_INVALID("MODBUS 메시지 필드가 올바르지 않습니다: field=%s, value=%s"),

    // Simulator
    SIMULATOR_START_FAILED("MODBUS TCP 시뮬레이터 시작에 실패했습니다: port=%s");

    private final String messageTemplate;
}
