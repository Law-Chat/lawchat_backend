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
        User user = userRepository
                .findBySocialProviderAndSocialId(provider, socialId)
                .orElseGet(() -> {
                    log.info("New user registration - email: {}", email);
                    // 첫 로그인 -> 자동 회원가입
                    User newUser = User.builder()
                            .name(name)
                            .email(email)
                            .socialProvider(provider)
                            .socialId(socialId)
                            .role(Role.USER)
                            .build();
                    return userRepository.save(newUser);
                });

        // ③ JWT 액세스 토큰 발급 (24시간)
        String accessToken = tokenProvider.generateToken(user);

        // ④ Flutter Deep Link로 리다이렉트
        String targetUrl = getRedirectUrl(oauthRedirectUri, accessToken);

        if (response.isCommitted()) {
            log.error("Response has already been committed");
            return;
        }

        log.info("Redirecting to: {}", targetUrl);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
        log.info("=== OAuth2 Authentication Success Handler Completed ===");
    }

    private String getRedirectUrl(String targetUrl, String token) {
        return UriComponentsBuilder
                .fromUriString(targetUrl)
                .queryParam("token", token)
                .build()
                .toUriString();
    }
}