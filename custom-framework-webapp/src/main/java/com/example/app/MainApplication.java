package com.example.app;

import com.example.framework.HttpRequest;
import com.example.framework.SimpleHttpServer;

public class MainApplication {

    public static void main(String[] args) throws Exception {
        int port = getPort();
        SimpleHttpServer server = new SimpleHttpServer(port, 10);

        server.addRoute("/hello", (HttpRequest request) -> {
            String name = request.getQueryParam("name", "World");
            return "Hello from custom framework, " + name + "!";
        });

        server.addRoute("/shutdown", (HttpRequest request) -> {
            new Thread(server::stop).start();
            return "Shutting down server...";
        });

        server.start();
    }

    private static int getPort() {
        String envPort = System.getenv("PORT");
        if (envPort != null) {
            try {
                return Integer.parseInt(envPort);
            } catch (NumberFormatException ignored) {
            }
        }
        return 6000;
    }
}
