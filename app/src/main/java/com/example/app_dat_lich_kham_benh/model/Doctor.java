package com.example.app_dat_lich_kham_benh.model;

public class Doctor {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String specialty;

    public Doctor(int id, String firstName, String lastName, String email, String specialty) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.specialty = specialty;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return "Dr. " + firstName + " " + lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getSpecialty() {
        return specialty;
    }
}
