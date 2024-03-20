package com.nailsbyliz.reservation.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nailsbyliz.reservation.domain.NailServiceEntity;
import com.nailsbyliz.reservation.domain.NailServiceRepository;
import com.nailsbyliz.reservation.service.NailService;

@RestController
@RequestMapping("/api/nailservices")
public class NailServiceRestController {

    @Autowired
    NailServiceRepository nailRepo;

    @Autowired
    NailService nailService;

    // To show all available services
    @GetMapping
    public ResponseEntity<Iterable<NailServiceEntity>> get() {
        Iterable<NailServiceEntity> services = nailRepo.findAll();
        return ResponseEntity.ok(services);
    }

    // To show a specific service
    @GetMapping("/{serviceId}")
    public ResponseEntity<NailServiceEntity> getServiceById(@PathVariable Long serviceId) {
        NailServiceEntity service = nailService.getNailServiceEntityById(serviceId);

        if (service != null) {
            return ResponseEntity.ok(service);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Create a new type of service
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NailServiceEntity> newNailSerEntity(@RequestBody NailServiceEntity newService) {
        NailServiceEntity createdNailServiceEntity = nailService.saveNailServiceEntity(newService);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNailServiceEntity);
    }

    // Edit existing services
    @PutMapping("/{serviceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NailServiceEntity> updateNailServicEntity(@PathVariable Long serviceId,
            @RequestBody NailServiceEntity updatedNailService) {
        NailServiceEntity result = nailService.updateNailServiceEntity(serviceId, updatedNailService);
        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete an unvanted service
    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteNailService(@PathVariable Long serviceId) {
        boolean deleted = nailService.deleteNailService(serviceId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

}
