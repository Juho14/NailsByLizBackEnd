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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nailsbyliz.reservation.config.authtoken.JwtService;
import com.nailsbyliz.reservation.domain.NailServiceEntity;
import com.nailsbyliz.reservation.domain.ReservationEntity;
import com.nailsbyliz.reservation.dto.NailServiceCustomerDTO;
import com.nailsbyliz.reservation.dto.ReservationAdminDTO;
import com.nailsbyliz.reservation.dto.ReservationCustomerDTO;
import com.nailsbyliz.reservation.dto.ReservationUserDTO;
import com.nailsbyliz.reservation.email.EmailBodyLogic;
import com.nailsbyliz.reservation.email.EmailSender;
import com.nailsbyliz.reservation.repositories.ReservationRepository;
import com.nailsbyliz.reservation.service.AuthService;
import com.nailsbyliz.reservation.service.ReservationService;
import com.nailsbyliz.reservation.util.TimeUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/reservations")
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

    // To show all reservations
    @GetMapping
    public ResponseEntity<?> getAllReservations(
            HttpServletRequest request) {

        // Extract the JWT token from the request headers
        String token = jwtService.resolveToken(request);

        // Check if the token is present
        if (token != null) {
            // Token is present, validate it
            if (jwtService.validateToken(token)) {
                // Token is valid
                String role = jwtService.getRoleFromToken(token);
                boolean isAdmin = "ROLE_ADMIN".equals(role);

                Iterable<ReservationEntity> reservations = reservationRepository.findAll();
                List<?> response;

                if (isAdmin) {
                    response = mapToAdminDTOs(reservations);
                } else {
                    response = mapToCustomerDTOs(reservations);
                }

                return ResponseEntity.ok(response);
            } else {
                // Token is invalid, return response indicating invalid token
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
            }
        } else {
            // Token is not present, return data for regular users
            Iterable<ReservationEntity> reservations = reservationRepository.findAll();
            List<?> response = mapToCustomerDTOs(reservations);
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/myreservations")
    public ResponseEntity<?> getReservationsForUser(HttpServletRequest request) {

        // Extract the JWT token from the request headers
        String token = jwtService.resolveToken(request);

        // Validate token
        if (jwtService.validateToken(token)) {
            Long customerId = jwtService.getIdFromToken(token);
            Iterable<ReservationEntity> reservations;
            reservations = reservationRepository.findByCustomerId(customerId);
            List<?> response = mapToUserDTOs(reservations);
            return ResponseEntity.ok(response);
        } else {
            // Token is invalid, return response indicating invalid token
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getReservationsForUser(HttpServletRequest request, @PathVariable Long customerId) {
        // Extract the JWT token from the request headers
        String token = jwtService.resolveToken(request);
        String role = jwtService.getRoleFromToken(token);
        boolean isAdmin = "ROLE_ADMIN".equals(role);

        // Validate token
        if (jwtService.validateToken(token) && isAdmin) {
            Iterable<ReservationEntity> reservations;
            reservations = reservationRepository.findByCustomerId(customerId);
            List<?> response = mapToAdminDTOs(reservations);
            return ResponseEntity.ok(response);
        } else {
            // Token is invalid, return response indicating invalid token
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
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
    public ResponseEntity<?> getReservationsByDayAUth(
            @PathVariable("day") @DateTimeFormat(pattern = "yyyy-MM-dd") Date day,
            HttpServletRequest request) {

        // Extract the JWT token from the request headers
        String token = jwtService.resolveToken(request);

        // Check if the token is present
        if (token != null) {
            // Token is present, validate it
            if (jwtService.validateToken(token)) {
                // Token is valid
                String role = jwtService.getRoleFromToken(token);
                boolean isAdmin = "ROLE_ADMIN".equals(role);

                Iterable<ReservationEntity> reservations = reservationService.getReservationsByDay(day);
                List<?> response;

                if (isAdmin) {
                    response = mapToAdminDTOs(reservations);
                } else {
                    response = mapToCustomerDTOs(reservations);
                }

                return ResponseEntity.ok(response);
            } else {
                // Token is invalid, return response indicating invalid token
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
            }
        } else {
            // Token is not present, return data for regular users
            Iterable<ReservationEntity> reservations = reservationService.getReservationsByDay(day);
            List<?> response = mapToCustomerDTOs(reservations);
            return ResponseEntity.ok(response);
        }
    }

    // End point to fetch all reservations of a week. Used for rendering timeslots
    @GetMapping("/byweek/{day}")
    public ResponseEntity<?> getReservationsByWeek(
            @PathVariable("day") @DateTimeFormat(pattern = "yyyy-MM-dd") Date day,
            HttpServletRequest request) {

        // Extract the JWT token from the request headers
        String token = jwtService.resolveToken(request);

        // Check if the token is present
        if (token != null) {
            // Token is present, validate it
            if (jwtService.validateToken(token)) {
                // Token is valid
                String role = jwtService.getRoleFromToken(token);
                boolean isAdmin = "ROLE_ADMIN".equals(role);

                Iterable<ReservationEntity> reservations = reservationService.getReservationsForWeek(day);
                List<?> response;

                if (isAdmin) {
                    response = mapToAdminDTOs(reservations);
                } else {
                    response = mapToCustomerDTOs(reservations);
                }

                return ResponseEntity.ok(response);
            } else {
                // Token is invalid, return response indicating invalid token
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
            }
        } else {
            // Token is not present, return data for regular users
            Iterable<ReservationEntity> reservations = reservationService.getReservationsForWeek(day);
            List<?> response = mapToCustomerDTOs(reservations);
            return ResponseEntity.ok(response);
        }
    }

    // Create a new reservation
    @PostMapping
    public ResponseEntity<ReservationEntity> newReservation(@RequestBody ReservationEntity reservation) {
        ReservationEntity createdReservation = reservationService.saveReservation(reservation);
        try {
            EmailSender.sendEmail(createdReservation.getEmail(),
                    "Nailzbyliz varausvavhistus, " + reservation.getLName() + " "
                            + TimeUtil.formatToHelsinkiTime(reservation.getStartTime()),
                    EmailBodyLogic.createNewReservationEmail(createdReservation));
        } catch (Exception ex) {
            System.out.println("Email wasnt sent");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReservation);
    }

    @PutMapping("/{reservationId}")
    public ResponseEntity<?> updateReservation(@PathVariable Long reservationId,
            @RequestBody ReservationEntity updatedReservation, HttpServletRequest request) {
        String token = jwtService.resolveToken(request);
        boolean valid = jwtService.validateToken(token);
        String role = jwtService.getRoleFromToken(token);

        if (!valid || !role.equals("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
        ReservationEntity result = reservationService.updateReservation(reservationId, updatedReservation);
        ReservationEntity originalReservation = reservationService.getReservationById(reservationId);
        if (result != null) {
            try {
                EmailSender.sendEmail(updatedReservation.getEmail(),
                        "Varuksenne tietoja muutettu, " + updatedReservation.getLName()
                                + TimeUtil.formatToHelsinkiTime(originalReservation.getStartTime()),
                        EmailBodyLogic.updatedReservationEmail(originalReservation, updatedReservation));
            } catch (Exception ex) {
                System.out.println("Email wasnt sent");
            }
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/cancel/{reservationId}")
    public ResponseEntity<String> cancelReservation(@PathVariable Long reservationId, HttpServletRequest request) {
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

        String token = jwtService.resolveToken(request);
        boolean isValid = jwtService.validateToken(token);

        if (!isValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        Long userId = jwtService.getIdFromToken(token);
        String userRole = jwtService.getRoleFromToken(token);

        if (!reservation.getCustomerId().equals(userId) && !"ROLE_ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }

        reservation.setStatus("Peruttu");
        ReservationEntity result = reservationService.updateReservation(reservationId, reservation);

        if (result == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error canceling the reservation.");
        }

        return ResponseEntity.ok(result.toString());
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long reservationId) {
        boolean deleted = reservationService.deleteReservation(reservationId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

}
