package com.colytra.server;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class Server {
    public static void main(String[] args) {
        final int PORT = 2124;

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Сервер запущен и ожидает подключений на порту " + PORT);

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(
                             new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(
                             clientSocket.getOutputStream(), true)) {

                    System.out.println("Подключился клиент: " +
                            clientSocket.getInetAddress().getHostAddress());

                    // Читаем сообщение от клиента
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        System.out.println("Получено от клиента: " + inputLine);

                        // Отправляем ответ клиенту
                        String response = "Сервер получил: " + inputLine;
                        out.println(response);

                        // Если клиент прислал "exit" - закрываем соединение
                        if (inputLine.equalsIgnoreCase("exit")) {
                            break;
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Ошибка при работе с клиентом: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Не удалось запустить сервер: " + e.getMessage());
        }
    }
}