package com.fbp.engine.app.tools.tcp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TcpEchoServer {
    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(9090);
             Socket clientSocket = serverSocket.accept();
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter writer = new BufferedWriter(
                     new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8))

        ) {
            String line = reader.readLine();
            System.out.println("received: " + line);

            writer.write(line);
            writer.newLine();
            writer.flush();
        }
    }
}
