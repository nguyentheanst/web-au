package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yubico.webauthn.AssertionRequest;
import com.yubico.webauthn.data.PublicKeyCredentialRequestOptions;
import lombok.Data;

@Data
public class AssertionStartResponse {
    private String assertionId;
    private PublicKeyCredentialRequestOptions credentialOptions;

    @JsonIgnore
    private final AssertionRequest assertionRequest;

    public AssertionStartResponse(String assertionId, AssertionRequest assertionRequest) {
        this.assertionId = assertionId;
        this.assertionRequest = assertionRequest;
        this.credentialOptions = assertionRequest.getPublicKeyCredentialRequestOptions();
    }
}
