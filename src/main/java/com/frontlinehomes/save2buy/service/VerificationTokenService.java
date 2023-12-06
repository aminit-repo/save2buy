package com.frontlinehomes.save2buy.service;

import com.frontlinehomes.save2buy.data.users.User;
import com.frontlinehomes.save2buy.data.verification.VerificationToken;
import com.frontlinehomes.save2buy.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

@Service
public class VerificationTokenService {
    @Autowired
    public VerificationTokenRepository verificationTokenRepository;
    public VerificationToken getVerificationToken(String token){
       return verificationTokenRepository.findByToken(token);
    }


    public void createVerificationToken(User user, String token) {
        VerificationToken myToken = new VerificationToken(token, user, calculateExpiryDate(2880));
        verificationTokenRepository.save(myToken);
    }

    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }
}
