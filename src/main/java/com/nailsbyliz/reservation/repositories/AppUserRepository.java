package com.nailsbyliz.reservation.repositories;

import org.springframework.data.repository.CrudRepository;

import com.nailsbyliz.reservation.domain.AppUserEntity;

public interface AppUserRepository extends CrudRepository<AppUserEntity, Long> {

    AppUserEntity findByUsername(String username);
}
