package com.fbp.engine.protocol.modbus.client;

import com.fbp.engine.protocol.modbus.exception.ModbusException;
import com.fbp.engine.protocol.modbus.exception.ModbusFailureType;
import com.fbp.engine.protocol.modbus.frame.ModbusFrameDecoder;
import com.fbp.engine.protocol.modbus.frame.ModbusFrameEncoder;
import com.fbp.engine.protocol.modbus.frame.ModbusMbapHeader;
import com.fbp.engine.protocol.modbus.frame.ModbusValidator;
import com.fbp.engine.protocol.modbus.frame.request.ModbusRequest;
import com.fbp.engine.protocol.modbus.frame.request.ReadHoldingRegistersRequestPdu;
import com.fbp.engine.protocol.modbus.frame.request.WriteSingleRegisterRequestPdu;
import com.fbp.engine.protocol.modbus.frame.response.ModbusResponse;
import com.fbp.engine.protocol.modbus.frame.response.ReadHoldingRegistersResponsePdu;
import com.fbp.engine.protocol.modbus.frame.response.WriteSingleRegisterResponsePdu;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

public class ModbusTcpClient {
    private static final int SOCKET_TIMEOUT_MS = 3000;

    private final String host;
    private final int port;
    private final ReentrantLock requestLock = new ReentrantLock();

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private int transactionId = 0;

    public ModbusTcpClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() {
        try {
            socket = new Socket(host, port);
            socket.setSoTimeout(SOCKET_TIMEOUT_MS);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new ModbusException(ModbusFailureType.CONNECTION_FAILED, e, host, port);
        }
    }

    public void disconnect() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            throw new ModbusException(ModbusFailureType.DISCONNECTION_FAILED, e);
        } finally {
            socket = null;
            in = null;
            out = null;
        }
    }

    public boolean isConnected() {
        return socket != null
                && socket.isConnected()
                && !socket.isClosed();
    }

    public int[] readHoldingRegisters(int unitId, int startAddress, int quantity) {
        requestLock.lock();
        try {
            // 1. 연결 상태 검증
            if (!isConnected()) {
                throw new ModbusException(ModbusFailureType.NOT_CONNECTED);
            }

            // 2. 요청 파라미터 검증
            ModbusValidator.validateUnitId(unitId);
            ModbusValidator.validateRegisterAddress(startAddress);
            ModbusValidator.validateReadRegisterCount(quantity);

            // 3. 요청 생성
            ModbusRequest request = ModbusRequest.of(
                    nextTransactionId(),
                    unitId,
                    new ReadHoldingRegistersRequestPdu(startAddress, quantity)
            );

            // 4. 요청 전송 및 응답 수신
            ModbusResponse response = exchange(request);
            if (!(response.pdu() instanceof ReadHoldingRegistersResponsePdu readPdu)) {
                throw new ModbusException(ModbusFailureType.RESPONSE_INVALID);
            }

            // 5. 응답 검증
            int[] registers = readPdu.registers();
            if (registers.length != quantity) {
                throw new ModbusException(ModbusFailureType.RESPONSE_INVALID);
            }

            return registers;

        } finally {
            requestLock.unlock();
        }
    }

    public void writeSingleRegister(int unitId, int address, int value) {
        requestLock.lock();
        try {
            // 1. 연결 상태 검증
            if (!isConnected()) {
                throw new ModbusException(ModbusFailureType.NOT_CONNECTED);
            }

            // 2. 요청 파라미터 검증
            ModbusValidator.validateUnitId(unitId);
            ModbusValidator.validateRegisterAddress(address);
            ModbusValidator.validateRegisterValue(value);

            // 3. 요청 생성
            ModbusRequest request = ModbusRequest.of(
                    nextTransactionId(),
                    unitId,
                    new WriteSingleRegisterRequestPdu(address, value)
            );

            // 4. 요청 전송 및 응답 수신
            ModbusResponse response = exchange(request);
            if (!(response.pdu() instanceof WriteSingleRegisterResponsePdu writePdu)) {
                throw new ModbusException(ModbusFailureType.RESPONSE_INVALID);
            }

            // 5. 응답 검증
            if (writePdu.address() != address || writePdu.value() != value) {
                throw new ModbusException(ModbusFailureType.RESPONSE_INVALID);
            }

        } finally {
            requestLock.unlock();
        }
    }

    // 요청 전송 및 응답 수신/검증
    private ModbusResponse exchange(ModbusRequest request) {
        try {
            out.write(ModbusFrameEncoder.encode(request));
            out.flush();

            ModbusResponse response = ModbusFrameDecoder.decode(readResponseFrame());
            ModbusValidator.validateResponse(request, response);

            return response;
        } catch (ModbusException e) {
            throw e;
        } catch (IOException e) {
            throw new ModbusException(ModbusFailureType.IO_FAILED, e);
        }
    }

    // 응답 프레임 읽기 (MBAP 헤더 + PDU)
    private byte[] readResponseFrame() throws IOException {
        // 1. MBAP 헤더 읽기
        byte[] mbapHeader = new byte[ModbusMbapHeader.HEADER_LENGTH];
        in.readFully(mbapHeader);

        // 2. 전체 프레임 길이 계산
        int totalLength = ModbusFrameDecoder.responseLength(mbapHeader);
        if (totalLength < ModbusMbapHeader.HEADER_LENGTH) {
            throw new ModbusException(ModbusFailureType.RESPONSE_INVALID);
        }

        // 3. 전체 프레임 읽기
        byte[] response = new byte[totalLength];
        System.arraycopy(mbapHeader, 0, response, 0, mbapHeader.length);

        // 4. PDU 부분 읽기
        int remainingLength = totalLength - mbapHeader.length;
        in.readFully(response, mbapHeader.length, remainingLength);

        return response;
    }

    // Transaction ID가 0~65535 범위를 순환하도록 생성
    private int nextTransactionId() {
        transactionId = (transactionId + 1) & 0xFFFF;
        return transactionId;
    }
}
