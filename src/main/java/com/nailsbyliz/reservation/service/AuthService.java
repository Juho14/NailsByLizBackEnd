package com.nailsbyliz.reservation.service;

import com.nailsbyliz.reservation.domain.AppUserEntity;

public interface AuthService {
    AppUserEntity getCurrentUser();

    String login();

    boolean isAdmin();
}