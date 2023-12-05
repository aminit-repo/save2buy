package com.frontlinehomes.save2buy.service;

import com.frontlinehomes.save2buy.data.users.User;
import com.frontlinehomes.save2buy.data.verification.VerificationToken;
import com.frontlinehomes.save2buy.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VerificationTokenService {
    @Autowired
    public VerificationTokenRepository verificationTokenRepository;
    public VerificationToken getVerificationToken(String token){
       return verificationTokenRepository.findByToken(token);
    }


    public void createVerificationToken(User user, String token) {
        VerificationToken myToken = new VerificationToken(token, user);
        verificationTokenRepository.save(myToken);
    }
}
