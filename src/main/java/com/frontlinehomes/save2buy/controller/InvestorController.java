package com.frontlinehomes.save2buy.controller;
import com.frontlinehomes.save2buy.data.land.data.InvestorLand;
import com.frontlinehomes.save2buy.data.users.Gender;
import com.frontlinehomes.save2buy.data.users.Phone;
import com.frontlinehomes.save2buy.data.users.User;
import com.frontlinehomes.save2buy.data.users.investor.data.Investor;
import com.frontlinehomes.save2buy.data.users.investor.request.InvestorDTO;
import com.frontlinehomes.save2buy.data.users.investor.response.InvestorResponseDTO;
import com.frontlinehomes.save2buy.data.users.request.LoginDTO;
import com.frontlinehomes.save2buy.data.users.request.SignUpDTO;
import com.frontlinehomes.save2buy.data.users.response.StatusResponseDTO;
import com.frontlinehomes.save2buy.events.OnRegistrationCompleteEvent;
import com.frontlinehomes.save2buy.exception.EntityDuplicationException;
import com.frontlinehomes.save2buy.exception.NotNullFieldException;
import com.frontlinehomes.save2buy.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpMethod;
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

    /**
     *
     *
     * @param signUpDTO
     * @param request
     * @return ResponseEntity<SignUpDTO>
     *
     *     creates  a new Investor
     */

    @CrossOrigin
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
                     eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user1, "http:save2buy.ng/page-confirm-mail/"));
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




    @CrossOrigin( allowedHeaders = {"Authorization", "Content-Type"}, methods = {RequestMethod.POST})
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



    @CrossOrigin( allowedHeaders = {"Authorization"})
    @GetMapping("/{email}")
    public ResponseEntity<InvestorResponseDTO> getInvestorDetails(@PathVariable String email){
        //fetch the
        try{
            User user =userService.getUserByEmail(email);
            if(user==null){
                throw  new ResponseStatusException(HttpStatus.NOT_FOUND, "Investor not found with email:"+email);
            }
            return new ResponseEntity<InvestorResponseDTO>(convertUserToInvestorResponseDTO(user),HttpStatus.OK);
        }catch (NoSuchElementException e){
            throw  new ResponseStatusException(HttpStatus.NOT_FOUND, "Investor not found with email:"+email);
        }
    }

   /* @CrossOrigin( allowedHeaders = {"Authorization"} )
    @GetMapping("/{id}")
    public ResponseEntity<InvestorDTO> getInvestorDetails(@PathVariable Long id){
        //fetch the
        try{
            User user =userService.getUser(id);
            if(user==null){
                throw  new ResponseStatusException(HttpStatus.NOT_FOUND, "Investor not found with id:"+ id);
            }
            return new ResponseEntity<InvestorDTO>(convertUserToInvestorDTO(user),HttpStatus.OK);
        }catch (NoSuchElementException e){
            throw  new ResponseStatusException(HttpStatus.NOT_FOUND, "Investor not found with id:"+id);
        }
    }*/


    @CrossOrigin(allowedHeaders = {"Authorization", "Content-Type"}, methods = {RequestMethod.PUT})
    @PutMapping("/{id}")
    public ResponseEntity<InvestorResponseDTO> updateInvestor(@PathVariable Long id, @RequestBody InvestorDTO investorDTO) {
        log.info("InvestorController:updateInvestor: updating investor details with id"+id);
        try{
            User user= userService.getUser(id);
            Investor investor= user.getInvestor();
            BeanUtils.copyProperties(investorDTO, investor, CopyUtils.getNullPropertyNames(investorDTO));
            //get  the user's phone line
            if(investorDTO.getPrimaryLine() != null){
                //create a phone object
                Phone phone = new Phone();
                phone.setPhone(investorDTO.getPrimaryLine());
                user.addPhone(phone);
            }
            BeanUtils.copyProperties(investorDTO, user,CopyUtils.getNullPropertyNames(investorDTO));
            userService.saveUser(user);
            return new ResponseEntity<InvestorResponseDTO>(convertUserToInvestorResponseDTO(user),HttpStatus.OK);
        }catch (NoSuchElementException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id"+id+" not found");
        }
    }

    /**
     *
     *
     * @param email
     * @return
     *
     *  The endpoint check if user email is verified and user profile is completed
     */

    @CrossOrigin( allowedHeaders = {"Authorization"})
    @GetMapping("/status/{email}")
    public ResponseEntity<StatusResponseDTO> checkProfileStatus(@PathVariable String email){
        //check if the user profile is verified
        try {
            User user = userService.getUserByEmail(email);
            if(user!= null){
                StatusResponseDTO statusResponseDTO= new StatusResponseDTO();
                statusResponseDTO.setId(user.getId());
                statusResponseDTO.setEmailStatus(user.getEnabled());

                //check if the user has completed his email
                if(user.getFirstName()==null || user.getLastName()== null || user.getInvestor().getNextOfKinName()== null || user.getPhone()== null){
                    statusResponseDTO.setProfileStatus(false);
                }else{
                    statusResponseDTO.setProfileStatus(true);
                }
                return new ResponseEntity<>(statusResponseDTO, HttpStatus.OK);
            }else{
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
            }
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }




    private InvestorResponseDTO convertUserToInvestorResponseDTO(User user){
        InvestorResponseDTO investorResponseDTO= new InvestorResponseDTO();
        BeanUtils.copyProperties(user, investorResponseDTO);
        //copy the from Investor object investorDTO
        BeanUtils.copyProperties(user.getInvestor(), investorResponseDTO);
        String line=null;
        //set the primaryline
         if(!user.getPhone().isEmpty()){
             line= user.getPhone().get(0).getPhone();
         }
         investorResponseDTO.setPrimaryLine(line);
        investorResponseDTO.setId(user.getId());
        return investorResponseDTO;
    }



    private InvestorDTO convertUserToInvestorDTO(User user){
        InvestorDTO investorDTO= new InvestorDTO();
        BeanUtils.copyProperties(user, investorDTO);
        String line=null;
        //check if user phone exist
        if(!user.getPhone().isEmpty()){
            line= user.getPhone().get(0).getPhone();
        }
        investorDTO.setPrimaryLine(line);
        //copy the from Investor object investorDTO
        BeanUtils.copyProperties(user.getInvestor(), investorDTO);
        return investorDTO;
    }

    private User convertInvestorDTOtoUser(InvestorDTO investorDTO){
        User user= new User();
        Investor investor= new Investor();
        BeanUtils.copyProperties(investorDTO, user);
        BeanUtils.copyProperties(investorDTO, investor);
        //get phone provided
        Phone phone= new Phone();
        if(investorDTO.getPrimaryLine()!=null){
            phone.setPhone(investorDTO.getPrimaryLine());
            user.addPhone(phone);
        }
        user.addInvestor(investor);
        return user;
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
