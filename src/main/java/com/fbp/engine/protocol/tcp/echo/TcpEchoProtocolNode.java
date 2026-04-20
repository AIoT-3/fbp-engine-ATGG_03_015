package com.fbp.engine.protocol.tcp.echo;

import com.fbp.engine.core.exception.EngineException;
import com.fbp.engine.core.exception.EngineFailureType;
import com.fbp.engine.core.message.PortMessage;
import com.fbp.engine.core.node.protocol.ProtocolNode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TcpEchoProtocolNode extends ProtocolNode {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 9090;
    private static final int SOCKET_TIMEOUT_MS = 3000;

    private final String host;
    private final int port;
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    public TcpEchoProtocolNode(String id, String host, int port) {
        super(id, null);
        this.host = host;
        this.port = port;
        addInputPort("in");
        addOutputPort("out");
    }

    private TcpEchoProtocolNode(String id, Socket socket) {
        this(id, DEFAULT_HOST, DEFAULT_PORT);
        this.socket = socket;
    }

    public static TcpEchoProtocolNode fromAcceptedSocket(String id, Socket socket) {
        return new TcpEchoProtocolNode(id, socket);
    }

    @Override
    protected void doConnect() {
        try {
            if (socket == null) {
                socket = new Socket(host, port);
            }
            socket.setSoTimeout(SOCKET_TIMEOUT_MS);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new EngineException(EngineFailureType.PROTOCOL_CONNECTION_FAILED, e);
        }
    }

    @Override
    protected void doDisconnect() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            throw new EngineException(EngineFailureType.PROTOCOL_DISCONNECTION_FAILED, e);
        } finally {
            writer = null;
            reader = null;
            socket = null;
        }
    }

    @Override
    public void onProcess(PortMessage portMessage) {
        String request = String.valueOf(portMessage.message().get("message"));
        String response = echo(request);
        send("out", portMessage.message().withEntry("echo", response));
    }

    public String echo(String request) {
        try {
            writer.write(request);
            writer.newLine();
            writer.flush();
            return reader.readLine();
        } catch (IOException e) {
            throw new EngineException(EngineFailureType.PROTOCOL_MESSAGE_SEND_FAILED, e);
        }
    }

    public String echoNextRequest() {
        try {
            String request = reader.readLine();
            if (request != null) {
                writer.write(request);
                writer.newLine();
                writer.flush();
            }
            return request;
        } catch (IOException e) {
            throw new EngineException(EngineFailureType.PROTOCOL_MESSAGE_RECEIVE_FAILED, e);
        }
    }
}
