package com.example.framework;

@FunctionalInterface
public interface RouteHandler {
    String handle(HttpRequest request);
}
