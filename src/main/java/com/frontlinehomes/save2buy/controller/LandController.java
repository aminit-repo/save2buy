package com.frontlinehomes.save2buy.controller;



import com.frontlinehomes.save2buy.data.ResponseDTO;
import com.frontlinehomes.save2buy.data.ResponseStatus;
import com.frontlinehomes.save2buy.data.account.response.InitTransactionResponseDTO;
import com.frontlinehomes.save2buy.data.land.data.*;
import com.frontlinehomes.save2buy.data.land.request.*;
import com.frontlinehomes.save2buy.data.land.response.CheckOutResponseDTO;
import com.frontlinehomes.save2buy.data.land.response.InvestorLandResponseDTO;
import com.frontlinehomes.save2buy.data.land.response.PaymentPlanResponseDTO;
import com.frontlinehomes.save2buy.data.users.User;
import com.frontlinehomes.save2buy.data.users.investor.data.Investor;

import com.frontlinehomes.save2buy.data.users.investor.response.InvestorResponseDTO;
import com.frontlinehomes.save2buy.exception.CalculatorConfigException;
import com.frontlinehomes.save2buy.exception.NotNullFieldException;
import com.frontlinehomes.save2buy.service.CopyUtils;
import com.frontlinehomes.save2buy.service.InvestorService;
import com.frontlinehomes.save2buy.service.UserService;
import com.frontlinehomes.save2buy.service.file.FileSystemStorageService;
import com.frontlinehomes.save2buy.service.investorLand.InvestorLandService;
import com.frontlinehomes.save2buy.service.land.LandService;
import com.frontlinehomes.save2buy.service.landPaymentPlan.LandPaymentPlanService;

import com.frontlinehomes.save2buy.service.paymentUtil.PaymentUtilService;
import com.frontlinehomes.save2buy.service.utils.DTOUtility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/land")
public class LandController {
  @Autowired
  private LandService landService;
  @Autowired
  private UserService userService;

  @Autowired
  private FileSystemStorageService fileSystemStorageService;

  @Autowired
  private LandPaymentPlanService landPaymentPlanService;

  @Autowired
  private InvestorLandService investorLandService;
  @Autowired
  private InvestorService investorService;

  @Autowired
  private PaymentUtilService paymentUtilService;



  private static Logger log = LogManager.getLogger(LandController.class);

  /***
   *
   *
   * @param addLandDTO
   * @return ResponseEntity<LandDetailsDTO>
   *
   *     creates a new land without land payment plans
   */

  @CrossOrigin( allowedHeaders = {"Authorization", "Content-Type" })
  @PostMapping("/create")
  public ResponseEntity<LandDetailsDTO>  createLand(@RequestBody AddLandDTO addLandDTO){
    if(addLandDTO.getSize()!=null && addLandDTO.getTitle()!= null&& addLandDTO.getNeigborhood()!= null && addLandDTO.getPriceInSqm()!=null){
      //persist land
      Land land=  landService.addLand(DTOUtility.convertAddLandDTOtoLand(addLandDTO));
      LandDetailsDTO landDetailsDTO= DTOUtility.convertLandToLandDetailsDTO(land);
      return ResponseEntity.ok(landDetailsDTO);
    }
    String message="The field  "+ addLandDTO.getSize()== null? "size": addLandDTO.getTitle()==null ? "title": addLandDTO.getPriceInSqm()==null?"priceInSqm": "neigborhood";
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
  }


  /**
   *
   * @param file
   * @param id
   * @return ResponseEntity<ImageDTO>
   *
   *     uploads an image to a land
   */

  @CrossOrigin(allowedHeaders = {"Authorization", "Content-Type"})
  @PostMapping("/image/{id}")
  public ResponseEntity<ImageDTO> uploadLandImage(@RequestParam("file") MultipartFile file, @PathVariable Long id){
   //get the specified land
    try{
      Land land= landService.getLand(id);
    }catch (NoSuchElementException e){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    log.info("LandController:uploadImage:  land found with id "+id);


    try{
      if(file.isEmpty()){
         throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File cannot be empty");
      }

      String url=fileSystemStorageService.store(file, "land"+id+".jpg", "land");
      log.info("FileSystemStorageService:store: image uploaded successfully");
       return ResponseEntity.ok(new ImageDTO(url));
    }catch (Exception e){
      log.warn("LandController:uploadImage:  error uploading image "+e.getMessage());
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }

  }



  @CrossOrigin( allowedHeaders = {"Authorization", "Content-Type"}, methods = {RequestMethod.PUT})
  @PutMapping("/{id}")
  public ResponseEntity<LandDetailsDTO> updateLand(@PathVariable Long id, @RequestBody UpdateLandDTO updateLandDTO) {
    try{
       Land land= landService.getLand(id);
       BeanUtils.copyProperties(updateLandDTO, land, CopyUtils.getNullPropertyNames(updateLandDTO));
       landService.addLand(land);
      return new ResponseEntity<LandDetailsDTO>(DTOUtility.convertLandToLandDetailsDTO(land), HttpStatus.OK);
    }catch (NoSuchElementException e){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Land with id "+id+" not found");
    }
  }


  @CrossOrigin( allowedHeaders = {"Authorization"})
  @GetMapping
  public ResponseEntity<List<LandDetailsDTO>> getAllLand(){
        List<Land> lands= landService.getAllLand();
        List<LandDetailsDTO> landDetailsDTOS= new ArrayList<>();
        lands.forEach(land ->{
          landDetailsDTOS.add(DTOUtility.convertLandToLandDetailsDTO(land));
        } );
        return new  ResponseEntity<List<LandDetailsDTO> >(landDetailsDTOS, HttpStatus.OK);
  }


  @CrossOrigin( allowedHeaders = {"Authorization"})
  @GetMapping("/{id}")
  public ResponseEntity<LandDetailsDTO> getLand(@PathVariable Long id){
    // get the land by id
    try{
       Land land= landService.getLand(id);
       return new ResponseEntity<LandDetailsDTO>(DTOUtility.convertLandToLandDetailsDTO(land), HttpStatus.OK);
    }catch (Exception e){
      log.info("LandController:getLand : The requested resource was not found" );
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The requested resource was not found");
    }
  }

  /**
   *
   * *
   * @param id
   * @param landPurchaseDTO
   * @return ResponseEntity<InvestorLandResponseDTO>
   *
   *  This endpoint is temporary and need upgrade in other to prevent a user, from using a token to access another person's data
   *
   */


  @CrossOrigin(allowedHeaders = {"Authorization", "Content-Type"} ,methods = {RequestMethod.POST})
  @PostMapping("/checkout/{id}")
  public ResponseEntity<ResponseDTO<InvestorLandResponseDTO>> purchaseLand(@PathVariable Long id,@RequestBody LandPurchaseDTO landPurchaseDTO){
    /**
     * Request is invalid if paymentPlan and paymentPlanId is specified together
     * Request is invalid if email or userId field is null
     *
     *  Amount  and charge should not be specified in the paymentPlan
     *
     *
     *  protect the endpoint to allow only instance of Investors to purchase land
     *
     *
     *
     */
    Land land=null;
    User user=null;

    Double amountCalculated= null;
    try{
      //validate if fields are valid
      isLandPurchaseFieldsValid(landPurchaseDTO, LandStatus.CheckOut);

      //get the specified land
      land= landService.getLand(id);

      //check if it's a new plan we are creating
      if (landPurchaseDTO.getPaymentPlanId() != null) {

      }else{
        amountCalculated= paymentUtilService.calculatePaymentCharge(landPurchaseDTO.getPaymentPlan().getSizeInSqm(), landPurchaseDTO.getPaymentPlan().getFrequency(),
                landPurchaseDTO.getPaymentPlan().getDurationLength(), landPurchaseDTO.getPaymentPlan().getDurationType(), land);
      }

      //check if user's email is provided
      if(landPurchaseDTO.getUserId()!= null){
        user  = userService.getUser(landPurchaseDTO.getUserId());
      }else{
        user= userService.getUserByEmail(landPurchaseDTO.getEmail());
      }

      if(!user.getEnabled()) return  new ResponseEntity<ResponseDTO<InvestorLandResponseDTO>>((new ResponseDTO<InvestorLandResponseDTO>(ResponseStatus.Error,"Email verification is required" )), HttpStatus.NOT_ACCEPTABLE);

      //check if user profile has been set
      if(user.getFirstName()== null || user.getLastName()== null || user.getInvestor().getNextOfKinName() == null ){
        return new ResponseEntity<>(new  ResponseDTO<InvestorLandResponseDTO>(ResponseStatus.Error, "Billing profile is required"), HttpStatus.NOT_ACCEPTABLE);
      }

    }catch(NoSuchElementException e){
      log.info("LandController:purchaseLand : "+e.getMessage());
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }catch (NotNullFieldException e){
      throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }catch ( InvalidPropertiesFormatException e){
      throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }catch( ResponseStatusException e){
      throw new ResponseStatusException(e.getStatusCode(), e.getMessage());
    }catch (CalculatorConfigException e){
      log.error("LandController:purchaseLand: error:  "+e.getMessage());
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "something wrong happend");
    }

    /*try{
        InvestorLand investorLandTemp= investorLandService.getInvestorLandByLand(user.getInvestor(),land);
        if(investorLandTemp.getLandStatus()!= LandStatus.Wishlist && investorLandTemp.getLandStatus() != LandStatus.CheckOut){
          throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, "user already obtained land");
        }else{

        }

    }catch (NoSuchElementException e){
          //do nothing
    } */



      //get land
     try{



       InvestorLand wishLand= null;
       /**
        *   check if the user has the specified land in his wishlist
        *   providing a purchaseId automatically specifies, land exist in user's wishlist
        */
       //check if purchaseId was provided
       if(landPurchaseDTO.getPurchaseId()!= null){
          wishLand= investorLandService.getInvestorLand(landPurchaseDTO.getPurchaseId());
       }else{
         List<InvestorLand> investorLandList= user.getInvestor().getInvestorLands();
         for (InvestorLand investorLand : investorLandList) {
           if(investorLand.getLand().equals(land)  && investorLand.getLandStatus().equals(LandStatus.Wishlist)){
             wishLand= investorLand;
           }
         }
       }




       InvestorLand checkOutLand= null;
       //check if wishList was not found
       if(wishLand== null){
         //check if land is checked out already
         List<InvestorLand> investorLandList= user.getInvestor().getInvestorLands();

         for (InvestorLand investorLand : investorLandList) {
           if(investorLand.getLand().equals(land) && investorLand.getLandStatus().equals(LandStatus.CheckOut)){
             checkOutLand= investorLand;
             //detaching the investor payment plan
           }
         }
       }






       //flag that specifies an update operation
       Boolean isLandUpdateOperation= false;
       InvestorLand investorLand= null;
       //if checkout land is found, update it
       if(wishLand!= null || checkOutLand != null){ // if wishlist land is found, update it
         isLandUpdateOperation=true;
         investorLand= wishLand!= null ? wishLand : checkOutLand;
         landPaymentPlanService.removeAllInvestorPaymentPlans(investorLand);
       }else{
         //create a new Investor Land
         investorLand= new InvestorLand();
         investorLand.setLand(land);
         investorLand.setInvestor(user.getInvestor());
         investorLand.setLandStatus(LandStatus.CheckOut);
       }


       PaymentPlan plan= null;

       //create a flag to tell a plan is managed
       Boolean isPlanManaged=false;
       PaymentPlan planManaged=null;


       //check if paymentPlanID is provided
       if (landPurchaseDTO.getPaymentPlanId() != null) {
         planManaged= landPaymentPlanService.getPaymentPlanById(landPurchaseDTO.getPaymentPlanId());

         isPlanManaged=true;
       } else {
         //persist the payment plan

         /**
          *
          * Validating the payment plan.
          * with rules
          *  * payment plan Amount should no be specified in request.
          *  * payment charge should not be specified in the request
          *  * sizeInSqm must be specified
          *  *
          *
          */
         plan= new PaymentPlan();
         //check for the specified duration
         List<Duration> durationList= paymentUtilService.getAllDuration();
         Duration durationManaged= null;
         Boolean found= false;
         for (Duration duration : durationList) {
           if(duration.getLength().equals(landPurchaseDTO.getPaymentPlan().getDurationLength()) && duration.getFrequency().equals(landPurchaseDTO.getPaymentPlan().getDurationType())){
             durationManaged= duration;
             found= true;
           }
         }

         if(!found){
           //create a new duration
           Duration duration= new Duration();
           duration.setFrequency(landPurchaseDTO.getPaymentPlan().getDurationType());
           duration.setLength(landPurchaseDTO.getPaymentPlan().getDurationLength());

           durationManaged= paymentUtilService.addDuration(duration);

         }


         durationManaged.addPaymentPlan(plan);

         plan.setFrequency(landPurchaseDTO.getPaymentPlan().getFrequency());

         plan.setSizeInSqm(landPurchaseDTO.getPaymentPlan().getSizeInSqm());

         //calculate the Amount and Charge
         plan.setCharges(amountCalculated);

         plan.setAmount(landPurchaseDTO.getPaymentPlan().getSizeInSqm() * land.getPriceInSqm());
       }





      //check for payment plan
       if(isLandUpdateOperation){

           //perform a remove operation
           investorLand = investorLandService.getInvestorLand(investorLand.getId());

           //persist a new InvestorLandPaymentPlan
           if(isPlanManaged){
             InvestorLandPaymentPlan investorLandPaymentPlan= new InvestorLandPaymentPlan();

             investorLandPaymentPlan.setStatus(PaymentPlanStatus.Active);
             investorLand.addInvestorLandPaymentPlan(investorLandPaymentPlan);


             //synchronize investorLand amount field with paymentPlan amount field
             investorLand.setAmount(planManaged.getAmount());


           }else{

             InvestorLandPaymentPlan investorLandPaymentPlan= new InvestorLandPaymentPlan();

             investorLandPaymentPlan.setStatus(PaymentPlanStatus.Active);


             investorLand.addInvestorLandPaymentPlan(investorLandPaymentPlan);

             /**
              *
              * Validate the payment plan
              */

             //persist the payment plan, because it is new
             planManaged= landPaymentPlanService.savePaymentPlan(plan);

             //synchronize investorLand size field with payment sizeInSqm field


           }


       }



       if(!isLandUpdateOperation){
         //persist a new InvestorLandPaymentPlan

         investorLand=investorLandService.addInvestorLand(investorLand);

         if(isPlanManaged){
           InvestorLandPaymentPlan investorLandPaymentPlan= new InvestorLandPaymentPlan();
           investorLandPaymentPlan.setStatus(PaymentPlanStatus.Active);
           investorLand.addInvestorLandPaymentPlan(investorLandPaymentPlan);

         }else{

           InvestorLandPaymentPlan investorLandPaymentPlan= new InvestorLandPaymentPlan();
           planManaged= landPaymentPlanService.savePaymentPlan(plan);

           investorLandPaymentPlan.setStatus(PaymentPlanStatus.Active);
           investorLand.addInvestorLandPaymentPlan(investorLandPaymentPlan);


         }

         /**
          * implement a garbage collector to clean up dynamic created payment
          */

       }

       /**
        *
        * TO DO: Calculate the investorLand Amount and add to investorLand
        */


       //ensure the land Status is checkout
       investorLand.setLandStatus(LandStatus.CheckOut);

       if(isPlanManaged){
         log.info("land size dur plan managed = "+planManaged.getSizeInSqm() );
         investorLand.setSize(planManaged.getSizeInSqm());
       }else{
         log.info("land size due un managed = "+plan.getSizeInSqm());
         investorLand.setSize(plan.getSizeInSqm());
       }


       //update the investor land
       InvestorLand investorLandManaged= investorLandService.addInvestorLand(investorLand);


       List<InvestorLandPaymentPlan> investorLandPaymentPlanList= investorLandManaged.getInvestorLandPaymentPlan();

         InvestorLandPaymentPlan investorLandPaymentPlan= null;
         for (InvestorLandPaymentPlan planData : investorLandPaymentPlanList) {
           if(planData.getStatus().equals(PaymentPlanStatus.Active))
             investorLandPaymentPlan= planData;
         }
         //this should never happen
         if(investorLandPaymentPlan== null) throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "payment plan  cannot be null");

         planManaged.addInvestorLandPaymentPlan(investorLandPaymentPlan);

         //update the payment plan
         landPaymentPlanService.savePaymentPlan(planManaged);



       InvestorLandResponseDTO investorLandResponseDTO = DTOUtility.convertInvestorLandToInvestorLandResponseDTO(investorLandManaged);

       investorLandResponseDTO.setUserId(user.getId());

       //set the purchase ID
       investorLandResponseDTO.setPurchaseId(investorLandManaged.getId());

       log.info("LandController:purchaseLand : land purchase initiated  successfully with land id=" + id + " user id=" + user.getId());
      ResponseDTO<InvestorLandResponseDTO> responseDTO= new ResponseDTO<>(ResponseStatus.Success,"successful");
      responseDTO.setBody(investorLandResponseDTO);
       return new  ResponseEntity<ResponseDTO<InvestorLandResponseDTO>>(responseDTO, HttpStatus.OK);


     }catch(NoSuchElementException e){
        log.info("LandController:purchaseLand : "+e.getMessage());
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
     }catch (NotNullFieldException e){
        throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
     }catch( ResponseStatusException e){
       throw new ResponseStatusException(e.getStatusCode(), e.getMessage());
     }

  }

  @CrossOrigin( allowedHeaders = {"Authorization"})
  @GetMapping("/checkout/{purchaseId}/{paymentPlanId}")
  public ResponseEntity<ResponseDTO<CheckOutResponseDTO>> getCheckOutDetails(@PathVariable Long purchaseId, @PathVariable Long paymentPlanId){

    try{
      //get the user's purchase id
      InvestorLand investorLand= investorLandService.getInvestorLand(purchaseId);

      //verify the status of this investor's Land
      if(investorLand.getLandStatus()!= LandStatus.CheckOut)
        return new ResponseEntity<>(new  ResponseDTO<CheckOutResponseDTO>(ResponseStatus.Error, "land can not be found in checkout list"), HttpStatus.NOT_ACCEPTABLE);

      //search for this plan in the

      PaymentPlan paymentPlan= landPaymentPlanService.getPaymentPlanById(paymentPlanId);

      Boolean paymentPlanSearch= false;

      List<InvestorLandPaymentPlan>   landPaymentPlanArrayList= investorLand.getInvestorLandPaymentPlan();

      //check if land is found
      for (InvestorLandPaymentPlan investorLandPaymentPlan : landPaymentPlanArrayList) {
        if(investorLandPaymentPlan.getPaymentPlan().equals(paymentPlan)){
          paymentPlanSearch= true;
        }
      }

      if(!paymentPlanSearch) return new ResponseEntity<>(new  ResponseDTO<CheckOutResponseDTO>(ResponseStatus.Error, "payment plan not found"), HttpStatus.NOT_FOUND);


      //get investor's details
      User user= investorLand.getInvestor().getUser();

      //check if user profile has been set
      if(user.getFirstName()== null || user.getLastName()== null || investorLand.getInvestor().getNextOfKinName() == null ){
        return new ResponseEntity<>(new  ResponseDTO<CheckOutResponseDTO>(ResponseStatus.Error, "user's profile not complete"), HttpStatus.NOT_ACCEPTABLE);
      }

      if(!user.getEnabled()) return new ResponseEntity<>(new  ResponseDTO<CheckOutResponseDTO>(ResponseStatus.Error, "user's  email not verified"), HttpStatus.NOT_ACCEPTABLE);

      InvestorResponseDTO investorResponseDTO= DTOUtility.convertUserToInvestorResponseDTO(user);

      LandDetailsDTO landDetailsDTO= DTOUtility.convertLandToLandDetailsDTO(investorLand.getLand());

      PaymentPlanResponseDTO paymentPlanDTO= DTOUtility.convertPaymentPlanToResponseDTO(paymentPlan);

      //create CheckOutResponseDTO
      CheckOutResponseDTO checkOutResponseDTO= new CheckOutResponseDTO();
      checkOutResponseDTO.setLand(landDetailsDTO);
      checkOutResponseDTO.setUser(investorResponseDTO);
      checkOutResponseDTO.setPaymentPlan(paymentPlanDTO);

      //create ResponseDTO
      ResponseDTO<CheckOutResponseDTO> responseDTO= new ResponseDTO<>();
      responseDTO.setMessage("Successful");
      responseDTO.setStatus(ResponseStatus.Success);
      responseDTO.setBody(checkOutResponseDTO);

      return new ResponseEntity<ResponseDTO<CheckOutResponseDTO>>(responseDTO, HttpStatus.OK);

    }catch (NoSuchElementException e){
      return new ResponseEntity<>(new  ResponseDTO<CheckOutResponseDTO>(ResponseStatus.Error, e.getMessage()), HttpStatus.NOT_FOUND);
    }


  }







  private Boolean isLandPurchaseFieldsValid(LandPurchaseDTO landPurchaseDTO, LandStatus landStatus) throws InvalidPropertiesFormatException, NotNullFieldException{


    if( landPurchaseDTO.getPaymentPlan() != null && landPurchaseDTO.getPaymentPlanId()!= null)
      throw new InvalidPropertiesFormatException("PaymentPlanId and PaymentPlan can not be specified together");


    //check if both fields are null
    if( landPurchaseDTO.getPaymentPlan() == null && landPurchaseDTO.getPaymentPlanId()== null)
      throw new NotNullFieldException("Missing required field ( paymentPlan or paymentPlanId)) ");


    if(landPurchaseDTO.getPurchaseId()== null && (landPurchaseDTO.getUserId() == null && landPurchaseDTO.getEmail()== null))
      throw new NotNullFieldException("Missing required field ( purchaseId  or userId and email) ");


    LandPurchasePaymentPlanDTO paymentPlanDTO= landPurchaseDTO.getPaymentPlan();
    //if paymentPlan is specified
    if(paymentPlanDTO != null){


      //duration  is optional if frequency is oneOff, else it's required


      if(paymentPlanDTO.getFrequency() == null){
        throw new NotNullFieldException("Missing required field frequency");
      }

      if(paymentPlanDTO.getFrequency() == Frequency.OneOff){
        //check for payment plan specified fields
        if(paymentPlanDTO.getDurationLength() != null || paymentPlanDTO.getDurationLength()  != null){
          throw new InvalidPropertiesFormatException("durationType and durationLength must be null if frequency is OneOff");
        }

      }


      if(paymentPlanDTO.getFrequency() != Frequency.OneOff){
        //check for payment plan specified fields
        if(paymentPlanDTO.getDurationLength() == null || paymentPlanDTO.getDurationLength() <= 0){
          throw new NotNullFieldException("Missing required field durationLength");
        }

        if(paymentPlanDTO.getDurationType()==null ){
          throw new NotNullFieldException("Missing required field durationType");
        }
      }


      if(paymentPlanDTO.getSizeInSqm() == null){
        throw new NotNullFieldException("Missing required field sizeInSqm");
      }

    }
    return true;
  }





}
