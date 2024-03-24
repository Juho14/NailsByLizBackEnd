package com.nailsbyliz.reservation.repositories;

import org.springframework.data.repository.CrudRepository;

import com.nailsbyliz.reservation.domain.ReservationEntity;

public interface ReservationRepository extends CrudRepository<ReservationEntity, Long> {

}
