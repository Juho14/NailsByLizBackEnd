package com.nailsbyliz.reservation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.nailsbyliz.reservation.domain.AppUserEntity;
import com.nailsbyliz.reservation.repositories.AppUserRepository;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AppUserRepository userRepo;

    @Override
    public AppUserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                return userRepo.findByUsername(userDetails.getUsername());
            }
        }
        return null;
    }

    @Override
    public boolean isAdmin() {
        try {
            AppUserEntity user = getCurrentUser();
            return user.getRole().equalsIgnoreCase("ROLE_ADMIN");
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String login() {
        return "login";
    }
}