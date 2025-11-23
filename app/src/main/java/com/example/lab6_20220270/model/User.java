package com.example.lab6_20220270.model;

import com.google.firebase.firestore.Exclude;

public class User {
    @Exclude
    private String documentId;
    private String name;
    private String dni;
    private String email;
    private String profileImageUrl;

    public User() {
    }

    public User(String name, String dni, String email) {
        this.name = name;
        this.dni = dni;
        this.email = email;
    }

    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    @Exclude
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
