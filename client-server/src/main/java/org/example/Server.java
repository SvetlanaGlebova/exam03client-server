package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static int PORT = 8888;
    private static final ExecutorService pool = Executors.newFixedThreadPool(10);
    private static ConcurrentHashMap<String, String> files = new ConcurrentHashMap<>();


    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server started");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                pool.execute(clientHandler);
            }

        } catch (IOException e) {
            e.printStackTrace();

        }

    }

    static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final BufferedReader in;
        private final PrintWriter out;

        ClientHandler(Socket socket) throws IOException {
            this.clientSocket = socket;
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

        }

        @Override
        public void run() {
            try {
                String inputLine = in.readLine();
                while (inputLine != null) {
                    if ("sendFile".equals(inputLine)) {
                        receiveFile();
                        System.out.println("File has been received");
                    }
                }
                in.close();
                out.close();
                clientSocket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        private void receiveFile() {
            try {
                String fileName = in.readLine();
                int fileSize = Integer.parseInt(in.readLine());

                // Предполагается, что файлы будут сохраняться в определенной директории
                FileOutputStream fos = new FileOutputStream("server_files/" + fileName);
                byte[] buffer = new byte[4096];
                int bytesRead;
                int totalRead = 0;

                while (totalRead < fileSize && (bytesRead = clientSocket.getInputStream().read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                    totalRead += bytesRead;
                }
                fos.close();

            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
