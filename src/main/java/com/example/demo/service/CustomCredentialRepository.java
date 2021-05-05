package com.example.demo.service;

import com.example.demo.entity.Customer;
import com.example.demo.repository.CustomerRepo;
import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class CustomCredentialRepository implements CredentialRepository {
    @Autowired
    private CustomerRepo customerRepo;

    @Override
    public Set<PublicKeyCredentialDescriptor> getCredentialIdsForUsername(String username) {
        Customer customer = customerRepo.findByUsername(username);
        Set<PublicKeyCredentialDescriptor> result = new HashSet<>();
        if (customer != null) {
            PublicKeyCredentialDescriptor descriptor = PublicKeyCredentialDescriptor.builder()
                    .id(new ByteArray(customer.getCredentialId())).build();
            result.add(descriptor);
        }

        return result;
    }

    @Override
    public Optional<ByteArray> getUserHandleForUsername(String username) {
        Customer customer = customerRepo.findByUsername(username);
        if (customer != null) {
            return Optional.of(new ByteArray(customer.getUsername().getBytes(StandardCharsets.UTF_8)));
        }

        return Optional.empty();
    }

    @Override
    public Optional<String> getUsernameForUserHandle(ByteArray byteArray) {
        String username = new String(byteArray.getBytes(), StandardCharsets.UTF_8);
        Customer customer = customerRepo.findByUsername(username);
        if (customer != null) {
            return Optional.of(customer.getUsername());
        }

        return Optional.empty();
    }

    @Override
    public Optional<RegisteredCredential> lookup(ByteArray credentialId, ByteArray userHandle) {
        String username = new String(userHandle.getBytes(), StandardCharsets.UTF_8);

        Customer customer = customerRepo.findByUsername(username);
        if (customer != null) {
            return Optional.of(RegisteredCredential.builder()
                    .credentialId(new ByteArray(customer.getCredentialId()))
                    .userHandle(userHandle)
                    .publicKeyCose(new ByteArray(customer.getPublicKeyCose()))
                    .signatureCount(customer.getSignatureCount())
                    .build()
            );
        }

        return Optional.empty();
    }

    @Override
    public Set<RegisteredCredential> lookupAll(ByteArray credentialId) {
        Customer customer = customerRepo.findByCredentialId(credentialId.getBytes());
        Set<RegisteredCredential> result = new HashSet<>();

        if (customer != null) {
            result.add(RegisteredCredential.builder()
                    .credentialId(new ByteArray(customer.getCredentialId()))
                    .userHandle(new ByteArray(customer.getUsername().getBytes(StandardCharsets.UTF_8)))
                    .publicKeyCose(new ByteArray(customer.getPublicKeyCose()))
                    .signatureCount(customer.getSignatureCount())
                    .build()
            );
        }

        return result;
    }
}
