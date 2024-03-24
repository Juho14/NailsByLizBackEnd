package com.nailsbyliz.reservation.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class NailServiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    private String type;
    private int duration;
    private double price;
    private boolean AdminService;

    @OneToMany(mappedBy = "nailService")
    @JsonIgnore
    private List<ReservationEntity> reservations = new ArrayList<>();

    public NailServiceEntity() {
    }

    public NailServiceEntity(String type, int duration, double price, boolean AdminService) {
        this.type = type;
        this.duration = duration;
        this.price = price;
        this.AdminService = AdminService;
    }

    public Long getId() {
        return this.id;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int length) {
        this.duration = length;
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isAdminService() {
        return this.AdminService;
    }

    public boolean getAdminService() {
        return this.AdminService;
    }

    public void setAdminService(boolean AdminService) {
        this.AdminService = AdminService;
    }

    public List<ReservationEntity> getReservations() {
        return this.reservations;
    }

    public void setReservations(List<ReservationEntity> reservations) {
        this.reservations = reservations;
    }

    @Override
    public String toString() {
        return "{" +
                " id='" + getId() + "'" +
                ", type='" + getType() + "'" +
                ", duration='" + getDuration() + "'" +
                ", price='" + getPrice() + "'" +
                ", AdminService='" + isAdminService() + "'" +
                "}";
    }

}
