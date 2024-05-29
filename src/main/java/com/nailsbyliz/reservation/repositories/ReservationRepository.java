package com.nailsbyliz.reservation.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.nailsbyliz.reservation.domain.NailServiceEntity;
import com.nailsbyliz.reservation.domain.ReservationEntity;

public interface ReservationRepository extends CrudRepository<ReservationEntity, Long> {
    List<ReservationEntity> findByNailService(NailServiceEntity nailService);

    Iterable<ReservationEntity> findByCustomerId(Long customerId);

}