package com.nailsbyliz.reservation.web;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
import com.nailsbyliz.reservation.config.authtoken.TokenValidationInterceptor;
import com.nailsbyliz.reservation.domain.NailServiceEntity;
import com.nailsbyliz.reservation.domain.ReservationEntity;
import com.nailsbyliz.reservation.dto.NailServiceCustomerDTO;
import com.nailsbyliz.reservation.dto.ReservationAdminDTO;
import com.nailsbyliz.reservation.dto.ReservationCustomerDTO;
import com.nailsbyliz.reservation.dto.ReservationUserDTO;
import com.nailsbyliz.reservation.email.EmailLogic;
import com.nailsbyliz.reservation.repositories.ReservationRepository;
import com.nailsbyliz.reservation.service.AuthService;
import com.nailsbyliz.reservation.service.ReservationService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/reservations")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class ReservationRestController {

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    ReservationService reservationService;

    @Autowired
    AuthService authService;

    @Autowired
    JwtService jwtService;

    @Autowired
    UserDetailServiceImpl userService;

    @Autowired
    TokenValidationInterceptor tokenValidationInterceptor;

    // To show all reservations
    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> getAllReservations(HttpServletRequest request) {
        String token = jwtService.resolveAuthToken(request);
        String userRole = jwtService.getRoleFromToken(token);

        Iterable<ReservationEntity> reservations;
        List<?> response;

        if (userRole != null && "ROLE_ADMIN".equals(userRole)) {
            reservations = reservationRepository.findAll();
            response = mapToAdminDTOs(reservations);
        } else {
            reservations = reservationRepository.findAll();
            response = mapToCustomerDTOs(reservations);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/myreservations")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> getReservationsForUser(HttpServletRequest request) {
        // Extract the user ID from the request attribute set by the interceptor
        String token = jwtService.resolveAuthToken(request);
        Long currUserId = jwtService.getIdFromToken(token);
        // Check if the customer ID is not null
        if (currUserId != null) {
            Iterable<ReservationEntity> reservations = reservationRepository.findByCustomerId(currUserId);
            List<?> response = mapToUserDTOs(reservations);
            return ResponseEntity.ok(response);
        } else {
            // If the customer ID is null, return UNAUTHORIZED status
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getReservationsForUser(HttpServletRequest request, @PathVariable Long customerId) {
        String token = jwtService.resolveAuthToken(request);
        String userRole = jwtService.getRoleFromToken(token);

        if (!"ROLE_ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        Iterable<ReservationEntity> reservations = reservationRepository.findByCustomerId(customerId);
        List<?> response = mapToAdminDTOs(reservations);

        return ResponseEntity.ok(response);
    }

    private List<ReservationCustomerDTO> mapToCustomerDTOs(Iterable<ReservationEntity> reservations) {
        List<ReservationCustomerDTO> dtos = new ArrayList<>();
        for (ReservationEntity reservation : reservations) {
            ReservationCustomerDTO dto = new ReservationCustomerDTO();
            dto.setId(reservation.getId());
            dto.setStartTime(reservation.getStartTime());
            dto.setEndTime(reservation.getEndTime());
            if (!reservation.getStatus().equalsIgnoreCase("ok")) {
                continue;
            }
            dto.setStatus(reservation.getStatus());
            dtos.add(dto);
        }
        return dtos;

    }

    private NailServiceCustomerDTO mapServiceToCustomerDTO(NailServiceEntity service) {
        NailServiceCustomerDTO dto = new NailServiceCustomerDTO();
        dto.setId(service.getId());
        dto.setType(service.getType());
        dto.setDuration(service.getDuration());
        return dto;
    }

    private List<ReservationUserDTO> mapToUserDTOs(Iterable<ReservationEntity> reservations) {
        List<ReservationUserDTO> dtos = new ArrayList<>();
        LocalDateTime dayBefore = LocalDateTime.now().minusDays(1);
        for (ReservationEntity reservation : reservations) {
            if (!reservation.getStatus().equalsIgnoreCase("ok")) {
                continue;
            }
            LocalDateTime startTime = reservation.getStartTime();
            // Check if the startTime is within the acceptable range
            if (startTime.isBefore(dayBefore)) {
                continue;
            }
            ReservationUserDTO dto = new ReservationUserDTO();
            dto.setId(reservation.getId());
            dto.setStartTime(startTime);
            dto.setEndTime(reservation.getEndTime());
            dto.setFName(reservation.getFName());
            dto.setLName(reservation.getLName());
            dto.setPrice(reservation.getPrice());
            NailServiceCustomerDTO nailServiceDto = mapServiceToCustomerDTO(reservation.getNailService());
            if (nailServiceDto != null) {
                dto.setNailService(nailServiceDto);
            }
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
            dto.setAddress(reservation.getAddress());
            dto.setCity(reservation.getCity());
            dto.setPostalcode(reservation.getPostalcode());
            dto.setPrice(reservation.getPrice());
            dto.setStartTime(reservation.getStartTime());
            dto.setEndTime(reservation.getEndTime());
            dto.setNailService(reservation.getNailService());
            dto.setStatus(reservation.getStatus());
            dto.setCustomerId(reservation.getCustomerId());
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

    // End point to fetch reservations of a given date
    @GetMapping("/byday/{day}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> getReservationsByDay(
            @PathVariable("day") @DateTimeFormat(pattern = "yyyy-MM-dd") Date day,
            HttpServletRequest request) {

        String token = jwtService.resolveAuthToken(request);
        String userRole = "";
        if (token != null) {
            userRole = jwtService.getRoleFromToken(token);
        }
        // Check if the user is an admin
        if ("ROLE_ADMIN".equals(userRole)) {
            Iterable<ReservationEntity> reservations = reservationService.getReservationsByDay(day);
            List<?> response = mapToAdminDTOs(reservations);
            return ResponseEntity.ok(response);
        } else {
            // Return data for regular users
            Iterable<ReservationEntity> reservations = reservationService.getReservationsByDay(day);
            List<?> response = mapToCustomerDTOs(reservations);
            return ResponseEntity.ok(response);
        }
    }

    // End point to fetch all reservations of a week. Used for rendering timeslots
    @GetMapping("/byweek/{day}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> getReservationsByWeek(
            @PathVariable("day") @DateTimeFormat(pattern = "yyyy-MM-dd") Date day,
            HttpServletRequest request) {
        String userRole = "";
        String token = jwtService.resolveAuthToken(request);
        if (token != null) {
            userRole = "";
            if (token != null) {
                userRole = jwtService.getRoleFromToken(token);
            }
        }
        // Check if the user is an admin
        if ("ROLE_ADMIN".equals(userRole)) {
            Iterable<ReservationEntity> reservations = reservationService.getReservationsForWeek(day);
            List<?> response = mapToAdminDTOs(reservations);
            return ResponseEntity.ok(response);
        } else {
            // Return data for regular users
            Iterable<ReservationEntity> reservations = reservationService.getReservationsForWeek(day);
            List<?> response = mapToCustomerDTOs(reservations);
            return ResponseEntity.ok(response);
        }
    }

    // Create a new reservation
    @PostMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<ReservationEntity> newReservation(@RequestBody ReservationEntity reservation,
            HttpServletRequest request) {
        String token = jwtService.resolveAuthToken(request);
        String userRole = "";
        if (token != null) {
            userRole = jwtService.getRoleFromToken(token);
        }
        ReservationEntity createdReservation = reservationService.newReservation(reservation, userRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReservation);
    }

    @PutMapping("/{reservationId}")
    public ResponseEntity<?> updateReservation(@PathVariable Long reservationId,
            @RequestBody ReservationEntity updatedReservation, HttpServletRequest request) {

        String token = jwtService.resolveAuthToken(request);
        String userRole = jwtService.getRoleFromToken(token);

        if (!userRole.equals("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
        ReservationEntity result = reservationService.updateReservation(reservationId, updatedReservation);
        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/cancel/{reservationId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<String> cancelReservation(@PathVariable Long reservationId, HttpServletRequest request) {

        String token = jwtService.resolveAuthToken(request);
        Long userId = jwtService.getIdFromToken(token);
        String userRole = jwtService.getRoleFromToken(token);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }

        ReservationEntity reservation = reservationService.getReservationById(reservationId);

        if (reservation == null) {
            return ResponseEntity.notFound().build();
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = reservation.getStartTime();
        Duration duration = Duration.between(now, startTime);

        if (duration.toHours() < 24) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Reservation cannot be canceled less than 24 hours before the start time.");
        }

        if (!"ROLE_ADMIN".equalsIgnoreCase(userRole)) {
            if (!reservation.getCustomerId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
            }
        }

        reservation.setStatus("Peruttu");
        reservationRepository.save(reservation);
        EmailLogic.sendCancelledReservationEmail(reservation);

        return ResponseEntity.ok(reservation.toString());
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<?> deleteReservation(HttpServletRequest request, @PathVariable Long reservationId) {
        String token = jwtService.resolveAuthToken(request);
        String userRole = jwtService.getRoleFromToken(token);
        if (!userRole.equals("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
        boolean deleted = reservationService.deleteReservation(reservationId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

}
