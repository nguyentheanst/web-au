package com.example.demo.dto;

import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegistrationStartResponse {
    private String registrationId;
    private PublicKeyCredentialCreationOptions creationOptions;
}
