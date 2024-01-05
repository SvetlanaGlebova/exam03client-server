package org.example;

import java.io.*;
import java.net.Socket;

public class Client {
    private static final String SERVER_ADRESS = "localhost";
    private static final int PORT = 8888;

    public static void main(String[] args) {
        try (
                Socket socket = new Socket(SERVER_ADRESS, PORT);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        ) {
            System.out.println("Success connection");

            String input = userInput.readLine();


            while (input != null) {
                printWriter.println(input);

                if ("sendFile".equals(input)) {
                    sendFile(socket.getOutputStream(), "C:\\Users\\HP\\JavaProjects\\client-sever\\client-server\\src\\main\\java\\org\\example\\file.txt");
                }
                if ("exit".equals(input)) {
                    break;
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void sendFile(OutputStream os, String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath);) {
            byte[] buffer = new byte[4096];
            int bytesRead = fis.read(buffer);

            while (bytesRead != -1) {
                os.write(buffer, 0, bytesRead);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}