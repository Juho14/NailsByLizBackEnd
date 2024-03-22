package com.nailsbyliz.reservation.service;

import com.nailsbyliz.reservation.domain.ReservationSettings;

public interface ReservationSettingsService {

    void setActiveReservationSetting(Long id);

    ReservationSettings findActiveReservation();

}
