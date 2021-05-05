package com.example.demo;

import com.example.demo.service.CustomCredentialRepository;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.data.RelyingPartyIdentity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

@SpringBootApplication
public class DemoApplication {

    @Bean
    public RelyingParty relyingParty(CustomCredentialRepository repository, Config config) {
        RelyingPartyIdentity identity = RelyingPartyIdentity.builder()
                .id(config.getRelyingPartyId())
                .name(config.getRelyingPartyName())
                .icon(Optional.ofNullable(config.getRelyingPartyIcon()))
                .build();

        return RelyingParty.builder()
				.identity(identity)
				.credentialRepository(repository)
				.origins(config.getRelyingPartyOrigins())
				.build();
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
