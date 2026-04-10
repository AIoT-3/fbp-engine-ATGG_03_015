package com.fbp.engine.app.tools.tcp;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TcpEchoClient {
    public static void main(String[] args) throws IOException {
        try (Socket socket = new Socket("localhost", 9090);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter writer = new BufferedWriter(
                     new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))
        ) {
            writer.write("Hello FBP");
            writer.newLine();
            writer.flush();

            String response = reader.readLine();
            System.out.println("response: " + response);
        }
    }
}
