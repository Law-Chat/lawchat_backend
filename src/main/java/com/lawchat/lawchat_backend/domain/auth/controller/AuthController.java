package com.lawchat.lawchat_backend.domain.auth.controller;

import com.lawchat.lawchat_backend.domain.auth.dto.UserResponse;
import com.lawchat.lawchat_backend.domain.user.entity.User;
import com.lawchat.lawchat_backend.domain.user.repository.UserRepository;
import com.lawchat.lawchat_backend.global.exception.UserNotFoundException;
import com.lawchat.lawchat_backend.global.oauth2.user.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "인증", description = "인증 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;

    @Operation(summary = "현재 로그인한 사용자 정보 조회", description = "JWT 토큰을 통해 현재 로그인한 사용자의 정보를 조회합니다.")
    @GetMapping("/me")
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<UserResponse> getCurrentUser(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(UserNotFoundException::new);

        return ResponseEntity.ok(UserResponse.from(user));
    }
}
