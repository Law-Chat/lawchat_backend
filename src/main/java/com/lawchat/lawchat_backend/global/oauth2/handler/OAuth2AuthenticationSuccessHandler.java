package com.lawchat.lawchat_backend.global.oauth2.handler;

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

    @Value("${app.oauth2.authorized-redirect-uris}")
    private String[] authorizedRedirectUris;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            log.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                       Authentication authentication) {
        // 개발 환경에서는 백엔드 URL 사용 (authorizedRedirectUris[1])
        // 프로덕션에서는 프론트엔드 URL 사용 (authorizedRedirectUris[0])
        String targetUrl = authorizedRedirectUris.length > 1 ? authorizedRedirectUris[1] : authorizedRedirectUris[0];

        String token = tokenProvider.generateToken(authentication);

        log.info("OAuth2 Login Success - Redirecting to: {} with token", targetUrl);

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", token)
                .build().toUriString();
    }
}
