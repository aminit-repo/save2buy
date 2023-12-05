package com.frontlinehomes.save2buy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
@ConfigurationProperties("rsa")
public record RSAProperties(RSAPublicKey rsaPublicKey, RSAPrivateKey rsaPrivateKey) {

}
