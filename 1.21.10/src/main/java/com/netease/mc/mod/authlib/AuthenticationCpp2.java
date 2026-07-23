package com.netease.mc.mod.authlib;

enum AuthenticationCpp2 {
    SUCCESS(0, "successfully authentication"),
    UNkNOWN_ERROR(-1, "unknown error in authentication"),
    SOCKET_FAILED(1, "Create socket failed"),
    CONNECT_FAILED(2, "Connect failed"),
    SEND_DATA_ERROR(3, "Send data Error!"),
    AUTHENTICATION_FAILED(4, "Authentication response failed"),
    URL_ERROR(5, "URL format failed"),
    CREATE_SSL_ERROR(6, "InitializeSslContext failed"),
    CONNECT_SSL_ERROR(7, "SslConnect failed");

    private final int code;
    private final String description;

    AuthenticationCpp2(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public int getCode() {
        return this.code;
    }

    public static AuthenticationCpp2 GetErrorCode(int code) {
        for (AuthenticationCpp2 error : values()) {
            if (error.getCode() == code) {
                return error;
            }
        }
        return UNkNOWN_ERROR;
    }
}
