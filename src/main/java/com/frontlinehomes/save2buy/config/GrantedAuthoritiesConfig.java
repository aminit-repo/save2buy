package com.frontlinehomes.save2buy.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@NoArgsConstructor
@ConfigurationProperties("scope")
public class GrantedAuthoritiesConfig {

    private String user;

    private String admin;



}
