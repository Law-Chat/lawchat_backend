package com.lawchat.lawchat_backend.domain.auth.dto;

import com.lawchat.lawchat_backend.domain.user.entity.AuthProvider;
import com.lawchat.lawchat_backend.domain.user.entity.Role;
import com.lawchat.lawchat_backend.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사용자 정보 응답")
public class UserResponse {

    @Schema(description = "사용자 ID", example = "1")
    private Long id;

    @Schema(description = "사용자 이름", example = "홍길동")
    private String name;

    @Schema(description = "이메일 주소", example = "hong@example.com")
    private String email;

    @Schema(description = "OAuth2 제공자", example = "GOOGLE")
    private AuthProvider provider;

    @Schema(description = "사용자 권한", example = "USER")
    private Role role;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .provider(user.getProvider())
                .role(user.getRole())
                .build();
    }
}
