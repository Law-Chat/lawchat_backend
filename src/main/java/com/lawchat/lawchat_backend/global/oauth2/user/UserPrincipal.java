package com.lawchat.lawchat_backend.global.oauth2.user;

import com.lawchat.lawchat_backend.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
@AllArgsConstructor
public class UserPrincipal implements OAuth2User, UserDetails {

    private Long id;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    public static UserPrincipal create(User user) {
        return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                null,
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getKey())),
                null
        );
    }

    public static UserPrincipal create(User user, Map<String, Object> attributes) {
        UserPrincipal userPrincipal = create(user);
        return new UserPrincipal(
                userPrincipal.getId(),
                userPrincipal.getEmail(),
                userPrincipal.getPassword(),
                userPrincipal.getAuthorities(),
                attributes
        );
    }

    @Override
    public String getPassword() {
        return password;
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

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return String.valueOf(id);
    }
}
