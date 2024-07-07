package com.nailsbyliz.reservation.service;

import java.util.Date;
import java.util.List;

import com.nailsbyliz.reservation.domain.ReservationEntity;

public interface ReservationService {

    ReservationEntity newReservation(ReservationEntity reservation, String userRole);

    ReservationEntity saveReservation(ReservationEntity reservation);

    ReservationEntity getReservationById(Long reservationId);

    ReservationEntity updateReservation(Long reservationId, ReservationEntity updatedReservation);

    boolean deleteReservation(Long reservationId);

    List<ReservationEntity> getReservationsByDay(Date date);

    List<ReservationEntity> getReservationsForWeek(Date date);
}
