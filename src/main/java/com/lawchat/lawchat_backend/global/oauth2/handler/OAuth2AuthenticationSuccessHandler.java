package com.lawchat.lawchat_backend.global.oauth2.handler;

import com.lawchat.lawchat_backend.domain.user.entity.Role;
import com.lawchat.lawchat_backend.domain.user.entity.User;
import com.lawchat.lawchat_backend.domain.user.repository.UserRepository;
import com.lawchat.lawchat_backend.global.oauth2.OAuth2Provider;
import com.lawchat.lawchat_backend.global.oauth2.user.OAuth2UserInfo;
import com.lawchat.lawchat_backend.global.oauth2.user.UserPrincipal;
import com.lawchat.lawchat_backend.global.security.jwt.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;

    @Value("${app.oauth2.default-redirect-uri}")
    private String oauthRedirectUri;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        log.info("=== OAuth2 Authentication Success Handler Started ===");

        // ① UserPrincipal 추출
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        OAuth2UserInfo oAuth2UserInfo = userPrincipal.getOAuth2UserInfo();

        if (oAuth2UserInfo == null) {
            log.error("OAuth2UserInfo is null");
            throw new IllegalStateException("OAuth2UserInfo cannot be null");
        }

        String socialId = oAuth2UserInfo.getSocialId();
        OAuth2Provider provider = oAuth2UserInfo.getProvider();
        String email = oAuth2UserInfo.getEmail();
        String name = oAuth2UserInfo.getName();

        log.info("OAuth2 Login - provider: {}, email: {}", provider, email);

        // ② 사용자 조회 또는 생성
        User user;
        boolean isNewUser;

        var existingUser = userRepository.findBySocialProviderAndSocialId(provider, socialId);

        if (existingUser.isPresent()) {
            // 기존 회원
            user = existingUser.get();
            isNewUser = false;
            log.info("Existing user login - email: {}", email);
        } else {
            // 신규 회원 자동 가입
            log.info("New user registration - email: {}", email);
            user = User.builder()
                    .name(name)
                    .email(email)
                    .socialProvider(provider)
                    .socialId(socialId)
                    .role(Role.USER)
                    .build();
            user = userRepository.save(user);
            isNewUser = true;
        }

        // ③ JWT 액세스 토큰 발급 (24시간)
        String accessToken = tokenProvider.generateToken(user);

        // ④ Flutter Deep Link로 리다이렉트
        String targetUrl = getRedirectUrl(oauthRedirectUri, accessToken, isNewUser);

        if (response.isCommitted()) {
            log.error("Response has already been committed");
            return;
        }

        log.info("Redirecting to: {}", targetUrl);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
        log.info("=== OAuth2 Authentication Success Handler Completed ===");
    }

    private String getRedirectUrl(String targetUrl, String token, boolean isNewUser) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(targetUrl)
                .queryParam("token", token);

        // 신규 가입인 경우에만 isNewUser 파라미터 추가
        if (isNewUser) {
            builder.queryParam("isNewUser", true);
        }

        return builder.build().toUriString();
    }
}