package com.lawchat.lawchat_backend.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "OAuth2", description = "OAuth2 로그인 테스트 API")
@Slf4j
@RestController
public class OAuth2TestController {

    @Operation(summary = "OAuth2 리다이렉트 처리", description = "OAuth2 로그인 성공 후 리다이렉트되는 엔드포인트입니다. JWT 토큰을 반환합니다.")
    @GetMapping("/oauth2/redirect")
    public Map<String, String> oauth2Redirect(
            @Parameter(description = "JWT 토큰", example = "eyJhbGciOiJIUzUxMiJ9...")
            @RequestParam(required = false) String token) {
        log.info("OAuth2 Redirect - Token received: {}", token != null ? "Yes" : "No");

        Map<String, String> response = new HashMap<>();
        response.put("message", "OAuth2 로그인 성공!");
        response.put("token", token != null ? token : "토큰 없음");
        response.put("instruction", "이 토큰을 Authorization 헤더에 'Bearer <token>' 형식으로 추가하여 /api/auth/me를 호출하세요.");

        return response;
    }
}
