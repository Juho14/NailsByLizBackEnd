package com.nailsbyliz.reservation.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import com.nailsbyliz.reservation.service.NailService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/nailservices")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class NailServiceRestController {

    @Autowired
    NailServiceRepository nailRepo;

    @Autowired
    NailService nailService;

    @Autowired
    JwtService jwtService;

    // To show all available services
    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> getAllNailServices(HttpServletRequest request) {
        String token = jwtService.resolveAuthToken(request);
        String userRole = "";
        if (token != null) {
            userRole = jwtService.getRoleFromToken(token);
        }
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
            dto.setDescription(service.getDescription());
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
            dto.setDescription(service.getDescription());
            dtos.add(dto);
        }
        return dtos;
    }

    // To fetch a specific service
    @GetMapping("/{serviceId}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> getServiceById(@PathVariable Long serviceId, HttpServletRequest request) {
        Optional<NailServiceEntity> service = nailRepo.findById(serviceId);
        String token = jwtService.resolveAuthToken(request);
        String userRole = "";
        if (token != null) {
            userRole = jwtService.getRoleFromToken(token);
        }

        if (service.isPresent()) {
            Object response;

            if ("ROLE_ADMIN".equals(userRole)) {
                response = mapToAdminDTO(service.get());
            } else {
                response = mapToCustomerDTO(service.get());
            }

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Singular service DTOs
    private NailServiceAdminDTO mapToAdminDTO(NailServiceEntity service) {
        NailServiceAdminDTO dto = new NailServiceAdminDTO();
        dto.setId(service.getId());
        dto.setType(service.getType());
        dto.setDuration(service.getDuration());
        dto.setPrice(service.getPrice());
        dto.setAdminService(service.isAdminService());
        dto.setDescription(service.getDescription());
        return dto;
    }

    private NailServiceCustomerDTO mapToCustomerDTO(NailServiceEntity service) {
        // Skip admin-only services
        if (service.isAdminService()) {
            return null;
        }

        NailServiceCustomerDTO dto = new NailServiceCustomerDTO();
        dto.setId(service.getId());
        dto.setType(service.getType());
        dto.setDuration(service.getDuration());
        dto.setPrice(service.getPrice());
        dto.setDescription(service.getDescription());
        return dto;
    }

    // Create a new type of service
    @PostMapping
    public ResponseEntity<?> newNailSerEntity(@RequestBody NailServiceEntity newService,
            HttpServletRequest request) {
        String token = jwtService.resolveAuthToken(request);
        String userRole = "";
        if (token != null) {
            userRole = jwtService.getRoleFromToken(token);
        }

        if (!"ROLE_ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
        NailServiceEntity createdNailServiceEntity = nailService.saveNailServiceEntity(newService);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNailServiceEntity);
    }

    // Edit existing services
    @PutMapping("/{serviceId}")
    public ResponseEntity<NailServiceEntity> updateNailServicEntity(@PathVariable Long serviceId,
            @RequestBody NailServiceEntity updatedNailService, HttpServletRequest request) {
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
