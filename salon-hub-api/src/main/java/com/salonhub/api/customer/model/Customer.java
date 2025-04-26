package com.salonhub.api.customer.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    private String phoneNumber;

    @Column(columnDefinition = "TEXT")
    private String note;

    public Customer() { }

    public Customer(String email, String name, String phoneNumber, String note) {
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.note = note;
    }

    // ─── Getters & Setters ──────────────────────────────────────────────────────

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }
}