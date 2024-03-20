package com.nailsbyliz.reservation.domain;

import org.springframework.data.repository.CrudRepository;

public interface AppUserRepository extends CrudRepository<AppUserEntity, Long> {
    AppUserEntity findByUsername(String username);
}