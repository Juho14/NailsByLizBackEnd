package com.nailsbyliz.reservation.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.nailsbyliz.reservation.domain.AppUserEntity;

public interface AppUserRepository extends CrudRepository<AppUserEntity, Long> {

    AppUserEntity findByUsername(String username);

    @Query("SELECT u FROM AppUserEntity u WHERE u.role = :role")
    Iterable<AppUserEntity> findByRole(@Param("role") String role);
}
