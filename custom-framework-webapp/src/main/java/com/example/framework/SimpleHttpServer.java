package com.example.framework;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleHttpServer {

    private final int port;
    private final Map<String, RouteHandler> routes = new ConcurrentHashMap<>();
    private final ExecutorService executorService;
    private volatile boolean running = false;
    private ServerSocket serverSocket;

    public SimpleHttpServer(int port, int maxThreads) {
        this.port = port;
        this.executorService = Executors.newFixedThreadPool(maxThreads);
    }

    public void addRoute(String path, RouteHandler handler) {
        routes.put(path, handler);
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        running = true;
        System.out.println("SimpleHttpServer listening on port " + port);

        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                executorService.submit(() -> handleClient(clientSocket));
            } catch (IOException e) {
                if (running) {
                    e.printStackTrace();
                }
            }
        }

        executorService.shutdown();
        System.out.println("SimpleHttpServer stopped.");
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())), true)) {

            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                return;
            }

            String[] parts = requestLine.split(" ");
            if (parts.length < 3) {
                return;
            }

            String method = parts[0];
            String fullPath = parts[1];

            String path = fullPath;
            String queryString = "";
            int qIndex = fullPath.indexOf('?');
            if (qIndex != -1) {
                path = fullPath.substring(0, qIndex);
                queryString = fullPath.substring(qIndex + 1);
            }

            Map<String, String> queryParams = parseQueryParams(queryString);
            HttpRequest request = new HttpRequest(method, path, queryString, queryParams);

            // Leer y descartar el resto de headers por simplicidad
            String header;
            while ((header = in.readLine()) != null && !header.isEmpty()) {
                // no-op
            }

            RouteHandler handler = routes.get(path);
            String responseBody;
            int statusCode;

            if (handler != null) {
                responseBody = handler.handle(request);
                statusCode = 200;
            } else {
                responseBody = "Not Found";
                statusCode = 404;
            }

            writeHttpResponse(out, statusCode, "text/plain; charset=utf-8", responseBody);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Map<String, String> parseQueryParams(String queryString) {
        Map<String, String> params = new HashMap<>();
        if (queryString == null || queryString.isEmpty()) {
            return params;
        }
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                String key = urlDecode(kv[0]);
                String value = urlDecode(kv[1]);
                params.put(key, value);
            }
        }
        return params;
    }

    private String urlDecode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private void writeHttpResponse(PrintWriter out, int statusCode, String contentType, String body) {
        out.println("HTTP/1.1 " + statusCode + " " + getStatusText(statusCode));
        out.println("Content-Type: " + contentType);
        out.println("Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length);
        out.println();
        out.print(body);
        out.flush();
    }

    private String getStatusText(int statusCode) {
        return switch (statusCode) {
            case 200 -> "OK";
            case 404 -> "Not Found";
            default -> "Unknown";
        };
    }
}
