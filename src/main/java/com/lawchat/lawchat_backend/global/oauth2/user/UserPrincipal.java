package com.lawchat.lawchat_backend.global.oauth2.user;

import com.lawchat.lawchat_backend.domain.user.entity.User;
import com.lawchat.lawchat_backend.global.oauth2.OAuth2Provider;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class UserPrincipal implements UserDetails, OAuth2User {

    @Getter
    private final Long userId;

    private final String email;

    @Getter
    private final OAuth2UserInfo oAuth2UserInfo;

    @Getter
    private final Map<String, Object> attributes;

    // OAuth2 로그인 시 생성 (소셜 로그인 중)
    public static UserPrincipal fromOAuth2UserInfo(OAuth2UserInfo oAuth2UserInfo) {
        return new UserPrincipal(
                null,  // userId는 아직 모름
                oAuth2UserInfo.getEmail(),
                oAuth2UserInfo,
                oAuth2UserInfo.getAttributes()
        );
    }

    // DB 엔티티에서 생성 (JWT 인증 시)
    public static UserPrincipal fromEntity(User user) {
        return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                null,  // OAuth2 정보 불필요
                Map.of()
        );
    }

    // UserDetails 구현
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return "";  // 소셜 로그인이므로 비밀번호 없음
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // OAuth2User 구현
    @Override
    public String getName() {
        return Optional.ofNullable(oAuth2UserInfo)
                .map(OAuth2UserInfo::getName)
                .orElse("");
    }

    // 헬퍼 메서드
    public OAuth2Provider getOAuth2Provider() {
        return Optional.ofNullable(oAuth2UserInfo)
                .map(OAuth2UserInfo::getProvider)
                .orElse(null);
    }
}