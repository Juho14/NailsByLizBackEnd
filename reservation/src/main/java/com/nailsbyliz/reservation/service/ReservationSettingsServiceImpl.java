package com.nailsbyliz.reservation.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nailsbyliz.reservation.domain.ReservationSettings;
import com.nailsbyliz.reservation.domain.ReservationSettingsRepository;

@Service
public class ReservationSettingsServiceImpl implements ReservationSettingsService {

    @Autowired
    private ReservationSettingsRepository settingsRepo;

    @Override
    public void setActiveReservationSetting(Long id) {
        // Set all reservations to false
        Iterable<ReservationSettings> reservationSettings = settingsRepo.findAll();
        for (ReservationSettings r : reservationSettings) {
            r.setIsActive(false);
            settingsRepo.save(r);
        }

        // Set the reservation with the given id to active
        Optional<ReservationSettings> optionalReservation = settingsRepo.findById(id);
        optionalReservation.ifPresent(reservation -> {
            reservation.setIsActive(true);
            settingsRepo.save(reservation);
        });
    }

    @Override
    public ReservationSettings findActiveReservation() {
        Iterable<ReservationSettings> reservationSettings = settingsRepo.findAll();

        for (ReservationSettings r : reservationSettings) {
            if (r.getIsActive()) {
                return r;
            }
        }
        return null;
    }

}
