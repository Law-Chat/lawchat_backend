package com.lawchat.lawchat_backend.global.oauth2.service;

import com.lawchat.lawchat_backend.global.oauth2.user.OAuth2UserInfo;
import com.lawchat.lawchat_backend.global.oauth2.user.OAuth2UserInfoFactory;
import com.lawchat.lawchat_backend.global.oauth2.user.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // ① 부모 클래스(DefaultOAuth2UserService)가 사용자 정보 조회
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // ② 제공자 식별 (google)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // ③ 원시 속성(Map)을 OAuth2UserInfo로 변환
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.create(
                registrationId,
                oAuth2User.getAttributes()
        );

        // ④ UserPrincipal 생성 및 반환 (사용자 저장은 SuccessHandler에서!)
        return UserPrincipal.fromOAuth2UserInfo(oAuth2UserInfo);
    }
}