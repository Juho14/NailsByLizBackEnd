package com.nailsbyliz.reservation.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nailsbyliz.reservation.domain.NailServiceEntity;
import com.nailsbyliz.reservation.dto.NailServiceAdminDTO;
import com.nailsbyliz.reservation.dto.NailServiceCustomerDTO;
import com.nailsbyliz.reservation.repositories.NailServiceRepository;
import com.nailsbyliz.reservation.service.AuthService;
import com.nailsbyliz.reservation.service.NailService;

@RestController
// @PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/api/nailservices")
public class NailServiceRestController {

    @Autowired
    NailServiceRepository nailRepo;

    @Autowired
    NailService nailService;

    @Autowired
    AuthService authService;

    // To show all available services
    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> getAllNailServices(Authentication authentication) {

        boolean isAdmin = authService.isAdmin();

        Iterable<NailServiceEntity> services = nailRepo.findAll();

        List<?> response;

        /*
         * if (isAdmin) {
         * response = mapToAdminDTOs(services);
         * } else {
         * response = mapToCustomerDTOs(services);
         * }
         */
        response = mapToAdminDTOs(services);
        return ResponseEntity.ok(response);

    }

    private List<NailServiceAdminDTO> mapToAdminDTOs(Iterable<NailServiceEntity> services) {
        List<NailServiceAdminDTO> dtos = new ArrayList<>();
        for (NailServiceEntity service : services) {
            NailServiceAdminDTO dto = new NailServiceAdminDTO();
            dto.setId(service.getId());
            dto.setType(service.getType());
            dto.setDuration(service.getDuration());
            dto.setPrice(service.getPrice());
            dto.setAdminService(service.isAdminService());
            dtos.add(dto);
        }
        return dtos;
    }

    private List<NailServiceCustomerDTO> mapToCustomerDTOs(Iterable<NailServiceEntity> services) {
        List<NailServiceCustomerDTO> dtos = new ArrayList<>();
        for (NailServiceEntity service : services) {
            // Skip all admin-only services
            if (service.isAdminService()) {
                continue;
            }
            NailServiceCustomerDTO dto = new NailServiceCustomerDTO();
            dto.setId(service.getId());
            dto.setType(service.getType());
            dto.setDuration(service.getDuration());
            dto.setPrice(service.getPrice());
            dtos.add(dto);
        }
        return dtos;
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
    public ResponseEntity<NailServiceEntity> newNailSerEntity(@RequestBody NailServiceEntity newService) {
        NailServiceEntity createdNailServiceEntity = nailService.saveNailServiceEntity(newService);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNailServiceEntity);
    }

    // Edit existing services
    @PutMapping("/{serviceId}")
    public ResponseEntity<NailServiceEntity> updateNailServicEntity(@PathVariable Long serviceId,
            @RequestBody NailServiceEntity updatedNailService) {
        NailServiceEntity result = nailService.updateNailServiceEntity(serviceId, updatedNailService);
        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete an unwanted service
    @DeleteMapping("/{serviceId}")
    public ResponseEntity<Void> deleteNailService(@PathVariable Long serviceId) {
        boolean deleted = nailService.deleteNailService(serviceId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

}
