package com.nailsbyliz.reservation.web;

import java.time.LocalTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nailsbyliz.reservation.config.authtoken.JwtService;
import com.nailsbyliz.reservation.domain.ReservationSettings;
import com.nailsbyliz.reservation.repositories.ReservationSettingsRepository;
import com.nailsbyliz.reservation.service.ReservationSettingsService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/reservationsettings")
@PreAuthorize("hasRole('ADMIN')")
public class ReservationSettingsRestController {

    @Autowired
    ReservationSettingsRepository reservationSettingsRepository;

    @Autowired
    ReservationSettingsService settingService;

    @Autowired
    JwtService jwtService;

    // READ all
    @GetMapping
    public ResponseEntity<Iterable<ReservationSettings>> getAllReservationSettings() {
        Iterable<ReservationSettings> reservationSettingsList = reservationSettingsRepository.findAll();
        return ResponseEntity.ok(reservationSettingsList);
    }

    // READ by id
    @GetMapping("/{id}")
    public ResponseEntity<ReservationSettings> getReservationSettingsById(@PathVariable Long id) {
        Optional<ReservationSettings> reservationSettingsOptional = reservationSettingsRepository.findById(id);
        return reservationSettingsOptional
                .map(reservationSettings -> new ResponseEntity<>(reservationSettings, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Only get the active reservation times
    @PutMapping("/activate/{id}")
    public ResponseEntity<?> activateReservation(@PathVariable Long id) {
        settingService.setActiveReservationSetting(id);
        return ResponseEntity.ok("Reservation activated successfully");
    }

    @GetMapping("/active")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> getActiveReservation() {
        ReservationSettings activeReservation = settingService.findActiveReservationSetting();

        if (activeReservation != null) {
            return ResponseEntity.ok(activeReservation);
        } else {
            ReservationSettings fallbackSetting = new ReservationSettings("Fallback Setting", LocalTime.of(11, 0),
                    LocalTime.of(18, 0), true);
            return ResponseEntity.ok(fallbackSetting);
        }
    }

    // CREATE
    @PostMapping
    public ResponseEntity<ReservationSettings> createReservationSettings(
            @RequestBody ReservationSettings reservationSettings, HttpServletRequest request) {
        String token = jwtService.resolveAuthToken(request);
        String userRole = jwtService.getRoleFromToken(token);
        System.out.println("Token: " + token);
        System.out.println("User Role: " + userRole);

        ReservationSettings savedReservationSettings = reservationSettingsRepository.save(reservationSettings);
        return new ResponseEntity<>(savedReservationSettings, HttpStatus.CREATED);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<ReservationSettings> updateReservationSettings(@PathVariable Long id,
            @RequestBody ReservationSettings updatedReservationSettings) {
        if (!reservationSettingsRepository.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        updatedReservationSettings.setId(id);
        ReservationSettings savedReservationSettings = reservationSettingsRepository.save(updatedReservationSettings);
        return new ResponseEntity<>(savedReservationSettings, HttpStatus.OK);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservationSettings(@PathVariable Long id) {
        if (!reservationSettingsRepository.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        reservationSettingsRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
