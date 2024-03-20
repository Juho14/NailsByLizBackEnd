package com.nailsbyliz.reservation.service;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nailsbyliz.reservation.domain.NailServiceEntity;
import com.nailsbyliz.reservation.domain.NailServiceRepository;

@Service
public class NailServiceImpl implements NailService {

    @Autowired
    NailServiceRepository nailRepo;

    @Override
    public NailServiceEntity saveNailServiceEntity(NailServiceEntity nailServiceEntity) {
        return nailRepo.save(nailServiceEntity);
    }

    @Override
    public NailServiceEntity getNailServiceEntityById(Long serviceId) {
        Optional<NailServiceEntity> optionalService = nailRepo.findById(serviceId);
        return optionalService.orElse(null);
    }

    @Override
    public NailServiceEntity updateNailServiceEntity(Long serviceId, NailServiceEntity updatedNailService) {
        Optional<NailServiceEntity> optionalNailService = nailRepo.findById(serviceId);
        if (optionalNailService.isPresent()) {
            NailServiceEntity existingNailService = optionalNailService.get();
            existingNailService.setType(updatedNailService.getType());
            existingNailService.setDuration(updatedNailService.getDuration());
            existingNailService.setPrice(updatedNailService.getPrice());
            return saveNailServiceEntity(existingNailService);
        } else {
            throw new NoSuchElementException("Nailservice not found with id: " + serviceId);
        }
    }

    @Override
    public boolean deleteNailService(Long serviceId) {
        Optional<NailServiceEntity> optionalNailService = nailRepo.findById(serviceId);
        if (optionalNailService.isPresent()) {
            NailServiceEntity nailServiceEntity = optionalNailService.get();
            nailRepo.delete(nailServiceEntity);
            return true;
        } else {
            return false;
        }
    }
}