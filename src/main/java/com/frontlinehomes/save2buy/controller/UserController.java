package com.frontlinehomes.save2buy.controller;

import com.frontlinehomes.save2buy.data.ResponseDTO;
import com.frontlinehomes.save2buy.data.ResponseStatus;
import com.frontlinehomes.save2buy.data.users.*;
import com.frontlinehomes.save2buy.data.users.request.LoginDTO;
import com.frontlinehomes.save2buy.data.users.request.SignUpDTO;
import com.frontlinehomes.save2buy.data.users.request.UserDTO;
import com.frontlinehomes.save2buy.data.users.response.LoginResponseDTO;
import com.frontlinehomes.save2buy.service.JWTService;
import com.frontlinehomes.save2buy.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

/**
 *
 * Custom Errors originated from UserController starts with the code 1XX
 */
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

    @CrossOrigin(allowedHeaders = {"Authorization", "Content-Type"})
    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<LoginResponseDTO>> signin(@RequestBody LoginDTO loginDTO){
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO
                    .getPassword()));
            //get the specified user details
            ResponseDTO<LoginResponseDTO> responseDTO= new ResponseDTO<>(ResponseStatus.Success, "Successful");
            LoginResponseDTO loginResponseDTO= new LoginResponseDTO();
            loginResponseDTO.setToken(jwtService.getJWTString(authentication));

            //get the user's details
            User user= userService.getUserByEmail(loginDTO.getEmail());
            loginResponseDTO.setId(user.getId());
            responseDTO.setBody(loginResponseDTO);

            return new ResponseEntity<ResponseDTO<LoginResponseDTO>>(responseDTO, HttpStatus.OK);
        }catch (BadCredentialsException e){
            return new ResponseEntity<ResponseDTO<LoginResponseDTO>>( new ResponseDTO<>(ResponseStatus.Error, e.getMessage()), HttpStatus.FORBIDDEN);
        }catch (UsernameNotFoundException e){
            return new ResponseEntity<ResponseDTO<LoginResponseDTO>>( new ResponseDTO<>(ResponseStatus.Error, e.getMessage()), HttpStatus.NOT_FOUND);
        }catch (DisabledException e){
            return new ResponseEntity<ResponseDTO<LoginResponseDTO>>( new ResponseDTO<>(ResponseStatus.Error, e.getMessage()), HttpStatus.FORBIDDEN);
        }
    }


   /* @CrossOrigin( allowedHeaders = {"Authorization"})
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id){
        try{
            return new ResponseEntity<User>(userService.getUser(id), HttpStatus.OK);
        }catch (NoSuchElementException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User was not found");
        }
    } */

   /* @CrossOrigin( allowedHeaders = {"Authorization"})
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(){
        return new ResponseEntity<List<User>>(userService.getAllUser(), HttpStatus.OK);
    } */

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



}
