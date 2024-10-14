package com.sharmachait.model.response;

public class ApiResponse {
    public ApiResponse(Object data, String message) {
        this.data=data;
        this.message=message;
    }
    private Object data;
    private String message;
}
