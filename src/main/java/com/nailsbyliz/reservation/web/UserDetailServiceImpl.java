package com.nailsbyliz.reservation.web;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.nailsbyliz.reservation.config.authtoken.CustomAuthToken;
import com.nailsbyliz.reservation.config.authtoken.CustomUserDetails;
import com.nailsbyliz.reservation.domain.AppUserEntity;
import com.nailsbyliz.reservation.repositories.AppUserRepository;

@Service
public class UserDetailServiceImpl implements UserDetailsService {
    private final AppUserRepository userRepository;

    public UserDetailServiceImpl(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUserEntity curruser = userRepository.findByUsername(username);
        if (curruser == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new CustomUserDetails(
                curruser.getUsername(),
                curruser.getPasswordHash(),
                AuthorityUtils.createAuthorityList(curruser.getRole()),
                curruser.getId(),
                curruser.getFName(),
                curruser.getLName(),
                curruser.getPhone(),
                curruser.getEmail(),
                curruser.getAddress(),
                curruser.getPostalcode(),
                curruser.getCity(),
                curruser.getRole());
    }

    public long getAuthIdentity() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof CustomAuthToken) {
            CustomAuthToken customToken = (CustomAuthToken) auth;
            return customToken.getUserid();
        } else {
            // Handle anonymous authentication
            return -1; // Or any other appropriate value indicating anonymous user
        }
    }

    public AppUserEntity getAuthUser() {
        long userId = getAuthIdentity();
        if (userId != -1) {
            Optional<AppUserEntity> user = userRepository.findById(userId);
            return user.orElse(null);
        } else {
            // Handle anonymous user case
            return null; // Or any other appropriate action
        }
    }
}