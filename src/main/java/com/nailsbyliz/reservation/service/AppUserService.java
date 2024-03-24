package com.nailsbyliz.reservation.service;

import com.nailsbyliz.reservation.domain.AppUserEntity;

public interface AppUserService {

    AppUserEntity getUserById(Long userId);

    AppUserEntity createUser(AppUserEntity user);

    AppUserEntity updateUser(Long userId, AppUserEntity updatedUser);

    boolean deleteUser(Long userId);

    AppUserEntity changePassword(Long userId, AppUserEntity updatedUser);
}