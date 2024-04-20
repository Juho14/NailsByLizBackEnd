package com.nailsbyliz.reservation.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nailsbyliz.reservation.domain.NailServiceEntity;
import com.nailsbyliz.reservation.domain.ReservationEntity;
import com.nailsbyliz.reservation.repositories.NailServiceRepository;
import com.nailsbyliz.reservation.repositories.ReservationRepository;

@Service
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    NailServiceRepository nailRepo;

    @Override
    public ReservationEntity saveReservation(ReservationEntity reservation) {
        LocalDateTime startTime = reservation.getStartTime();
        // Value to track the existing id when editing a reservation
        Long existingId = reservation.getId();

        Long serviceId = reservation.getNailService().getId();
        NailServiceEntity nailService = nailRepo.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Nail Service ID"));
        reservation.setNailService(nailService);
        reservation.setEndTime(startTime.plusMinutes(nailService.getDuration()));
        if (existingId != null) { // Check if it's an existing reservation
            // Retrieve the existing reservation from the database
            Optional<ReservationEntity> optionalExistingReservation = reservationRepository.findById(existingId);
            if (!optionalExistingReservation.isPresent()) {
                // New reservations are always set to the active service price
                // When editing a reservation, price can be set manually.
                reservation.setPrice(nailService.getPrice());
            }
        }
        // Check for overlaps
        List<ReservationEntity> reservationsOfDay = getReservationsByDay(
                Date.from(reservation.getStartTime().atZone(ZoneId.systemDefault()).toInstant()));

        for (ReservationEntity r : reservationsOfDay) {
            // Ignore the overlap with its own timeslot when editing a reservation
            if (existingId != null && r.getId().equals(existingId)) {
                continue;
            }
            if (!r.getStatus().equalsIgnoreCase("OK")) {
                continue;
            }
            LocalDateTime existingStartTime = r.getStartTime();
            LocalDateTime existingEndTime = r.getEndTime();

            // Check if the new reservation overlaps with the existing reservation
            if (reservation.getStartTime().isBefore(existingEndTime) &&
                    existingStartTime.isBefore(reservation.getEndTime())) {
                throw new IllegalArgumentException("The new reservation overlaps with an existing reservation.");
            }
        }

        return reservationRepository.save(reservation);
    }

    @Override
    public ReservationEntity getReservationById(Long reservationId) {
        Optional<ReservationEntity> optionalReservation = reservationRepository.findById(reservationId);
        return optionalReservation.orElse(null);
    }

    @Override
    public ReservationEntity updateReservation(Long reservationId, ReservationEntity updatedReservation) {
        Optional<ReservationEntity> opitonalReservation = reservationRepository.findById(reservationId);
        if (opitonalReservation.isPresent()) {
            ReservationEntity existingReservation = opitonalReservation.get();
            existingReservation.setFName(updatedReservation.getFName());
            existingReservation.setLName(updatedReservation.getLName());
            existingReservation.setEmail(updatedReservation.getEmail());
            existingReservation.setAddress(updatedReservation.getAddress());
            existingReservation.setCity(updatedReservation.getCity());
            existingReservation.setPostalcode(updatedReservation.getPostalcode());
            existingReservation.setPrice(updatedReservation.getPrice());
            existingReservation.setStartTime(updatedReservation.getStartTime());
            existingReservation.setNailService(updatedReservation.getNailService());
            existingReservation.setStatus(updatedReservation.getStatus());
            return saveReservation(existingReservation);
        } else {
            throw new NoSuchElementException("Reservation not found with id: " + reservationId);
        }
    }

    @Override
    public boolean deleteReservation(Long reservationId) {
        Optional<ReservationEntity> optionalReservation = reservationRepository.findById(reservationId);
        if (optionalReservation.isPresent()) {
            ReservationEntity reservation = optionalReservation.get();
            reservationRepository.delete(reservation);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<ReservationEntity> getReservationsByDay(Date day) {
        List<ReservationEntity> reservationsOfDay = new ArrayList<>();
        Iterable<ReservationEntity> existingReservations = reservationRepository.findAll();

        LocalDate targetDate = day.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        for (ReservationEntity r : existingReservations) {
            LocalDateTime startTime = r.getStartTime();
            LocalDate reservationDate = startTime.toLocalDate();

            if (reservationDate.equals(targetDate)) {
                reservationsOfDay.add(r);
            }
        }

        return reservationsOfDay;
    }

    @Override
    public List<ReservationEntity> getReservationsForWeek(Date date) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate startOfWeek = localDate.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        // Retrieve reservations for the entire week
        List<ReservationEntity> reservationsForWeek = new ArrayList<>();
        for (LocalDate currentDate = startOfWeek; currentDate
                .isBefore(endOfWeek.plusDays(1)); currentDate = currentDate.plusDays(1)) {
            List<ReservationEntity> reservationsOfDay = getReservationsByDay(
                    Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            reservationsForWeek.addAll(reservationsOfDay);
        }

        return reservationsForWeek;
    }

}
