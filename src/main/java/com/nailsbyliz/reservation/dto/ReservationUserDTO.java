package com.nailsbyliz.reservation.dto;

import java.time.LocalDateTime;

public class ReservationUserDTO {
    private Long id;
    private String fName;
    private String lName;
    private double price;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private NailServiceCustomerDTO nailService;

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

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
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

    public NailServiceCustomerDTO getNailService() {
        return this.nailService;
    }

    public void setNailService(NailServiceCustomerDTO nailService) {
        this.nailService = nailService;
    }
}