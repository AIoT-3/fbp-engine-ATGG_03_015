package com.fbp.engine.protocol.tcp.echo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpEchoServer {
    private static final int DEFAULT_PORT = 9090;

    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(DEFAULT_PORT)) {
            Socket clientSocket = serverSocket.accept();
            TcpEchoProtocolNode echoNode = TcpEchoProtocolNode.fromAcceptedSocket("tcp-echo-server", clientSocket);
            try {
                echoNode.initialize();
                String line = echoNode.echoNextRequest();
                System.out.println("received: " + line);
            } finally {
                echoNode.shutdown();
            }
        }
    }
}
