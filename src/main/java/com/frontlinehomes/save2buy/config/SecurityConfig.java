package com.frontlinehomes.save2buy.config;

import com.frontlinehomes.save2buy.data.users.admin.Scopes;
import com.frontlinehomes.save2buy.service.DefaultAuthenticationProvider;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.crypto.RsaKeyConversionServicePostProcessor;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import java.lang.reflect.Array;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.oauth2.core.authorization.OAuth2AuthorizationManagers.hasScope;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private DefaultAuthenticationProvider authenticationProvider;
    @Autowired
    private RSAProperties rsaKeys;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize.requestMatchers("/images/**","user/login","/registrationConfirm/**", "/investor/create", "/init", "/css/**", "/js/**").permitAll()
                        .requestMatchers("/land/create", "/land/image/{id}","/payment-plan/create/land/{id}", "/payment-plan/duration/create", "/payment-plan/configuration/create" ).access(hasScope(Scopes.create_land.toString()))
                        .requestMatchers("/administrator/create").access(hasScope(Scopes.create_admin.toString()))
                        .requestMatchers(HttpMethod.GET,"/investor").access(hasScope(Scopes.list_investors.toString()))
                        .requestMatchers(HttpMethod.PUT, "/land/{id}").access(hasScope(Scopes.update_land.toString()))
                        .requestMatchers("/account/transactions/{id}", "/account/initiated-transactions/{id}").access(hasScope(Scopes.read_transaction.toString()))
                        .requestMatchers(HttpMethod.PUT,"/investor/{id}").access(hasScope(Scopes.update_investor.toString()))
                        .requestMatchers("/investor/passport/{id}", "/investor/identification/{id}").access(hasScope(Scopes.update_investor.toString()))
                        .requestMatchers(HttpMethod.GET,"/investor/**", "/investor/status/**").access(hasScope(Scopes.read_investor.toString()))
                        .anyRequest().authenticated()
                ).csrf(httpSecurityCsrfConfigurer -> {httpSecurityCsrfConfigurer.disable(); })
                .sessionManagement(session-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer((oauth2) -> oauth2
                        .jwt(withDefaults())
                );
        return http.build();
    }


    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(rsaKeys.rsaPublicKey()).build();
    }



    @Bean
    public JwtEncoder jwtEncoder(){
        JWK jwk= new RSAKey.Builder(rsaKeys.rsaPublicKey()).privateKey(rsaKeys.rsaPrivateKey()).build();
        JWKSource<SecurityContext> jwks= new ImmutableJWKSet<>(new JWKSet(jwk));
        return  new NimbusJwtEncoder(jwks);
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return  new ProviderManager(authenticationProvider);
    }


}
