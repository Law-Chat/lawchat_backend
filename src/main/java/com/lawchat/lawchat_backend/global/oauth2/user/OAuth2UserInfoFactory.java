package com.lawchat.lawchat_backend.global.oauth2.user;

import com.lawchat.lawchat_backend.domain.user.entity.AuthProvider;
import com.lawchat.lawchat_backend.global.exception.ErrorCode;
import com.lawchat.lawchat_backend.global.exception.OAuth2AuthenticationProcessingException;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase(AuthProvider.GOOGLE.toString())) {
            return new GoogleOAuth2UserInfo(attributes);
        } else {
            throw new OAuth2AuthenticationProcessingException(
                    ErrorCode.OAUTH2_PROVIDER_NOT_SUPPORTED,
                    "지원하지 않는 OAuth2 Provider입니다: " + registrationId
            );
        }
    }
}
