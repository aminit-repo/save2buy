package com.frontlinehomes.save2buy.service;

import com.frontlinehomes.save2buy.data.verification.JwtDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
public class JWTService {
    @Autowired
    private JwtEncoder jwtEncoder;
    @Autowired
    private JwtDecoder jwtDecoder;

    public String getJWTString(Authentication authentication){
        Instant now= Instant.now();

        String scope= authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        JwtClaimsSet claimsSet= JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(24, ChronoUnit.HOURS))
                .subject(authentication.getName())
                .claim("scope",scope)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
    }


    public JwtDetails getTokenDetails(String token){
        //remove bearer from token if it exist
        String process= token.replace("bearer ", "");
        process= process.replace("Bearer ", "");
       Jwt jwt= jwtDecoder.decode(process);
       JwtDetails details= new JwtDetails(jwt.getSubject(), jwt.hasClaim("admin"));
       return details;
    }






}
