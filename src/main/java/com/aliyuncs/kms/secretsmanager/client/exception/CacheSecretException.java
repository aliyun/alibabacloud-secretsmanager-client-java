package com.aliyuncs.kms.secretsmanager.client.exception;

public class CacheSecretException extends Exception {
    public CacheSecretException() {
        super();
    }

    public CacheSecretException(String message) {
        super(message);
    }

    public CacheSecretException(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheSecretException(Throwable cause) {
        super(cause);
    }

    protected CacheSecretException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
