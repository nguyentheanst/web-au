package com.example.demo.controller;

import com.example.demo.dto.AssertionFinishRequest;
import com.example.demo.dto.AssertionStartResponse;
import com.example.demo.dto.RegistrationFinishRequest;
import com.example.demo.dto.RegistrationStartResponse;
import com.example.demo.entity.Customer;
import com.example.demo.service.CustomCredentialRepository;
import com.example.demo.service.CustomerService;
import com.yubico.webauthn.*;
import com.yubico.webauthn.data.*;
import com.yubico.webauthn.exception.AssertionFailedException;
import com.yubico.webauthn.exception.RegistrationFailedException;
import com.yubico.webauthn.extension.appid.AppId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
public class CustomerController {
    @Autowired
    private CustomerService customerService;
    @Autowired
    private RelyingParty relyingParty;
    @Autowired
    private CustomCredentialRepository credentialRepository;

    private final SecureRandom random = new SecureRandom();
    private final Map<String, Object> cache = new HashMap<>();

    @PostMapping("/registration/start")
    public ResponseEntity<?> registrationStart(@RequestParam("username") String username) {
        Customer customer = customerService.findByUsername(username);
        if (customer == null) {
            PublicKeyCredentialCreationOptions credentialCreation = this.relyingParty
                    .startRegistration(StartRegistrationOptions.builder()
                            .user(UserIdentity.builder().name(username)
                                    .displayName(username)
                                    .id(new ByteArray(username.getBytes(StandardCharsets.UTF_8)))
                                    .build()
                            )
                            .authenticatorSelection(AuthenticatorSelectionCriteria.builder()
                                    .authenticatorAttachment(AuthenticatorAttachment.PLATFORM)
                                    .userVerification(UserVerificationRequirement.REQUIRED)
                                    .build()
                            )
                            .timeout(18000)
                            .build()
                    );
            byte[] registrationId = new byte[16];
            this.random.nextBytes(registrationId);
            RegistrationStartResponse startResponse = new RegistrationStartResponse(
                    Base64.getEncoder().encodeToString(registrationId), credentialCreation);

            cache.put(startResponse.getRegistrationId(), credentialCreation);

            return ResponseEntity.ok(startResponse);
        }

        Map<String, String> message = new HashMap<>();
        message.put("error", "User already exist!");

        return ResponseEntity.badRequest().body(message);
    }

    @PostMapping("/registration/finish")
    public ResponseEntity<?> registrationFinish(@RequestBody RegistrationFinishRequest request) {
        PublicKeyCredentialCreationOptions creationOptions = (PublicKeyCredentialCreationOptions) cache.get(request.getRegistrationId());
        cache.remove(request.getRegistrationId());

        try {
            RegistrationResult registrationResult = relyingParty.finishRegistration(FinishRegistrationOptions.builder()
                    .request(creationOptions)
                    .response(request.getCredential()).build()
            );

            String username = creationOptions.getUser().getName();

            Customer customer = this.customerService.create(username,
                    registrationResult.getKeyId().getId().getBytes(),
                    registrationResult.getPublicKeyCose().getBytes(),
                    request.getCredential().getResponse().getParsedAuthenticatorData().getSignatureCounter());

            return ResponseEntity.ok(customer);
        } catch (RegistrationFailedException e) {
            e.printStackTrace();
        }

        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/assertion/start")
    public AssertionStartResponse start(@RequestParam("username") String username) {
        byte[] assertionId = new byte[16];
        this.random.nextBytes(assertionId);

        String assertionIdBase64 = Base64.getEncoder().encodeToString(assertionId);
        AssertionRequest assertionRequest = this.relyingParty.startAssertion(
                StartAssertionOptions.builder()
                        .username(username)
                        .userVerification(UserVerificationRequirement.REQUIRED)
                        .timeout(180000)
                        .build()
        );

        AssertionStartResponse response = new AssertionStartResponse(assertionIdBase64, assertionRequest);

        this.cache.put(assertionIdBase64, response);
        return response;
    }

    @PostMapping("/assertion/finish")
    public ResponseEntity<?> finish(@RequestBody AssertionFinishRequest finishRequest) {
        AssertionStartResponse startResponse = (AssertionStartResponse) this.cache.get(finishRequest.getAssertionId());
        this.cache.remove(finishRequest.getAssertionId());

        try {
            AssertionResult result = this.relyingParty.finishAssertion(
                    FinishAssertionOptions.builder().request(
                            startResponse.getAssertionRequest())
                            .response(finishRequest.getCredential())
                            .build()
            );

            if (result.isSuccess()) {
                System.out.println(result.getSignatureCount());
                System.out.println(result.getUsername());
                System.out.println(result.getWarnings());
                return ResponseEntity.ok(result.isSuccess());
            }
        } catch (AssertionFailedException e) {
            Map<String, String> message = new HashMap<>();
            message.put("error", "assertion failed");

            return ResponseEntity.badRequest().body(message);
        }
        return ResponseEntity.badRequest().build();
    }
}
