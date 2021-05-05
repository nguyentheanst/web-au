package com.example.demo.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "CUSTOMER")
@Data
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String email;
    private String mobileNumber;
    private String username;
    @Column(name = "credential_id")
    private byte[] credentialId;
    @Column(name = "public_key_cose")
    private byte[] publicKeyCose;
    @Column(name = "signature_count")
    private Long signatureCount;
}
