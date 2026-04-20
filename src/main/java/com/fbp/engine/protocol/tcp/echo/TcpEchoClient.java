package com.fbp.engine.protocol.tcp.echo;

public class TcpEchoClient {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 9090;

    public static void main(String[] args) {
        TcpEchoProtocolNode echoNode = new TcpEchoProtocolNode("tcp-echo-client", DEFAULT_HOST, DEFAULT_PORT);
        try {
            echoNode.initialize();
            String response = echoNode.echo("Hello FBP");
            System.out.println("response: " + response);
        } finally {
            echoNode.shutdown();
        }
    }
}
