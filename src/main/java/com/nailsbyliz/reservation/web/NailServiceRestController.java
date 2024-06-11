package com.nailsbyliz.reservation.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

import com.nailsbyliz.reservation.config.authtoken.JwtService;
import com.nailsbyliz.reservation.domain.NailServiceEntity;
import com.nailsbyliz.reservation.dto.NailServiceAdminDTO;
import com.nailsbyliz.reservation.dto.NailServiceCustomerDTO;
import com.nailsbyliz.reservation.repositories.NailServiceRepository;
import com.nailsbyliz.reservation.service.AuthService;
import com.nailsbyliz.reservation.service.NailService;

import jakarta.servlet.http.HttpServletRequest;

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

    @Autowired
    JwtService jwtService;

    // To show all available services
    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> getAllNailServices(HttpServletRequest request) {
        String userRole = (String) request.getAttribute("userRole");
        Iterable<NailServiceEntity> services = nailRepo.findAll();
        List<?> response;

        // Check if the user is authenticated
        if ("ROLE_ADMIN".equals(userRole)) {
            response = mapServiceToAdminDTOs(services);
        } else {
            // User is not authenticated, return data for regular users
            response = mapServiceToCustomerDTOs(services);
        }

        return ResponseEntity.ok(response);
    }

    // Method to map Iterable<NailServiceEntity> to List<NailServiceAdminDTO>
    private List<NailServiceAdminDTO> mapServiceToAdminDTOs(Iterable<NailServiceEntity> services) {
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

    // Method to map Iterable<NailServiceEntity> to List<NailServiceCustomerDTO>
    private List<NailServiceCustomerDTO> mapServiceToCustomerDTOs(Iterable<NailServiceEntity> services) {
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
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> getServiceById(@PathVariable Long serviceId, HttpServletRequest request) {
        List<Long> ids = Collections.singletonList(serviceId);
        Iterable<NailServiceEntity> service = nailRepo.findAllById(ids);
        String userRole = (String) request.getAttribute("userRole");
        List<?> response;

        // Check if the token is present
        // Token is present, validate it
        if ("ROLE_ADMIN".equals(userRole)) {
            response = mapServiceToAdminDTOs(service);
            return ResponseEntity.ok(response);
        } else {
            response = mapServiceToCustomerDTOs(service);
            return ResponseEntity.ok(response);
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

    // Delete an unwanted service
    @DeleteMapping("/{serviceId}")
    public ResponseEntity<Void> deleteNailService(@PathVariable Long serviceId) {
        boolean deleted = nailService.deleteNailService(serviceId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

}
