package com.epam.finaltask.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ApiResponse {
    private String statusCode;
    private String statusMessage;
    private Object data;

    public ApiResponse(String statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public ApiResponse(String statusCode, String statusMessage, Object data) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.data = data;
    }

}
