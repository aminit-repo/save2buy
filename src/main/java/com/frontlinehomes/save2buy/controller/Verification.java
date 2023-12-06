package com.frontlinehomes.save2buy.controller;

import com.frontlinehomes.save2buy.data.users.User;
import com.frontlinehomes.save2buy.data.verification.VerificationToken;
import com.frontlinehomes.save2buy.service.UserService;
import com.frontlinehomes.save2buy.service.VerificationTokenService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.Calendar;
import java.util.Locale;
@CrossOrigin
@Controller
@RequestMapping("/registrationConfirm")
public class Verification {
    @Autowired
    private VerificationTokenService verificationTokenService;

    @Autowired
    private UserService userService;
    @PostMapping("/{token}")
    public ResponseEntity<String> confirmRegistration(HttpServletResponse response, @PathVariable String token) {

        VerificationToken verificationToken = verificationTokenService.getVerificationToken(token);

        if (verificationToken == null) {
            //redirect:/badUser.html
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }


        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        user.setEnabled(true);
        userService.saveUser(user);
        return new ResponseEntity<String>("email verified",HttpStatus.OK);

    }

}
