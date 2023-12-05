package com.frontlinehomes.save2buy.controller;

import com.frontlinehomes.save2buy.data.users.User;
import com.frontlinehomes.save2buy.data.verification.VerificationToken;
import com.frontlinehomes.save2buy.service.UserService;
import com.frontlinehomes.save2buy.service.VerificationTokenService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import java.util.Calendar;
import java.util.Locale;
@CrossOrigin
@Controller
@RequestMapping("/regitrationConfirm")
public class Verification {
    @Autowired
    private VerificationTokenService verificationTokenService;

    @Autowired
    private UserService userService;
    @GetMapping("/{token}")
    public String confirmRegistration(HttpServletResponse response, @PathVariable String token) {

        VerificationToken verificationToken = verificationTokenService.getVerificationToken(token);

        if (verificationToken == null) {
            //redirect:/badUser.html
            return "redirect:http://save2buy.ng/check?entity=0";
        }


        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            return "redirect:http://save2buy.ng/expired?entity="+user.getId();
        }

        user.setEnabled(true);
        userService.saveUser(user);

        return "redirect:http://save2buy.ng/check?entity="+user.getId();

    }

}
