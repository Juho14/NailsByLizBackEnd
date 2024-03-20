package com.nailsbyliz.reservation.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    private String fName;
    private String lName;
    private String email;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "nailservice_id")
    private NailServiceEntity nailService;

    private String status;

    public ReservationEntity() {
    }

    public ReservationEntity(String fName, String lName, String email, LocalDateTime startTime, LocalDateTime endTime,
            NailServiceEntity nailService, String status) {
        this.fName = fName;
        this.lName = lName;
        this.email = email;
        this.startTime = startTime;
        this.endTime = endTime;
        this.nailService = nailService;
        this.status = status;
    }

    public Long getId() {
        return this.id;
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

    @Override
    public String toString() {
        return "{" +
                " id='" + getId() + "'" +
                ", fName='" + getFName() + "'" +
                ", lName='" + getLName() + "'" +
                ", email='" + getEmail() + "'" +
                ", startTime='" + getStartTime() + "'" +
                ", endTime='" + getEndTime() + "'" +
                ", nailService='" + getNailService() + "'" +
                ", status='" + getStatus() + "'" +
                "}";
    }

}
