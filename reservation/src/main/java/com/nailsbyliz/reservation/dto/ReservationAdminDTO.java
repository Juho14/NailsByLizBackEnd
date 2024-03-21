package com.nailsbyliz.reservation.dto;

import java.time.LocalDateTime;

import com.nailsbyliz.reservation.domain.NailServiceEntity;

public class ReservationAdminDTO {
    private Long id;
    private String fName;
    private String lName;
    private String email;
    private String phone;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private NailServiceEntity nailService;
    private String status;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFName() {
        return this.fName;
    }

    public void setFName(String fName) {
        this.fName = fName;
    }

    public String getLName() {
        return this.lName;
    }

    public void setLName(String lName) {
        this.lName = lName;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public NailServiceEntity getNailService() {
        return this.nailService;
    }

    public void setNailService(NailServiceEntity nailService) {
        this.nailService = nailService;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}