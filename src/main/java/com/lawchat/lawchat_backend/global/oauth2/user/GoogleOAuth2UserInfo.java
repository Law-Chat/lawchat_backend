package com.lawchat.lawchat_backend.global.oauth2.user;

import com.lawchat.lawchat_backend.global.oauth2.OAuth2Provider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor(staticName = "from")
public class GoogleOAuth2UserInfo implements OAuth2UserInfo {
    private final Map<String, Object> attributes;

    @Override
    public String getEmail() {
        String email = (String) attributes.get("email");
        return email != null ? email : FALLBACK_EMAIL;
    }

    @Override
    public String getName() {
        String name = (String) attributes.get("name");
        return name != null ? name : FALLBACK_NAME;
    }

    @Override
    public OAuth2Provider getProvider() {
        return OAuth2Provider.GOOGLE;
    }

    @Override
    public String getSocialId() {
        return (String) attributes.get("sub");
    }
}