package com.example.demo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Set;

@ConfigurationProperties(prefix = "app")
@Data
@Component
public class Config {
    private String relyingPartyId;
    private String relyingPartyName;
    private URL relyingPartyIcon;
    private Set<String> relyingPartyOrigins;
}
