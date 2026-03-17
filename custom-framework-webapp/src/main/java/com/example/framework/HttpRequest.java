package com.example.framework;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private final String method;
    private final String path;
    private final String queryString;
    private final Map<String, String> queryParams;

    public HttpRequest(String method, String path, String queryString, Map<String, String> queryParams) {
        this.method = method;
        this.path = path;
        this.queryString = queryString;
        this.queryParams = new HashMap<>(queryParams);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getQueryString() {
        return queryString;
    }

    public Map<String, String> getQueryParams() {
        return new HashMap<>(queryParams);
    }

    public String getQueryParam(String name, String defaultValue) {
        return queryParams.getOrDefault(name, defaultValue);
    }
}
