package com.colytra.server;

import com.colytra.cvhelper.core.Point;
import com.colytra.cvhelper.core.color.ColorDifference;
import com.colytra.cvhelper.core.matrix.Matrix;
import com.colytra.cvhelper.image.ImageMatrix;
import com.colytra.cvhelper.search.Searcher;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Server {
    public static void main(String[] args) {
        final int PORT = 2124;

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Сервер запущен и ожидает подключений на порту " + PORT);

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
                     ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream())) {

                    System.out.println("Подключился клиент: " +
                            clientSocket.getInetAddress().getHostAddress());

                    while (true) {
                        try {
                            List<int[][]> matrices = (List<int[][]>) ois.readObject();
                            if (matrices.size() > 1) {
                                Matrix m1 = new Matrix();
                                m1.convert(matrices.get(0));
                                Matrix m2 = new Matrix();
                                m2.convert(matrices.get(1));
                                List<Point> points = Searcher.findAll(m1,m2, new ColorDifference(60), 45, 3);
                                List<java.awt.Point> parsed = new ArrayList<>();
                                points.forEach(e -> parsed.add(new java.awt.Point(e.getX(), e.getY())));
                                oos.writeObject(parsed);
                                oos.flush();
                                points = null;
                                m1 = null;
                                m2 = null;
                            }

                        } catch (ClassNotFoundException e) {
                            System.err.println("Неизвестный класс объекта: " + e.getMessage());
                        } catch (EOFException e) {
                            System.out.println("Клиент отключился");
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