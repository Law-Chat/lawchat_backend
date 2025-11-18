package com.lawchat.lawchat_backend.global.oauth2.user;

import com.lawchat.lawchat_backend.global.oauth2.OAuth2Provider;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo create(String registrationId, Map<String, Object> attributes) {
        OAuth2Provider provider = OAuth2Provider.byRegistrationId(registrationId);

        switch (provider) {
            case GOOGLE:
                return GoogleOAuth2UserInfo.from(attributes);
            default:
                throw new IllegalArgumentException("지원하지 않는 OAuth2 제공자입니다: " + registrationId);
        }
    }
}