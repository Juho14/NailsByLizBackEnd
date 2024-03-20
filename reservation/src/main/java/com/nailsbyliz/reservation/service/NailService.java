package com.nailsbyliz.reservation.service;

import com.nailsbyliz.reservation.domain.NailServiceEntity;

public interface NailService {

    NailServiceEntity saveNailServiceEntity(NailServiceEntity nailServiceEntity);

    NailServiceEntity getNailServiceEntityById(Long serviceId);

    NailServiceEntity updateNailServiceEntity(Long serviceId, NailServiceEntity updatedNailService);

    boolean deleteNailService(Long serviceId);

}
