package com.frontlinehomes.save2buy.controller;
import com.frontlinehomes.save2buy.data.ResponseDTO;
import com.frontlinehomes.save2buy.data.ResponseStatus;
import com.frontlinehomes.save2buy.data.users.Phone;
import com.frontlinehomes.save2buy.data.users.User;
import com.frontlinehomes.save2buy.data.users.investor.data.Investor;
import com.frontlinehomes.save2buy.data.users.investor.request.InvestorDTO;
import com.frontlinehomes.save2buy.data.users.investor.response.InvestorResponseDTO;
import com.frontlinehomes.save2buy.data.users.request.SignUpDTO;
import com.frontlinehomes.save2buy.data.users.response.StatusResponseDTO;
import com.frontlinehomes.save2buy.events.registration.OnRegistrationCompleteEvent;
import com.frontlinehomes.save2buy.exception.EntityDuplicationException;
import com.frontlinehomes.save2buy.exception.NotNullFieldException;
import com.frontlinehomes.save2buy.service.*;
import com.frontlinehomes.save2buy.service.file.FileSystemStorageService;
import com.frontlinehomes.save2buy.service.utils.DTOUtility;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;


@RestController
@RequestMapping("/investor")
public class InvestorController {
    @Autowired
    UserService userService;
    @Autowired
    InvestorService investorService;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    FileSystemStorageService fileSystemStorageService;
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
    public ResponseEntity<ResponseDTO<SignUpDTO>> createInvestor(@RequestBody SignUpDTO signUpDTO, HttpServletRequest request){
        //check for all required fields
        if(signUpDTO.getEmail()== null){
            return new ResponseEntity< ResponseDTO<SignUpDTO>>( new ResponseDTO<SignUpDTO>(ResponseStatus.Error,"Email is required"),HttpStatus.BAD_REQUEST);
        }
        //validate the email
        EmailValidator validator = EmailValidator.getInstance();

        if(!(validator.isValid(signUpDTO.getEmail()))){
            return new ResponseEntity< ResponseDTO<SignUpDTO>>( new ResponseDTO<SignUpDTO>(ResponseStatus.Error,"Enter a valid Email"),HttpStatus.BAD_REQUEST);
        }



        try{
            //check if password match
            if(signUpDTO.getPassword()!= null || signUpDTO.getConfirmPassword() != null){
                if(signUpDTO.getPassword().equals( signUpDTO.getConfirmPassword())) {
                    User user = DTOUtility.convertSignUpDTOtoUser(signUpDTO);

                    //check if phone was provided
                    Phone phone= new Phone();
                    if(signUpDTO.getPrimaryLine()!=null){
                        phone.setPhone(signUpDTO.getPrimaryLine());
                        user.addPhone(phone);
                    }

                    //harsh user's password
                    user.setPassword(HarshService.getSecuredPassword(signUpDTO.getPassword()));
                    user.addInvestor(new Investor());
                    User user1= userService.saveUser(user);
                    SignUpDTO newDTO=  DTOUtility.convertUserToSigUpDTO(user1);
                    signUpDTO.setPassword(null);
                    signUpDTO.setConfirmPassword(null);
                    String appUrl = request.getContextPath();
                    //public event
                     eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user1, "https:save2buy.ng/page-confirm-mail/"));
                    log.info("InvestorController:createInvestor : investor created with email "+user1.getEmail());
                    SignUpDTO signUpDTO1=  DTOUtility.convertUserToSigUpDTO(user1);

                    ResponseDTO<SignUpDTO> responseDTO= new ResponseDTO<SignUpDTO>(ResponseStatus.Success,"Successful");
                    responseDTO.setBody(signUpDTO1);
                    return new ResponseEntity< ResponseDTO<SignUpDTO>>( responseDTO,HttpStatus.OK);
                }else{
                    return new ResponseEntity< ResponseDTO<SignUpDTO>>( new ResponseDTO<SignUpDTO>(ResponseStatus.Error,"Password does not match"),HttpStatus.FORBIDDEN);
                }
            }else{
                return new ResponseEntity< ResponseDTO<SignUpDTO>>( new ResponseDTO<SignUpDTO>(ResponseStatus.Error,"Password is required"),HttpStatus.BAD_REQUEST);
            }
        }catch (EntityDuplicationException exec){
            log.error("InvestorController:createInvestor : conflicts occurred creating user");
            return new ResponseEntity< ResponseDTO<SignUpDTO>>( new ResponseDTO<SignUpDTO>(ResponseStatus.Error,"User with email "+signUpDTO.getEmail()+" already exist"),HttpStatus.CONFLICT);

        }catch (NotNullFieldException e){
            return new ResponseEntity< ResponseDTO<SignUpDTO>>( new ResponseDTO<SignUpDTO>(ResponseStatus.Error, e.getMessage()),HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            log.error("InvestorController: createInvestor:  "+e.getMessage());
            return new ResponseEntity< ResponseDTO<SignUpDTO>>( new ResponseDTO<SignUpDTO>(ResponseStatus.Error,"User with email "+signUpDTO.getEmail()+" already exist"),HttpStatus.CONFLICT);
        }
    }





    @CrossOrigin( allowedHeaders = {"Authorization", "Content-Type"}, methods = {RequestMethod.POST})
    @PostMapping("/{userId}")
    public ResponseEntity<InvestorDTO> createInvestor(@RequestBody InvestorDTO investorDTO, @PathVariable Long userId){
        try{
            User user= userService.getUser(userId);
            Investor investor=  DTOUtility.convertInvestorDTOtoInvestor(investorDTO);
            user.addInvestor(investor);
            Investor investor1= investorService.addInvestor(investor);
          return  new  ResponseEntity<InvestorDTO>( DTOUtility.convertInvestorToInvestorDTO(investor1), HttpStatus.OK);
        }catch(NoSuchElementException e){
             throw   new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id "+userId+" not found");
        }
    }



    @CrossOrigin(allowedHeaders = {"Authorization"})
    @GetMapping("/{email}")
    public ResponseEntity<InvestorResponseDTO> getInvestorDetails(@PathVariable String email){
        //fetch the
        try{
            User user =userService.getUserByEmail(email);
            if(user==null){
                throw  new ResponseStatusException(HttpStatus.NOT_FOUND, "Investor not found with email:"+email);
            }
            return new ResponseEntity<InvestorResponseDTO>( DTOUtility.convertUserToInvestorResponseDTO(user),HttpStatus.OK);
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
            return new ResponseEntity<InvestorResponseDTO>( DTOUtility.convertUserToInvestorResponseDTO(user),HttpStatus.OK);
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

    @CrossOrigin( allowedHeaders = {"Authorization", "Content-Type"}, methods = {RequestMethod.POST})
    @PostMapping("/passport/{id}")
    public ResponseEntity saveInvestorPassport(@RequestParam("file") MultipartFile file, @PathVariable Long id){


        User user= null;
        try{
            //check if the user exist
           user = userService.getUser(id);
        }catch (NoSuchElementException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
        }

        try{
            if(file.isEmpty()){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File cannot be empty");
            }

            String url=fileSystemStorageService.store(file, "user_passport_"+id+".jpg", "passport");

            //persist the url in the database

            user.getInvestor().setPassportUrl(url);
            userService.saveUser(user);


            log.info("FileSystemStorageService:store: image uploaded successfully");
            return ResponseEntity.ok().build();
        }catch (Exception e){
            log.warn("LandController:uploadImage:  error uploading image "+e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }


    @CrossOrigin( allowedHeaders = {"Authorization", "Content-Type"}, methods = {RequestMethod.POST})
    @PostMapping("/identification/{id}")
    public ResponseEntity saveInvestorCredential(@RequestParam("file") MultipartFile file, @PathVariable Long id){


        User user= null;
        try{
            //check if the user exist
          user = userService.getUser(id);
        }catch (NoSuchElementException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
        }

        try{
            if(file.isEmpty()){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File cannot be empty");
            }

            String url=fileSystemStorageService.store(file, "user_passport_"+id+".jpg", "credentials");

            //persist the url in the database
            user.getInvestor().setIdCardUrl(url);
            userService.saveUser(user);

            log.info("FileSystemStorageService:store: image uploaded successfully");
            return ResponseEntity.ok().build();
        }catch (Exception e){
            log.warn("LandController:uploadImage:  error uploading image "+e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }





}
