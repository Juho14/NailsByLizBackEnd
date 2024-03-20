package com.nailsbyliz.reservation.service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nailsbyliz.reservation.domain.ReservationEntity;
import com.nailsbyliz.reservation.domain.ReservationRepository;

@Service
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    ReservationRepository reservationRepository;

    @Override
    public ReservationEntity saveReservation(ReservationEntity reservation) {
        LocalDateTime startTime = reservation.getStartTime();
        int durationMinutes = reservation.getNailService().getDuration();
        LocalDateTime endDateTime = startTime.plusMinutes(durationMinutes);
        reservation.setEndTime(endDateTime);
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
}
