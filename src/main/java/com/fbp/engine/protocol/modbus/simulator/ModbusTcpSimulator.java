package com.fbp.engine.protocol.modbus.simulator;

import com.fbp.engine.protocol.modbus.exception.ModbusException;
import com.fbp.engine.protocol.modbus.exception.ModbusFailureType;
import com.fbp.engine.protocol.modbus.frame.ModbusExceptionCode;
import com.fbp.engine.protocol.modbus.frame.ModbusFrameDecoder;
import com.fbp.engine.protocol.modbus.frame.ModbusFrameEncoder;
import com.fbp.engine.protocol.modbus.frame.ModbusMbapHeader;
import com.fbp.engine.protocol.modbus.frame.ModbusValidator;
import com.fbp.engine.protocol.modbus.frame.request.ModbusRequest;
import com.fbp.engine.protocol.modbus.frame.request.ReadHoldingRegistersRequestPdu;
import com.fbp.engine.protocol.modbus.frame.request.WriteSingleRegisterRequestPdu;
import com.fbp.engine.protocol.modbus.frame.response.ModbusExceptionResponsePdu;
import com.fbp.engine.protocol.modbus.frame.response.ModbusResponse;
import com.fbp.engine.protocol.modbus.frame.response.ModbusResponsePdu;
import com.fbp.engine.protocol.modbus.frame.response.ReadHoldingRegistersResponsePdu;
import com.fbp.engine.protocol.modbus.frame.response.WriteSingleRegisterResponsePdu;
import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class ModbusTcpSimulator {
    private final int port;
    private final int[] registers;
    private final ReentrantLock registerLock = new ReentrantLock();

    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private volatile boolean running;

    public ModbusTcpSimulator(int port, int registerCount) {
        this.port = port;
        this.registers = new int[registerCount];
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            executorService = Executors.newVirtualThreadPerTaskExecutor();
            running = true;
            executorService.submit(this::acceptLoop);
        } catch (IOException e) {
            throw new ModbusException(ModbusFailureType.SIMULATOR_START_FAILED, e, port);
        }
    }

    public void stop() {
        running = false;
        closeServerSocket();
        if (executorService != null) {
            executorService.shutdownNow();
            executorService = null;
        }
    }

    public void handleClient(Socket clientSocket) {
        try (clientSocket;
             DataInputStream in = new DataInputStream(clientSocket.getInputStream());
             DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream())) {
            while (running && !clientSocket.isClosed()) {
                ModbusRequest request = ModbusFrameDecoder.decodeRequest(readFrame(in));
                out.write(ModbusFrameEncoder.encode(handleRequest(request)));
                out.flush();
            }
        } catch (EOFException | SocketException ignored) {
            // client closed
        } catch (IOException | RuntimeException e) {
            if (running) {
                log.warn("MODBUS TCP 시뮬레이터 클라이언트 처리 중 오류가 발생했습니다.", e);
            }
        }
    }

    public void setRegister(int address, int value) {
        validateSimulatorAddress(address);
        ModbusValidator.validateRegisterValue(value);

        registerLock.lock();
        try {
            registers[address] = value;
        } finally {
            registerLock.unlock();
        }
    }

    public int getRegister(int address) {
        validateSimulatorAddress(address);

        registerLock.lock();
        try {
            return registers[address];
        } finally {
            registerLock.unlock();
        }
    }

    public int getPort() {
        if (serverSocket == null) {
            return port;
        }
        return serverSocket.getLocalPort();
    }

    private void acceptLoop() {
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                executorService.submit(() -> handleClient(clientSocket));
            } catch (IOException e) {
                if (running) {
                    log.warn("MODBUS TCP 시뮬레이터 클라이언트 연결 수락 중 오류가 발생했습니다.", e);
                }
            }
        }
    }

    private ModbusResponse handleRequest(ModbusRequest request) {
        return switch (request.pdu()) {
            case ReadHoldingRegistersRequestPdu read -> handleRead(request, read);
            case WriteSingleRegisterRequestPdu write -> handleWrite(request, write);
        };
    }

    private ModbusResponse handleRead(ModbusRequest request, ReadHoldingRegistersRequestPdu read) {
        if (isInvalidRegisterRange(read.startAddress(), read.quantity())) {
            return exceptionResponse(request, ModbusExceptionCode.ILLEGAL_DATA_ADDRESS);
        }

        int[] values = readRegisters(read.startAddress(), read.quantity());

        return response(request, new ReadHoldingRegistersResponsePdu(values));
    }

    private ModbusResponse handleWrite(ModbusRequest request, WriteSingleRegisterRequestPdu write) {
        if (isInvalidRegisterRange(write.address(), 1)) {
            return exceptionResponse(request, ModbusExceptionCode.ILLEGAL_DATA_ADDRESS);
        }

        setRegister(write.address(), write.value());

        return response(request, new WriteSingleRegisterResponsePdu(write.address(), write.value()));
    }

    private int[] readRegisters(int startAddress, int quantity) {
        registerLock.lock();
        try {
            int[] values = new int[quantity];
            System.arraycopy(registers, startAddress, values, 0, quantity);
            return values;
        } finally {
            registerLock.unlock();
        }
    }

    private ModbusResponse response(ModbusRequest request, ModbusResponsePdu pdu) {
        return new ModbusResponse(
                ModbusMbapHeader.responseHeader(
                        request.header().transactionId(),
                        request.header().unitId(),
                        pdu
                ),
                pdu
        );
    }

    private ModbusResponse exceptionResponse(ModbusRequest request, ModbusExceptionCode exceptionCode) {
        return response(
                request,
                new ModbusExceptionResponsePdu(request.functionCode(), exceptionCode.getCode())
        );
    }

    private byte[] readFrame(DataInputStream in) throws IOException {
        byte[] mbapHeader = new byte[ModbusMbapHeader.HEADER_LENGTH];
        in.readFully(mbapHeader);

        int totalLength = ModbusFrameDecoder.frameLength(mbapHeader);
        byte[] frame = new byte[totalLength];
        System.arraycopy(mbapHeader, 0, frame, 0, mbapHeader.length);
        in.readFully(frame, mbapHeader.length, totalLength - mbapHeader.length);

        return frame;
    }

    private boolean isInvalidRegisterRange(int address, int quantity) {
        return address < 0
                || quantity <= 0
                || (long) address + quantity > registers.length;
    }

    private void validateSimulatorAddress(int address) {
        if (isInvalidRegisterRange(address, 1)) {
            throw new ModbusException(ModbusFailureType.REGISTER_ADDRESS_INVALID, address);
        }
    }

    private void closeServerSocket() {
        if (serverSocket == null) {
            return;
        }
        try {
            serverSocket.close();
        } catch (IOException e) {
            log.warn("MODBUS TCP 시뮬레이터 종료 중 오류가 발생했습니다.", e);
        } finally {
            serverSocket = null;
        }
    }
}
