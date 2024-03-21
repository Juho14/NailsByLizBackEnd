package com.nailsbyliz.reservation.web;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

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

import com.nailsbyliz.reservation.domain.ReservationEntity;
import com.nailsbyliz.reservation.domain.ReservationRepository;
import com.nailsbyliz.reservation.dto.ReservationAdminDTO;
import com.nailsbyliz.reservation.dto.ReservationUserDTO;
import com.nailsbyliz.reservation.service.AuthService;
import com.nailsbyliz.reservation.service.ReservationService;

@RestController
@RequestMapping("/api/reservations")
public class ReservationRestController {

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    ReservationService reservationService;

    @Autowired
    AuthService authService;

    // To show all reservations
    @GetMapping
    public ResponseEntity<?> getReservations(Principal principal) {
        boolean isAdmin = false;

        // Logic to determine if the user is an admin based on principal
        if (principal != null) {
            String role = authService.getCurrentUser().getRole();
            if (role.equalsIgnoreCase("admin")) {
                isAdmin = true;
            }
        }

        Iterable<ReservationEntity> reservations = reservationRepository.findAll();
        List<?> response;

        if (isAdmin) {
            response = mapToAdminDTOs(reservations);
        } else {
            response = mapToUserDTOs(reservations);
        }

        return ResponseEntity.ok(response);
    }

    private List<ReservationUserDTO> mapToUserDTOs(Iterable<ReservationEntity> reservations) {
        List<ReservationUserDTO> dtos = new ArrayList<>();
        for (ReservationEntity reservation : reservations) {
            ReservationUserDTO dto = new ReservationUserDTO();
            dto.setId(reservation.getId());
            dto.setStartTime(reservation.getStartTime());
            dto.setEndTime(reservation.getEndTime());
            dto.setStatus(reservation.getStatus());
            dtos.add(dto);
        }
        return dtos;
    }

    private List<ReservationAdminDTO> mapToAdminDTOs(Iterable<ReservationEntity> reservations) {
        List<ReservationAdminDTO> dtos = new ArrayList<>();
        for (ReservationEntity reservation : reservations) {
            ReservationAdminDTO dto = new ReservationAdminDTO();
            dto.setId(reservation.getId());
            dto.setFName(reservation.getFName());
            dto.setLName(reservation.getLName());
            dto.setEmail(reservation.getEmail());
            dto.setPhone(reservation.getPhone());
            dto.setStartTime(reservation.getStartTime());
            dto.setEndTime(reservation.getEndTime());
            dto.setNailService(reservation.getNailService());
            dto.setStatus(reservation.getStatus());
            dtos.add(dto);
        }
        return dtos;
    }

    // To show a specific reservation
    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationEntity> getReservationById(@PathVariable Long reservationId) {
        ReservationEntity reservation = reservationService.getReservationById(reservationId);

        if (reservation != null) {
            return ResponseEntity.ok(reservation);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Create a new reservation
    @PostMapping
    public ResponseEntity<ReservationEntity> newReservation(@RequestBody ReservationEntity reservation) {
        ReservationEntity createdReservation = reservationService.saveReservation(reservation);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReservation);
    }

    @PutMapping("/{reservationId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReservationEntity> updateReservation(@PathVariable Long reservationId,
            @RequestBody ReservationEntity updatedReservation) {
        ReservationEntity result = reservationService.updateReservation(reservationId, updatedReservation);
        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long reservationId) {
        boolean deleted = reservationService.deleteReservation(reservationId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

}
