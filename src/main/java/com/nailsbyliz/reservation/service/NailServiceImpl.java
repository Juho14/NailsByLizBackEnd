package com.nailsbyliz.reservation.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nailsbyliz.reservation.domain.NailServiceEntity;
import com.nailsbyliz.reservation.domain.ReservationEntity;
import com.nailsbyliz.reservation.repositories.NailServiceRepository;
import com.nailsbyliz.reservation.repositories.ReservationRepository;

@Service
public class NailServiceImpl implements NailService {

    @Autowired
    NailServiceRepository nailRepo;

    @Autowired
    ReservationRepository reservationRepo;

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
            existingNailService.setDescription(updatedNailService.getDescription());
            existingNailService.setAdminService(updatedNailService.getAdminService());
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

            List<ReservationEntity> reservationsUsingService = reservationRepo
                    .findByNailService(nailServiceEntity);

            // Set the service to null for each reservation
            for (ReservationEntity reservation : reservationsUsingService) {
                reservation.setNailService(null);

                // Save the updated reservation
                reservationRepo.save(reservation);
            }

            nailRepo.delete(nailServiceEntity);
            return true;
        } else {
            return false;
        }
    }

}