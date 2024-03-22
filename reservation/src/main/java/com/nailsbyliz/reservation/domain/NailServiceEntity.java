package com.nailsbyliz.reservation.domain;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class NailServiceEntity {

    private Long id;
    private String type;
    private int duration;
    private double price;
    private boolean AdminService;

    public NailServiceEntity() {
    }

    public NailServiceEntity(String type, int duration, double price, boolean AdminService) {
        this.type = type;
        this.duration = duration;
        this.price = price;
        this.AdminService = AdminService;
    }

    @DynamoDbPartitionKey
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
