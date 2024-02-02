package com.frontlinehomes.save2buy.controller;

import com.frontlinehomes.save2buy.data.users.User;
import com.frontlinehomes.save2buy.data.verification.VerificationToken;
import com.frontlinehomes.save2buy.service.UserService;
import com.frontlinehomes.save2buy.service.VerificationTokenService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Calendar;

/**
 *
 * Custom Errors originated from  VerificationController starts with the code 7XX
 */
@CrossOrigin
@RestController
@RequestMapping("/registrationConfirm")
public class VerificationController {
    @Autowired
    private VerificationTokenService verificationTokenService;
    private static Logger log = LogManager.getLogger( VerificationController.class);


    @Autowired
    private UserService userService;
    @PostMapping("/{token}")
    public ResponseEntity<String> confirmRegistration(HttpServletResponse response, @PathVariable String token) {

        VerificationToken verificationToken = verificationTokenService.getVerificationToken(token);
        log.info("VerificationController: confirmRegistration: token fetched is "+verificationToken.getToken());

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
        log.info("VerificationController: confirmRegistration: email verified ");
        return new ResponseEntity<String>("email verified",HttpStatus.OK);
    }

}
