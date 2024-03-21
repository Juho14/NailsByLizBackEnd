package com.nailsbyliz.reservation.service;

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
import com.nailsbyliz.reservation.domain.NailServiceRepository;
import com.nailsbyliz.reservation.domain.ReservationEntity;
import com.nailsbyliz.reservation.domain.ReservationRepository;

@Service
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    NailServiceRepository nailRepo;

    @Override
    public ReservationEntity saveReservation(ReservationEntity reservation) {
        LocalDateTime startTime = reservation.getStartTime();
        int durationMinutes = reservation.getNailService().getDuration();
        LocalDateTime endDateTime = startTime.plusMinutes(durationMinutes);
        reservation.setEndTime(endDateTime);

        Long serviceId = reservation.getNailService().getId();
        NailServiceEntity nailService = nailRepo.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Nail Service ID"));

        reservation.setNailService(nailService);

        List<ReservationEntity> reservationsOfDay = getReservationsByDay(
                Date.from(reservation.getStartTime().atZone(ZoneId.systemDefault()).toInstant()));
        if (!reservationsOfDay.isEmpty()) {
            for (ReservationEntity r : reservationsOfDay) {
                if (startTime.isAfter(r.getEndTime()) || endDateTime.isBefore(r.getStartTime())) {
                    // Checks if there are overlaps. If not, the entity will be saved.
                    continue;
                } else {
                    throw new IllegalArgumentException("The new reservation overlaps with an existing reservation.");
                }
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
            existingReservation.setStartTime(updatedReservation.getStartTime());
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

        for (ReservationEntity r : existingReservations) {
            Date dateOfExisting = Date.from(r.getStartTime().atZone(ZoneId.systemDefault()).toInstant());
            if (dateOfExisting.compareTo(day) == 0) {
                reservationsOfDay.add(r);
            }
        }

        return reservationsOfDay;
    }
}
