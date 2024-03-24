package com.nailsbyliz.reservation.repositories;

import org.springframework.data.repository.CrudRepository;

import com.nailsbyliz.reservation.domain.ReservationSettings;

public interface ReservationSettingsRepository extends CrudRepository<ReservationSettings, Long> {

}
