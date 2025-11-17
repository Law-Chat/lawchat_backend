package com.lawchat.lawchat_backend.global.oauth2.service;

import com.lawchat.lawchat_backend.domain.user.entity.AuthProvider;
import com.lawchat.lawchat_backend.domain.user.entity.Role;
import com.lawchat.lawchat_backend.domain.user.entity.User;
import com.lawchat.lawchat_backend.domain.user.repository.UserRepository;
import com.lawchat.lawchat_backend.global.exception.ErrorCode;
import com.lawchat.lawchat_backend.global.exception.OAuth2AuthenticationProcessingException;
import com.lawchat.lawchat_backend.global.oauth2.user.OAuth2UserInfo;
import com.lawchat.lawchat_backend.global.oauth2.user.OAuth2UserInfoFactory;
import com.lawchat.lawchat_backend.global.oauth2.user.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        return processOAuth2User(userRequest, oAuth2User);
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                registrationId,
                oAuth2User.getAttributes()
        );

        if (!StringUtils.hasText(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException(ErrorCode.OAUTH2_EMAIL_NOT_FOUND);
        }

        User user = userRepository.findByEmail(oAuth2UserInfo.getEmail())
                .map(existingUser -> updateExistingUser(existingUser, oAuth2UserInfo))
                .orElseGet(() -> registerNewUser(userRequest, oAuth2UserInfo));

        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    private User registerNewUser(OAuth2UserRequest userRequest, OAuth2UserInfo oAuth2UserInfo) {
        User user = User.builder()
                .name(oAuth2UserInfo.getName())
                .email(oAuth2UserInfo.getEmail())
                .provider(AuthProvider.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase()))
                .providerId(oAuth2UserInfo.getId())
                .role(Role.USER)
                .build();

        return userRepository.save(user);
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        return userRepository.save(
                existingUser.update(oAuth2UserInfo.getName())
        );
    }
}
