package com.nailsbyliz.reservation.dto;

public class NailServiceAdminDTO {
    private Long id;
    private String type;
    private int duration;
    private double price;
    private boolean adminService;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isAdminService() {
        return this.adminService;
    }

    public boolean getAdminService() {
        return this.adminService;
    }

    public void setAdminService(boolean isAdminService) {
        this.adminService = isAdminService;
    }

}
