package com.frontlinehomes.save2buy.controller;
import com.frontlinehomes.save2buy.data.land.data.InvestorLand;
import com.frontlinehomes.save2buy.data.users.User;
import com.frontlinehomes.save2buy.data.users.investor.data.Investor;
import com.frontlinehomes.save2buy.data.users.investor.request.InvestorDTO;
import com.frontlinehomes.save2buy.data.users.request.LoginDTO;
import com.frontlinehomes.save2buy.data.users.request.SignUpDTO;
import com.frontlinehomes.save2buy.events.OnRegistrationCompleteEvent;
import com.frontlinehomes.save2buy.exception.EntityDuplicationException;
import com.frontlinehomes.save2buy.exception.NotNullFieldException;
import com.frontlinehomes.save2buy.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

@CrossOrigin
@RestController
@RequestMapping("/investor")
public class InvestorController {
    @Autowired
    UserService userService;
    @Autowired
    InvestorService investorService;

    @Autowired
    ApplicationEventPublisher eventPublisher;
    private static Logger log = LogManager.getLogger( InvestorController.class);

    @PostMapping("/create")
    public ResponseEntity<SignUpDTO> createInvestor(@RequestBody SignUpDTO signUpDTO, HttpServletRequest request){
        try{
            //check if password match
            if(signUpDTO.getPassword()!= null || signUpDTO.getConfirmPassword() != null){
                if(signUpDTO.getPassword().equals( signUpDTO.getConfirmPassword())) {
                    User user = convertSignUpDTOtoUser(signUpDTO);
                    user.setPassword(HarshService.getSecuredPassword(signUpDTO.getPassword()));
                    user.addInvestor(new Investor());
                    User user1= userService.saveUser(user);
                    SignUpDTO newDTO= convertUserToSigUpDTO(user1);
                    signUpDTO.setPassword(null);
                    signUpDTO.setConfirmPassword(null);
                    String appUrl = request.getContextPath();
                    //public event
                   // eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user1, appUrl));
                    log.info("InvestorController:createInvestor : investor created with email "+user1.getEmail());
                    SignUpDTO signUpDTO1=convertUserToSigUpDTO(user1);
                    signUpDTO1.setPassword(null);
                    return new ResponseEntity<SignUpDTO>( signUpDTO1,HttpStatus.OK);
                }else{
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Password does not match");
                }
            }else{
                throw  new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Password fields cannot be empty");
            }
        }catch (EntityDuplicationException exec){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User with email "+signUpDTO.getEmail()+" already exist"
            );
        }catch (NotNullFieldException e){
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, e.getMessage());
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User already exist");
        }
    }


    @PostMapping("/{userId}")
    public ResponseEntity<InvestorDTO> createInvestor(@RequestBody InvestorDTO investorDTO, @PathVariable Long userId){
        try{
            User user= userService.getUser(userId);
            Investor investor= convertInvestorDTOtoInvestor(investorDTO);
            user.addInvestor(investor);
            Investor investor1= investorService.addInvestor(investor);
          return  new  ResponseEntity<InvestorDTO>(convertInvestorToInvestorDTO(investor1), HttpStatus.OK);
        }catch(NoSuchElementException e){
             throw   new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id "+userId+" not found");
        }
    }




    @GetMapping("/{id}")
    public ResponseEntity<InvestorDTO> getInvestorDetails(@PathVariable Long id){
        //fetch the
        try{
            User user =userService.getUser(id);
            return new ResponseEntity<InvestorDTO>(convertInvestorToInvestorDTO(user.getInvestor()),HttpStatus.OK);
        }catch (NoSuchElementException e){
            throw  new ResponseStatusException(HttpStatus.NOT_FOUND, "Investor not found with id"+id);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<InvestorDTO> updateInvestor(@PathVariable Long id, @RequestBody InvestorDTO investorDTO) {

        try{
            User user= userService.getUser(id);
            Investor investor= user.getInvestor();
            BeanUtils.copyProperties(investorDTO, investor, CopyUtils.getNullPropertyNames(investorDTO));
            investorService.addInvestor(investor);
            return new ResponseEntity<InvestorDTO>(convertInvestorToInvestorDTO(investor),HttpStatus.OK);
        }catch (NoSuchElementException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id"+id+" not found");
        }

    }


    private InvestorDTO convertInvestorToInvestorDTO(Investor investor){
        InvestorDTO investorDTO= new InvestorDTO();
        BeanUtils.copyProperties(investor, investorDTO);
        return investorDTO;
    }


    private Investor convertInvestorDTOtoInvestor(InvestorDTO investorDTO){
        Investor investor= new Investor();
        BeanUtils.copyProperties(investorDTO, investor);
        return investor;
    }

    private User convertSignUpDTOtoUser(SignUpDTO signUpDTO){
        User user= new User();
        BeanUtils.copyProperties(signUpDTO, user);
        return user;
    }

    public SignUpDTO convertUserToSigUpDTO(User user){
        SignUpDTO signUpDTO= new SignUpDTO();
        BeanUtils.copyProperties(user, signUpDTO);
        return signUpDTO;
    }




}
