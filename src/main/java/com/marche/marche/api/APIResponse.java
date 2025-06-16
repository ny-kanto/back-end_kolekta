package com.marche.marche.api;

public class APIResponse {
    String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    Object data;

    public APIResponse(String error) {
        this.error = error;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public APIResponse(String string, Object object) {
        this.setError(string);
        this.setData(object);
    }
}