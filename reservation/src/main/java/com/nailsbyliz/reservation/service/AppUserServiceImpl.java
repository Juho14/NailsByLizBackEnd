package com.nailsbyliz.reservation.service;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nailsbyliz.reservation.domain.AppUserEntity;
import com.nailsbyliz.reservation.repositories.AppUserRepository;

@Service
public class AppUserServiceImpl implements AppUserService {

    @Autowired
    AppUserRepository userRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public AppUserEntity getUserById(Long userId) {
        Optional<AppUserEntity> optionalUser = userRepo.findById(userId);
        return optionalUser.orElse(null);
    }

    @Override
    public AppUserEntity createUser(AppUserEntity user) {
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        return userRepo.save(user);
    }

    @Override
    public AppUserEntity updateUser(Long userId, AppUserEntity updatedUser) {
        Optional<AppUserEntity> optionalExistingUser = userRepo.findById(userId);

        if (optionalExistingUser.isPresent()) {
            AppUserEntity existingUser = optionalExistingUser.get();

            existingUser.setFName(updatedUser.getFName());
            existingUser.setLName(updatedUser.getLName());
            existingUser.setPhone(updatedUser.getPhone());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setUsername(updatedUser.getUsername());
            existingUser.setRole(updatedUser.getRole());
            return userRepo.save(existingUser);
        } else {
            throw new NoSuchElementException("Reservation not found with id: " + userId);
        }
    }

    @Override
    public AppUserEntity changePassword(Long userId, AppUserEntity updatedUser) {

        Optional<AppUserEntity> optionalExistingUser = userRepo.findById(userId);

        if (optionalExistingUser.isPresent()) {
            AppUserEntity existingUser = optionalExistingUser.get();
            existingUser.setPasswordHash(passwordEncoder.encode(updatedUser.getPasswordHash()));
            return userRepo.save(existingUser);
        } else {

            return null;
        }
    }

    @Override
    public boolean deleteUser(Long userId) {
        Optional<AppUserEntity> userOptional = userRepo.findById(userId);

        if (userOptional.isPresent()) {
            AppUserEntity user = userOptional.get();
            userRepo.delete(user);
            return true;
        } else {
            return false;
        }
    }

}
