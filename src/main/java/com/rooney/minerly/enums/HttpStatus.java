package com.rooney.minerly.enums;

public enum HttpStatus {
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    SUCCESS(200, "Success");

    private final int statusCode;
    private final String text;

    HttpStatus(int statusCode, String text) {
        this.statusCode = statusCode;
        this.text = text;
    }

    public int getCode() {
        return this.statusCode;
    }

    public String getText() {return this.text;}

    public static HttpStatus fromStatusCode(int statusCode) {
        for (HttpStatus status : HttpStatus.values()) {
            if (status.statusCode == statusCode) {
                return status;
            }
        }
        return null;
    }
}
