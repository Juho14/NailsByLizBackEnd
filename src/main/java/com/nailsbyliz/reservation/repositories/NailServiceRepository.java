package com.nailsbyliz.reservation.repositories;

import org.springframework.data.repository.CrudRepository;

import com.nailsbyliz.reservation.domain.NailServiceEntity;

public interface NailServiceRepository extends CrudRepository<NailServiceEntity, Long> {

}
