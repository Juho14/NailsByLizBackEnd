package com.nailsbyliz.reservation.config.authtoken;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class CustomAuthToken extends UsernamePasswordAuthenticationToken {
    private static final long serialVersionUID = 1L;
    private long user_id;

    public CustomAuthToken(String principal, Object credentials,
            Collection<? extends GrantedAuthority> authorities, long user_id) {
        super(principal, credentials, authorities);
        this.user_id = user_id;
    }

    public long getUser_id() {
        return user_id;
    }

    @Override
    public String toString() {
        return "CustomAuthenticationToken [Principal=" + getPrincipal() + ", Credentials=[PROTECTED], Authenticated="
                + isAuthenticated() + ", Details=" + getDetails() + ", Granted Authorities=" + getAuthorities()
                + ", User id=" + user_id + "]";
    }

}