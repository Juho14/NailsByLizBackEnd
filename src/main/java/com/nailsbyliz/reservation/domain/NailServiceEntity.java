package com.nailsbyliz.reservation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class NailServiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    private String type;
    private int duration;
    private double price;
    private String description;
    private boolean AdminService;

    public NailServiceEntity() {
    }

    public NailServiceEntity(String type, int duration, double price, String description, boolean AdminService) {
        this.type = type;
        this.duration = duration;
        this.price = price;
        this.description = description;
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

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getAdminService() {
        return this.AdminService;
    }

    public void setAdminService(boolean AdminService) {
        this.AdminService = AdminService;
    }

    @Override
    public String toString() {
        return "{" +
                " id='" + getId() + "'" +
                ", type='" + getType() + "'" +
                ", duration='" + getDuration() + "'" +
                ", price='" + getPrice() + "'" +
                ", description='" + getDescription() + "'" +
                ", AdminService='" + isAdminService() + "'" +
                "}";
    }

}
