package com.lawchat.lawchat_backend.global.exception;

public class OAuth2AuthenticationProcessingException extends BusinessException {

    public OAuth2AuthenticationProcessingException(ErrorCode errorCode) {
        super(errorCode);
    }

    public OAuth2AuthenticationProcessingException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
