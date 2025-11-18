package com.lawchat.lawchat_backend.global.oauth2.user;

import com.lawchat.lawchat_backend.global.oauth2.OAuth2Provider;

import java.util.Map;

public interface OAuth2UserInfo {
    String getEmail();
    String getName();
    OAuth2Provider getProvider();
    String getSocialId();
    Map<String, Object> getAttributes();

    String FALLBACK_EMAIL = "no-email@lawchat.com";
    String FALLBACK_NAME = "Unknown";
}