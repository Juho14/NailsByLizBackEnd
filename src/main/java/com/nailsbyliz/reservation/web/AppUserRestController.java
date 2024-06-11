package com.nailsbyliz.reservation.web;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nailsbyliz.reservation.config.authtoken.JwtService;
import com.nailsbyliz.reservation.domain.AppUserEntity;
import com.nailsbyliz.reservation.repositories.AppUserRepository;
import com.nailsbyliz.reservation.service.AppUserService;
import com.nailsbyliz.reservation.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/users")
// @PreAuthorize("hasRole('ROLE_ADMIN')")
public class AppUserRestController {

    @Autowired
    AppUserRepository userRepo;

    @Autowired
    AppUserService userService;

    @Autowired
    AuthService authService;

    @Autowired
    JwtService jwtService;

    // Get all users
    @GetMapping
    public ResponseEntity<Iterable<AppUserEntity>> get() {
        Iterable<AppUserEntity> appUsers = userRepo.findAll();
        return ResponseEntity.ok(appUsers);
    }

    // Get admins
    @GetMapping("/admins")
    public ResponseEntity<Iterable<AppUserEntity>> getAdmins() {
        Iterable<AppUserEntity> admins = userRepo.findByRole("ROLE_ADMIN");
        return ResponseEntity.ok(admins);
    }

    // Get a specific user
    @GetMapping("/{userId}")
    public ResponseEntity<AppUserEntity> getaAppUserById(@PathVariable Long userId) {
        AppUserEntity appUser = userService.getUserById(userId);
        if (appUser != null) {
            return ResponseEntity.ok(appUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Create a new user
    @PostMapping
    public ResponseEntity<AppUserEntity> createAppUser(@RequestBody AppUserEntity createdAppUser) {
        AppUserEntity creaAppUser = userService.createUser(createdAppUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(creaAppUser);
    }

    // Edit a user
    @PutMapping("/{userId}")
    public ResponseEntity<AppUserEntity> updateAppUser(@PathVariable Long userId,
            @RequestBody AppUserEntity updatedAppUser) {
        AppUserEntity result = userService.updateUser(userId, updatedAppUser);

        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete a user
    @DeleteMapping
    public ResponseEntity<Void> deleteAppUser(@RequestHeader("Authorization") String token) {
        Long userId = jwtService.getIdFromToken(token);
        boolean deleted = userService.deleteUser(userId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // Delete a specific
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteAppUser(HttpServletRequest request, @PathVariable Long userId) {
        String userRole = (String) request.getAttribute("userRole");

        if ("ROLE_ADMIN".equals(userRole)) {
            Optional<AppUserEntity> deletedUserOptional = userRepo.findById(userId);
            AppUserEntity deletedUser = null;
            String deletedUserRole = "";
            if (deletedUserOptional.isPresent()) {
                deletedUser = deletedUserOptional.get();
                deletedUserRole = deletedUser.getRole();
            }
            // Token is valid
            if (deletedUserRole == "ROLE_ADMIN") {
                Iterable<AppUserEntity> admins = userRepo.findByRole("ROLE_ADMIN");
                List<AppUserEntity> adminList = StreamSupport.stream(admins.spliterator(), false)
                        .collect(Collectors.toList());

                int adminCount = adminList.size();

                if (adminCount < 2) {
                    return ResponseEntity.badRequest().build();
                }
            }

            boolean deleted = userService.deleteUser(userId);
            return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Change password
    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(@RequestBody AppUserEntity updatedUser,
            HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        if (userId != updatedUser.getId()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid ID");
        }
        AppUserEntity user = userService.changePassword(userId, updatedUser);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}