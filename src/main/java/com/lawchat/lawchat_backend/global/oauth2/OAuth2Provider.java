package com.lawchat.lawchat_backend.global.oauth2;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OAuth2Provider {
    GOOGLE("google");

    private final String registrationId;

    public static OAuth2Provider byRegistrationId(String registrationId) {
        for (OAuth2Provider provider : values()) {
            if (provider.registrationId.equals(registrationId)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 OAuth2 제공자입니다: " + registrationId);
    }
}