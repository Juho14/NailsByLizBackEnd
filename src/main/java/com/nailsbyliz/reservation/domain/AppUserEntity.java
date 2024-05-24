package com.nailsbyliz.reservation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class AppUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    private String fName;
    private String lName;
    private String username;
    private String phone;
    private String email;
    private String address;
    private String postalcode;
    private String city;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "role", nullable = false)
    private String role;

    public AppUserEntity() {
    }

    public AppUserEntity(String fName, String lName, String username, String phone, String email,
            String address, String postalcode, String city, String passwordHash, String role) {
        this.fName = fName;
        this.lName = lName;
        this.username = username;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.postalcode = postalcode;
        this.city = city;
        this.passwordHash = passwordHash;
        this.role = role;
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

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return this.passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalcode() {
        return this.postalcode;
    }

    public void setPostalcode(String postalcode) {
        this.postalcode = postalcode;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "{" +
                " id='" + getId() + "'" +
                ", fName='" + getFName() + "'" +
                ", lName='" + getLName() + "'" +
                ", username='" + getUsername() + "'" +
                ", phone='" + getPhone() + "'" +
                ", email='" + getEmail() + "'" +
                ", address='" + getAddress() + "'" +
                ", postalcode='" + getPostalcode() + "'" +
                ", city='" + getCity() + "'" +
                ", passwordHash='" + getPasswordHash() + "'" +
                ", role='" + getRole() + "'" +
                "}";
    }

}