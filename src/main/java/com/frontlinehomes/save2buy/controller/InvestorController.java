package com.frontlinehomes.save2buy.controller;
import com.frontlinehomes.save2buy.data.ResponseDTO;
import com.frontlinehomes.save2buy.data.ResponseStatus;
import com.frontlinehomes.save2buy.data.account.data.Transaction;
import com.frontlinehomes.save2buy.data.account.data.TransactionStatus;
import com.frontlinehomes.save2buy.data.account.response.InitTransactionResponseDTO;
import com.frontlinehomes.save2buy.data.account.response.TransactionResponseDTO;
import com.frontlinehomes.save2buy.data.land.data.*;
import com.frontlinehomes.save2buy.data.land.response.InvestorLandDetailsDTO;
import com.frontlinehomes.save2buy.data.land.response.InvestorLandOverview;
import com.frontlinehomes.save2buy.data.land.response.MileStoneResponseDTO;
import com.frontlinehomes.save2buy.data.users.Phone;
import com.frontlinehomes.save2buy.data.users.User;
import com.frontlinehomes.save2buy.data.users.investor.data.Investor;
import com.frontlinehomes.save2buy.data.users.investor.request.InvestorDTO;
import com.frontlinehomes.save2buy.data.users.investor.response.InvestorResponseDTO;
import com.frontlinehomes.save2buy.data.users.request.SignUpDTO;
import com.frontlinehomes.save2buy.data.users.response.StatusResponseDTO;
import com.frontlinehomes.save2buy.data.verification.JwtDetails;
import com.frontlinehomes.save2buy.events.registration.OnRegistrationCompleteEvent;
import com.frontlinehomes.save2buy.exception.EntityDuplicationException;
import com.frontlinehomes.save2buy.exception.NotNullFieldException;
import com.frontlinehomes.save2buy.service.*;
import com.frontlinehomes.save2buy.service.file.FileSystemStorageService;
import com.frontlinehomes.save2buy.service.investorLand.InvestorLandService;
import com.frontlinehomes.save2buy.service.utils.DTOUtility;
import jakarta.servlet.http.HttpServletRequest;
import okhttp3.Response;
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

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 *
 * Custom Errors originated from InvestorController starts with the code 2XX
 */
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
    @Autowired
    InvestorLandService investorLandService;

    @Autowired
    JWTService jwtService;
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
            return new ResponseEntity< ResponseDTO<SignUpDTO>>( new ResponseDTO<SignUpDTO>(ResponseStatus.Error,20,"Email is required"),HttpStatus.BAD_REQUEST);
        }
        //validate the email
        EmailValidator validator = EmailValidator.getInstance();

        if(!(validator.isValid(signUpDTO.getEmail()))){
            return new ResponseEntity< ResponseDTO<SignUpDTO>>( new ResponseDTO<SignUpDTO>(ResponseStatus.Error,21,"Enter a valid Email"),HttpStatus.BAD_REQUEST);
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
                     eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user1, "https:save2buy.ng/page-confirm-mail/", "email-verification"));
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
    public ResponseEntity<ResponseDTO<InvestorDTO>> createInvestor(@RequestBody InvestorDTO investorDTO, @PathVariable Long userId, @RequestHeader("Authorization") String token){
        try{
           // JwtDetails details =jwtService.getTokenDetails(token);
            User user= userService.getUser(userId);

            if(user.getInvestor()== null) return  new ResponseEntity<ResponseDTO<InvestorDTO>>(new ResponseDTO<InvestorDTO>(ResponseStatus.Error, "Account is not an Investor's account"), HttpStatus.BAD_REQUEST);
           /* if(details.getIsAdmin()){
                //this is an admin
            } else {
                //investors should access only their resources
                if(!user.getEmail().equals(details.getUsername()))  return  new ResponseEntity<ResponseDTO<InvestorDTO>>(new ResponseDTO<InvestorDTO>(ResponseStatus.Error, "invalid access token"), HttpStatus.UNAUTHORIZED);
            } */
            Investor investor=  DTOUtility.convertInvestorDTOtoInvestor(investorDTO);
            user.addInvestor(investor);
            Investor investor1= investorService.addInvestor(investor);
            ResponseDTO<InvestorDTO> responseDTO= new ResponseDTO<>(ResponseStatus.Success, "Successful");
            responseDTO.setBody(DTOUtility.convertInvestorToInvestorDTO(investor1));
          return  new ResponseEntity<ResponseDTO<InvestorDTO>>( responseDTO, HttpStatus.OK);
        }catch(NoSuchElementException e){
            return  new ResponseEntity<ResponseDTO<InvestorDTO>>(new ResponseDTO<InvestorDTO>(ResponseStatus.Error, "user with id "+userId+" not found"), HttpStatus.NOT_FOUND);
        }
    }



    @CrossOrigin(allowedHeaders = {"Authorization"})
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<InvestorResponseDTO>> getInvestorDetails(@PathVariable Long id, @RequestHeader("Authorization") String token){
        //fetch the
        try{
            User user =userService.getUser(id);

            if(user.getInvestor()== null) return  new ResponseEntity<ResponseDTO<InvestorResponseDTO>>(new ResponseDTO<InvestorResponseDTO>(ResponseStatus.Error, "Account is not an Investor's account"), HttpStatus.BAD_REQUEST);
           /* JwtDetails details =jwtService.getTokenDetails(token);
            if(details.getIsAdmin()){
                //this is an admin
            } else {
                //investors should access only their resources
                if(!user.getEmail().equals(details.getUsername()))  return  new ResponseEntity<ResponseDTO<InvestorResponseDTO>>(new ResponseDTO<InvestorResponseDTO>(ResponseStatus.Error, "invalid access token"), HttpStatus.UNAUTHORIZED);
            } */

            if(user==null){
                return  new ResponseEntity<ResponseDTO<InvestorResponseDTO>>(new ResponseDTO<InvestorResponseDTO>(ResponseStatus.Error, "user with id: "+id+" not found"), HttpStatus.NOT_FOUND);
            }

            ResponseDTO<InvestorResponseDTO> responseDTO= new ResponseDTO<>(ResponseStatus.Success, "Successful");
            responseDTO.setBody(DTOUtility.convertUserToInvestorResponseDTO(user));
            return new ResponseEntity<ResponseDTO<InvestorResponseDTO>>(responseDTO,HttpStatus.OK);
        }catch (NoSuchElementException e){
            return  new ResponseEntity<ResponseDTO<InvestorResponseDTO>>(new ResponseDTO<InvestorResponseDTO>(ResponseStatus.Error, "user with id:"+id+" not found"), HttpStatus.NOT_FOUND);
        }
    }

    @CrossOrigin(allowedHeaders = {"Authorization"})
    @GetMapping
    public ResponseEntity<ResponseDTO> getAllInvestors(){
        //get the list of all investors
       List<Investor> investors= investorService.getAllInvestors();
       List<InvestorResponseDTO> investorResponseDTOS= new ArrayList<>();
        for (Investor investor : investors) {
            investorResponseDTOS.add(DTOUtility.convertUserToInvestorResponseDTO(investor.getUser()));
        }
        ResponseDTO responseDTO= new ResponseDTO(ResponseStatus.Success, "Successful");
        responseDTO.setBody(investorResponseDTOS);
        return new ResponseEntity<ResponseDTO>(responseDTO, HttpStatus.OK);
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
    public ResponseEntity<ResponseDTO<InvestorResponseDTO>> updateInvestor(@PathVariable Long id, @RequestBody InvestorDTO investorDTO, @RequestHeader("Authorization") String token) {
        log.info("InvestorController:updateInvestor: updating investor details with id"+id);
        try{
            User user= userService.getUser(id);

            if(user.getInvestor()== null) return  new ResponseEntity<ResponseDTO<InvestorResponseDTO>>(new ResponseDTO<InvestorResponseDTO>(ResponseStatus.Error, "Account is not an Investor's account"), HttpStatus.BAD_REQUEST);
           /* JwtDetails details =jwtService.getTokenDetails(token);
            if(details.getIsAdmin()){
                //this is an admin
            } else {
                //investors should access only their resources
                if(!user.getEmail().equals(details.getUsername()))  return  new ResponseEntity<ResponseDTO<InvestorResponseDTO>>(new ResponseDTO<InvestorResponseDTO>(ResponseStatus.Error, "invalid access token"), HttpStatus.UNAUTHORIZED);
            } */

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
            user=userService.saveUser(user);
            ResponseDTO<InvestorResponseDTO> responseDTO= new ResponseDTO<>(ResponseStatus.Success, "Successful");
            responseDTO.setBody(DTOUtility.convertUserToInvestorResponseDTO(user));
            return new ResponseEntity<ResponseDTO<InvestorResponseDTO>>(responseDTO,HttpStatus.OK);
        }catch (NoSuchElementException e){
            return  new ResponseEntity<ResponseDTO<InvestorResponseDTO>>(new ResponseDTO<InvestorResponseDTO>(ResponseStatus.Error, "user with id:"+id+" not found"), HttpStatus.NOT_FOUND);
        }
    }

    /**
     *
     *
     * @param id
     * @return
     *
     *  The endpoint check if user email is verified and user profile is completed
     */

    @CrossOrigin( allowedHeaders = {"Authorization"})
    @GetMapping("/status/{id}")
    public ResponseEntity<ResponseDTO<StatusResponseDTO>> checkProfileStatus(@PathVariable Long id,  @RequestHeader("Authorization") String token){
        //check if the user profile is verified
        try {
            User user = userService.getUser(id);

            if(user.getInvestor()== null) return  new ResponseEntity<ResponseDTO<StatusResponseDTO>>(new ResponseDTO<StatusResponseDTO>(ResponseStatus.Error, "Account is not an Investor's account"), HttpStatus.BAD_REQUEST);

           /* JwtDetails details =jwtService.getTokenDetails(token);
            if(details.getIsAdmin()){
                //this is an admin
            } else {
                //investors should access only their resources
                if(!user.getEmail().equals(details.getUsername()))  return  new ResponseEntity<ResponseDTO<StatusResponseDTO>>(new ResponseDTO<StatusResponseDTO>(ResponseStatus.Error, "invalid access token"), HttpStatus.UNAUTHORIZED);
            } */

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
                ResponseDTO<StatusResponseDTO> responseDTO= new ResponseDTO<>(ResponseStatus.Success, "Successful");
                responseDTO.setBody(statusResponseDTO);
                return new ResponseEntity<ResponseDTO<StatusResponseDTO>>(responseDTO, HttpStatus.OK);
            }else{
                return  new ResponseEntity<ResponseDTO<StatusResponseDTO>>(new ResponseDTO<StatusResponseDTO>(ResponseStatus.Error, "user not found"), HttpStatus.NOT_FOUND);
            }
        }catch (Exception e){
            return  new ResponseEntity<ResponseDTO<StatusResponseDTO>>(new ResponseDTO<StatusResponseDTO>(ResponseStatus.Error, "user not found"), HttpStatus.NOT_FOUND);
        }
    }

    @CrossOrigin( allowedHeaders = {"Authorization", "Content-Type"})
    @PostMapping("/passport/{id}")
    public ResponseEntity<ResponseDTO> saveInvestorPassport(@RequestParam("file") MultipartFile file, @PathVariable Long id, @RequestHeader("Authorization") String token){


        User user= null;
        try{
            //check if the user exist
           user = userService.getUser(id);
        }catch (NoSuchElementException e){
            return  new ResponseEntity<ResponseDTO>(new ResponseDTO<StatusResponseDTO>(ResponseStatus.Error, "user not found"), HttpStatus.NOT_FOUND);
        }

        if(user.getInvestor()== null) return  new ResponseEntity<ResponseDTO>(new ResponseDTO(ResponseStatus.Error, "Account is not an Investor's account"), HttpStatus.BAD_REQUEST);

      /*  JwtDetails details =jwtService.getTokenDetails(token);
        if(details.getIsAdmin()){
            //this is an admin
        } else {
            //investors should access only their resources
            if(!user.getEmail().equals(details.getUsername()))  return  new ResponseEntity<ResponseDTO>(new ResponseDTO<StatusResponseDTO>(ResponseStatus.Error, "invalid access token"), HttpStatus.UNAUTHORIZED);
        } */

        try{
            if(file.isEmpty()){
                return  new ResponseEntity<ResponseDTO>(new ResponseDTO<StatusResponseDTO>(ResponseStatus.Error, "file cannot be empty"), HttpStatus.BAD_REQUEST);
            }

            String url=fileSystemStorageService.store(file, "user_passport_"+id+".jpg", "passport");

            //persist the url in the database

            user.getInvestor().setPassportUrl(url);
            userService.saveUser(user);


            log.info("FileSystemStorageService:store: image uploaded successfully");
            return  new ResponseEntity<ResponseDTO>(new ResponseDTO<StatusResponseDTO>(ResponseStatus.Success, "Successful"), HttpStatus.OK);
        }catch (Exception e){
            log.warn("LandController:uploadImage:  error uploading image "+e.getMessage());
            return  new ResponseEntity<ResponseDTO>(new ResponseDTO<StatusResponseDTO>(ResponseStatus.Error, "internal server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }


    @CrossOrigin( allowedHeaders = {"Authorization", "Content-Type"})
    @PostMapping("/identification/{id}")
    public ResponseEntity<ResponseDTO> saveInvestorCredential(@RequestParam("file") MultipartFile file, @PathVariable Long id, @RequestHeader("Authorization") String token){

        User user= null;
        try{
            //check if the user exist
          user = userService.getUser(id);
        }catch (NoSuchElementException e){
            return  new ResponseEntity<ResponseDTO>(new ResponseDTO<StatusResponseDTO>(ResponseStatus.Error, "user not found"), HttpStatus.NOT_FOUND);
        }

        if(user.getInvestor()== null) return  new ResponseEntity<ResponseDTO>(new ResponseDTO(ResponseStatus.Error, "Account is not an Investor's account"), HttpStatus.BAD_REQUEST);

      /**  JwtDetails details =jwtService.getTokenDetails(token);
        if(details.getIsAdmin()){
            //this is an admin
        } else {
            //investors should access only their resources
            if(!user.getEmail().equals(details.getUsername()))  return  new ResponseEntity<ResponseDTO>(new ResponseDTO<StatusResponseDTO>(ResponseStatus.Error, "invalid access token"), HttpStatus.UNAUTHORIZED);
        } */

        try{
            if(file.isEmpty()){
                return  new ResponseEntity<ResponseDTO>(new ResponseDTO<StatusResponseDTO>(ResponseStatus.Error, "file cannot be empty"), HttpStatus.BAD_REQUEST);
            }

            String url=fileSystemStorageService.store(file, "user_passport_"+id+".jpg", "credentials");

            //persist the url in the database
            user.getInvestor().setIdCardUrl(url);
            userService.saveUser(user);

            log.info("FileSystemStorageService:store: image uploaded successfully");
            return  new ResponseEntity<ResponseDTO>(new ResponseDTO<StatusResponseDTO>(ResponseStatus.Success, "Successful"), HttpStatus.OK);
        }catch (Exception e){
            log.warn("LandController:uploadImage:  error uploading image "+e.getMessage());
            return  new ResponseEntity<ResponseDTO>(new ResponseDTO<StatusResponseDTO>(ResponseStatus.Error, "internal server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


    @GetMapping("/{userId}/lands/overview/{id}")
    @CrossOrigin(allowedHeaders = {"Authorization"})
    public ResponseEntity<ResponseDTO<InvestorLandOverview>> getInvestorLandOverview(@PathVariable Long userId,@PathVariable Long id){

        //get the investors land
        try{
            //get the user with  the specified userId.
            User user= userService.getUser(userId);

            if(user.getInvestor()== null) return  new ResponseEntity<ResponseDTO<InvestorLandOverview>>(new ResponseDTO<InvestorLandOverview>(ResponseStatus.Error, "Account is not an Investor's account"), HttpStatus.BAD_REQUEST);
            InvestorLand investorLand= null;
            for (InvestorLand land : user.getInvestor().getInvestorLands()) {
                if(land.getId().equals(id)){
                    investorLand= land;
                }
            }

            if(investorLand== null) return new ResponseEntity<ResponseDTO<InvestorLandOverview>>( new ResponseDTO<>(ResponseStatus.Error, "User Land not found"), HttpStatus.NOT_FOUND);

            Double paid=0.0;

            //get all transactions for the investors land
            for (Transaction transaction : investorLand.getTransactionList()) {
                //check the transaction status if is successfull.
                if(transaction.getTransactionStatus().equals(TransactionStatus.Successful)){
                    paid+= transaction.getAmount();
                }
            }


            List payRate= new ArrayList();
            payRate.add(( paid/investorLand.getAmount()) * 100);

            InvestorLandOverview overview= new InvestorLandOverview(investorLand.getId(), investorLand.getLand().getTitle(), investorLand.getAmount(), paid,(investorLand.getAmount() - paid),payRate, investorLand.getMilestone());
            ResponseDTO<InvestorLandOverview> responseDTO= new ResponseDTO<>(ResponseStatus.Success, "Successful");
            responseDTO.setBody(overview);
            return new ResponseEntity<ResponseDTO<InvestorLandOverview>>(responseDTO, HttpStatus.OK);

        }catch (NoSuchElementException e){
            return new ResponseEntity<ResponseDTO<InvestorLandOverview>>( new ResponseDTO<>(ResponseStatus.Error, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{userId}/milestone/{purchaseId}")
    @CrossOrigin(allowedHeaders = {"Authorization"})
    public ResponseEntity<ResponseDTO<MileStoneResponseDTO>> getMilestone(@PathVariable Long userId , @PathVariable Long purchaseId){
        //get the user
        try {
            User user= userService.getUser(userId);

            if(user.getInvestor()== null) return  new ResponseEntity<ResponseDTO<MileStoneResponseDTO>>(new ResponseDTO<MileStoneResponseDTO>(ResponseStatus.Error, "Account is not an Investor's account"), HttpStatus.BAD_REQUEST);

            InvestorLand investorLand= null;

            for (InvestorLand land : user.getInvestor().getInvestorLands()) {
                if(land.getId().equals(purchaseId)){
                    investorLand= land;
                }
            }

            if(investorLand == null) return new ResponseEntity<ResponseDTO<MileStoneResponseDTO>>(new ResponseDTO<>(ResponseStatus.Error, "User's Land not found"), HttpStatus.NOT_FOUND);

            MileStoneResponseDTO mileStoneResponseDTO= new MileStoneResponseDTO();
            mileStoneResponseDTO.setId(investorLand.getId());
            mileStoneResponseDTO.setTitle(investorLand.getLand().getTitle());
            mileStoneResponseDTO.setMilestone(investorLand.getMilestone());


            ResponseDTO<MileStoneResponseDTO> responseDTO= new ResponseDTO<>(ResponseStatus.Success, "Successful");
            responseDTO.setBody(mileStoneResponseDTO);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        }catch (NoSuchElementException e){
            return new ResponseEntity<ResponseDTO<MileStoneResponseDTO>>( new ResponseDTO<>(ResponseStatus.Error, e.getMessage()), HttpStatus.NOT_FOUND);
        }

    }



   @GetMapping("/{id}/lands")
   @CrossOrigin(allowedHeaders = {"Authorization"})
   public ResponseEntity<ResponseDTO<List<InvestorLandDetailsDTO>>> getAllInvestorLand(@PathVariable Long id){

            try{
                //return all the list of investors lands
              //  List<InvestorLand> investorLandList= investorLandService.getAllInitiatedInvestorLandByID(id);

                User user= userService.getUser(id);
                if(user.getInvestor()== null) return  new ResponseEntity<ResponseDTO<List<InvestorLandDetailsDTO>>>(new ResponseDTO<List<InvestorLandDetailsDTO>>(ResponseStatus.Error, "Account is not an Investor's account"), HttpStatus.BAD_REQUEST);

                List<InvestorLand> investorLandList= user.getInvestor().getInvestorLands();
                List<InvestorLandDetailsDTO> investorLandDetailsDTOList= new ArrayList<InvestorLandDetailsDTO>();
                for (InvestorLand investorLand : investorLandList) {
                    //get only pending, Initiated, acquired and withheld lands
                    if(investorLand.getLandStatus().equals(LandStatus.Initiated) || investorLand.getLandStatus().equals(LandStatus.Acquired) || investorLand.getLandStatus().equals(LandStatus.Withheld)){
                        investorLandDetailsDTOList.add(DTOUtility.convertInvestorLandToInvestorLandDetailsDTO(investorLand));
                    }
                }

                ResponseDTO<List<InvestorLandDetailsDTO>> responseDTO= new ResponseDTO<>(ResponseStatus.Success, "Successful");
                responseDTO.setBody(investorLandDetailsDTOList);
                return new ResponseEntity<ResponseDTO<List<InvestorLandDetailsDTO>>>(responseDTO, HttpStatus.OK);

            }catch (NoSuchElementException e){
                return new ResponseEntity<ResponseDTO<List<InvestorLandDetailsDTO>>>(new ResponseDTO<>(ResponseStatus.Error, e.getMessage()), HttpStatus.NOT_FOUND);
            }

    }

    @CrossOrigin(allowedHeaders = {"Authorization"} )
    @GetMapping("/verify-docs/{id}")
    public ResponseEntity<ResponseDTO> verifyUploadedDoc(@PathVariable Long id){
        try{
            User user= userService.getUser(id);

            if(user.getInvestor() == null) return new ResponseEntity<>(new  ResponseDTO<InitTransactionResponseDTO>(ResponseStatus.Error, "Account is not an investor account"), HttpStatus.NOT_ACCEPTABLE);

            //check if user's credentials and passport photograph has been uploaded
            if(user.getInvestor().getIdCardUrl()== null) return new ResponseEntity<>(new  ResponseDTO(ResponseStatus.Error, "upload a valid identification credential"), HttpStatus.NOT_ACCEPTABLE);

            //check if user's credentials and passport photograph has been uploaded
            if(user.getInvestor().getPassportUrl()== null) return new ResponseEntity<>(new  ResponseDTO(ResponseStatus.Error, "upload a recent passport photograph"), HttpStatus.NOT_ACCEPTABLE);

            //check if user profile has been set
            if(user.getFirstName()== null || user.getLastName()== null || user.getInvestor().getNextOfKinName() == null ){
                return new ResponseEntity<>(new  ResponseDTO(ResponseStatus.Error, "user's profile not complete"), HttpStatus.NOT_ACCEPTABLE);
            }

            if(!user.getEnabled()) return new ResponseEntity<>(new  ResponseDTO(ResponseStatus.Error, "user's  email not verified"), HttpStatus.NOT_ACCEPTABLE);

            return new ResponseEntity<>(new  ResponseDTO(ResponseStatus.Error,"Completed profile" ), HttpStatus.OK);

        }catch (NoSuchElementException e){
            return new ResponseEntity<>(new  ResponseDTO(ResponseStatus.Error, e.getMessage()), HttpStatus.NOT_FOUND);
        }


    }


    @CrossOrigin(allowedHeaders = {"Authorization"} )
    @GetMapping("/{userId}/lands/{id}")
    public ResponseEntity<ResponseDTO<InvestorLandDetailsDTO>> getInvestorLandDetails(@PathVariable Long userId, @PathVariable Long id){
        try{
            //get the investor's land
            User user= userService.getUser(userId);
            if(user.getInvestor()== null) return  new ResponseEntity<ResponseDTO<InvestorLandDetailsDTO>>(new ResponseDTO<InvestorLandDetailsDTO>(ResponseStatus.Error, "Account is not an Investor's account"), HttpStatus.BAD_REQUEST);
            InvestorLand investorLand=null;
            for (InvestorLand land : user.getInvestor().getInvestorLands()) {
                if(land.getId().equals(id)){
                    investorLand= land;
                }
            }
            if (investorLand== null)    return new ResponseEntity<ResponseDTO<InvestorLandDetailsDTO>>(new ResponseDTO<>(ResponseStatus.Error, "investor's land not found"), HttpStatus.NOT_FOUND);

            InvestorLandDetailsDTO investorLandDetailsDTO= DTOUtility.convertInvestorLandToInvestorLandDetailsDTO(investorLand);
            ResponseDTO<InvestorLandDetailsDTO> landDetailsDTO= new ResponseDTO<>(ResponseStatus.Success, "Successful");
            landDetailsDTO.setBody(investorLandDetailsDTO);
            return new  ResponseEntity<ResponseDTO<InvestorLandDetailsDTO>>( landDetailsDTO, HttpStatus.OK);
        }catch (NoSuchElementException e){
            return new ResponseEntity<ResponseDTO<InvestorLandDetailsDTO>>(new ResponseDTO<>(ResponseStatus.Error, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }




}
