package com.frontlinehomes.save2buy.controller;

import com.frontlinehomes.save2buy.data.email.VerifyEmail;
import com.frontlinehomes.save2buy.data.users.*;
import com.frontlinehomes.save2buy.data.users.request.LoginDTO;
import com.frontlinehomes.save2buy.data.users.request.SignUpDTO;
import com.frontlinehomes.save2buy.data.users.request.UserDTO;
import com.frontlinehomes.save2buy.data.users.response.LoginResponseDTO;
import com.frontlinehomes.save2buy.data.verification.VerificationToken;
import com.frontlinehomes.save2buy.events.OnRegistrationCompleteEvent;
import com.frontlinehomes.save2buy.exception.EntityDuplicationException;
import com.frontlinehomes.save2buy.exception.NotNullFieldException;
import com.frontlinehomes.save2buy.service.HarshService;
import com.frontlinehomes.save2buy.service.JWTService;
import com.frontlinehomes.save2buy.service.UserService;
import com.frontlinehomes.save2buy.service.VerificationTokenService;
import com.frontlinehomes.save2buy.service.mail.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import org.antlr.v4.runtime.misc.MultiMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    private static Logger log = LogManager.getLogger( UserController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtService;

    @CrossOrigin
    @PostMapping("/login")
    public ResponseEntity<String> signin(@RequestBody LoginDTO loginDTO){
        Authentication authentication= authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO
                .getPassword()));
        //get the specified user details
        return  ResponseEntity.ok(jwtService.getJWTString(authentication));
    }

    @CrossOrigin( allowedHeaders = {"Authorization"})
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id){
        try{
            return new ResponseEntity<User>(userService.getUser(id), HttpStatus.OK);
        }catch (NoSuchElementException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User was not found");
        }
    }

    @CrossOrigin( allowedHeaders = {"Authorization"})
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(){
        return new ResponseEntity<List<User>>(userService.getAllUser(), HttpStatus.OK);
    }

   /* @PutMapping
    public ResponseEntity<UserDTO> updateDetails(@RequestBody UserDTO userDTO){
        if(userDTO.getId()== null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Specify Id");
        }

        User user= userService.getUser(userDTO.getId());
        userDTO.getPhone().forEach( phone ->{
            user.addPhone(new Phone(phone));
        });
        //prevent the email update
        userDTO.setEmail(null);
        userDTO.setId(null);
        User user1= convertUserDTOtoUser(userDTO);

        //updates the provided fields
        CopyUtils.copyNonNullFields(user, user1);
        return new ResponseEntity<UserDTO>( convertUserToUserDTO(user), HttpStatus.OK);
    }

    */

    private User convertSignUpDTOtoUser(SignUpDTO signUpDTO){
        User user= new User();
        BeanUtils.copyProperties(signUpDTO, user);
        return user;
    }

    private LoginResponseDTO convertUserTOLoginResponseDTO(User user){
        LoginResponseDTO loginResponseDTO= new LoginResponseDTO();
        BeanUtils.copyProperties(user, loginResponseDTO);
        return loginResponseDTO;
    }


    public SignUpDTO convertUserToSigUpDTO(User user){
        SignUpDTO signUpDTO= new SignUpDTO();
        BeanUtils.copyProperties(user, signUpDTO);
        return signUpDTO;
    }
    public User convertUserDTOtoUser(UserDTO userDTO){
        User user= new User();
        BeanUtils.copyProperties(userDTO, user);
        return user;
    }

    public UserDTO convertUserToUserDTO(User user){
        UserDTO userDTO= new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }





}
