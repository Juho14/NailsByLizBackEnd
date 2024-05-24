package com.nailsbyliz.reservation.config.authtoken;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class CustomAuthToken extends UsernamePasswordAuthenticationToken {
    private static final long serialVersionUID = 1L;
    private long userId;

    public CustomAuthToken(String principal, Object credentials,
            Collection<? extends GrantedAuthority> authorities, long userId) {
        super(principal, credentials, authorities);
        this.userId = userId;
    }

    public long getUserid() {
        return userId;
    }

    @Override
    public String toString() {
        return "CustomAuthenticationToken [Principal=" + getPrincipal() + ", Credentials=[PROTECTED], Authenticated="
                + isAuthenticated() + ", Details=" + getDetails() + ", Granted Authorities=" + getAuthorities()
                + ", User id=" + userId + "]";
    }

}