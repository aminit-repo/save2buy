package com.frontlinehomes.save2buy.controller;

import com.fasterxml.jackson.databind.util.BeanUtil;
import com.frontlinehomes.save2buy.data.account.data.BillingType;
import com.frontlinehomes.save2buy.data.land.data.*;
import com.frontlinehomes.save2buy.data.land.request.*;
import com.frontlinehomes.save2buy.data.land.response.InvestorLandResponseDTO;
import com.frontlinehomes.save2buy.data.land.response.PaymentPlanResponseDTO;
import com.frontlinehomes.save2buy.data.users.User;
import com.frontlinehomes.save2buy.data.users.investor.data.Investor;
import com.frontlinehomes.save2buy.data.users.investor.request.InvestorDTO;
import com.frontlinehomes.save2buy.service.CopyUtils;
import com.frontlinehomes.save2buy.service.InvestorService;
import com.frontlinehomes.save2buy.service.UserService;
import com.frontlinehomes.save2buy.service.file.FileSystemStorageService;
import com.frontlinehomes.save2buy.service.investorLand.InvestorLandService;
import com.frontlinehomes.save2buy.service.land.LandService;
import com.frontlinehomes.save2buy.service.landPaymentPlan.LandPaymentPlanService;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
      Land land=  landService.addLand(convertAddLandDTOtoLand(addLandDTO));
      LandDetailsDTO landDetailsDTO= convertLandToLandDetailsDTO(land);
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

  @CrossOrigin( allowedHeaders = {"Authorization", "Content-Type"})
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

       String url=fileSystemStorageService.store(file, "land"+id+".jpg", "investor");
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
      return new ResponseEntity<LandDetailsDTO>(convertLandToLandDetailsDTO(land), HttpStatus.OK);
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
          landDetailsDTOS.add(convertLandToLandDetailsDTO(land));
        } );
        return new  ResponseEntity<List<LandDetailsDTO> >(landDetailsDTOS, HttpStatus.OK);
  }


  @CrossOrigin( allowedHeaders = {"Authorization"})
  @GetMapping("/{id}")
  public ResponseEntity<LandDetailsDTO> getLand(@PathVariable Long id){
    // get the land by id
    try{
       Land land= landService.getLand(id);
       return new ResponseEntity<LandDetailsDTO>(convertLandToLandDetailsDTO(land), HttpStatus.OK);
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
   *
   */

  @CrossOrigin( allowedHeaders = {"Authorization", "Content-Type"} ,methods = {RequestMethod.POST})
  @PostMapping("/purchase/{id}")
  public ResponseEntity<InvestorLandResponseDTO> purchaseLand(@PathVariable Long id, LandPurchaseDTO landPurchaseDTO){

    //validate fields
   try{
     isLandPurchaseFieldsValid(landPurchaseDTO);
   }catch (Exception e){
     throw  e;
   }

    if(landPurchaseDTO.getPaymentPlan()==null){
      log.info("LandController:purchaseLand : no payment method specified id="+landPurchaseDTO.getUserId());
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No payment plan specified");
    }
      //get land
     try{
       Land land= landService.getLand(id);
       //check if user's has verified his email

       if(landPurchaseDTO.getUserId()!=  null){
         try{
           User user= userService.getUser(landPurchaseDTO.getUserId());
           //verify is user emails is verified
           if(user.getEnabled()){
             //verify if user's profile is completed
             if(user.getFirstName()!= null
                     && user.getInvestor()!= null
                     && user.getInvestor().getAddress()!= null
                     && user.getInvestor().getNationality()!= null
                     && user.getInvestor().getNextOfKinName()!= null
                     && user.getInvestor().getOccupation()!= null
                     && user.getInvestor().getSourceOfIncome()!=null
             ){
               //check if paymentPlanID is provided
               if(landPurchaseDTO.getPaymentPlanId()!=null){
                  PaymentPlan paymentPlan= landPaymentPlanService.getPaymentPlanById(landPurchaseDTO.getPaymentPlanId());
                  if(paymentPlan!=null){

                    InvestorLand investorLand= convertLandPurchaseDTOToInvestorLand(landPurchaseDTO,land,user,paymentPlan);
                    InvestorLandResponseDTO investorLandResponseDTO= convertInvestorLandToInvestorLandResponseDTO(investorLand);
                    investorLandResponseDTO.setUserId(user.getId());
                    return new ResponseEntity<InvestorLandResponseDTO>(investorLandResponseDTO, HttpStatus.OK);

                  }else{
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"land payment plan cannot be found");
                  }

               }else{
                 //create a new payment plan for the purchase
                 PaymentPlan paymentPlan= convertPaymentPlanDTOToPaymentPlan(landPurchaseDTO.getPaymentPlan());
                 //persist the payment plan
                 PaymentPlan paymentPlan1= landPaymentPlanService.savePaymentPlan(paymentPlan);
                 InvestorLand investorLand= convertLandPurchaseDTOToInvestorLand(landPurchaseDTO,land,user,paymentPlan1);
                 InvestorLandResponseDTO investorLandResponseDTO= convertInvestorLandToInvestorLandResponseDTO(investorLand);
                 investorLandResponseDTO.setUserId(user.getId());
                 log.info("LandController:purchaseLand : land purchase success with land id="+landPurchaseDTO.getUserId()+" user="+user.getEmail());
                 return new ResponseEntity<InvestorLandResponseDTO>(investorLandResponseDTO, HttpStatus.OK);
               }

             }else{
               log.info("LandController:purchaseLand : user email profile not complete with id="+landPurchaseDTO.getUserId());
               throw  new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "user email not verified");
             }
           }else{
             //email is not verified
             log.info("LandController:purchaseLand : user email not verified with id="+landPurchaseDTO.getUserId());
             throw  new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "user email not verified");
           }
         }catch(NoSuchElementException e){
           log.info("LandController:purchaseLand : user cannot be found with id="+landPurchaseDTO.getUserId());
           throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user cannot be found");
         }
       }else{
         log.info("LandController:purchaseLand : user id cannot be null" );
         throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, "user id cannot be null");
       }

     }catch(NoSuchElementException e){
       log.info("LandController:purchaseLand : land not found" );
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "land not found");
     }

  }


  private InvestorLandResponseDTO convertInvestorLandToInvestorLandResponseDTO(InvestorLand investorLand){
    InvestorLandResponseDTO investorLandResponseDTO= new InvestorLandResponseDTO();
    investorLandResponseDTO.setId(investorLand.getId());
    investorLandResponseDTO.setBillingType(investorLand.getBillingType());
    investorLandResponseDTO.setAmount(investorLand.getAmount());
    investorLandResponseDTO.setLandId(investorLand.getLand().getId());
    PaymentPlan paymentPlan= investorLand.getInvestorLandPaymentPlan().get(0).getPaymentPlan();
    investorLandResponseDTO.setPaymentPlanId(paymentPlan.getId());
    investorLandResponseDTO.setCreationDate(investorLand.getCreationDate());
    return  investorLandResponseDTO;
  }


  private PaymentPlan convertPaymentPlanDTOToPaymentPlan(PaymentPlanDTO paymentPlanDTO){
    PaymentPlan paymentPlan= new PaymentPlan();
    BeanUtils.copyProperties(paymentPlanDTO, paymentPlan);
    return paymentPlan;
  }


  private Boolean isLandPurchaseFieldsValid(LandPurchaseDTO landPurchaseDTO){

    if( landPurchaseDTO.getPaymentPlan() != null && landPurchaseDTO.getPaymentPlanId()!= null){
      throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, "PaymentPlanId and PaymentPlan can not be specified together");
    }
    if(landPurchaseDTO.getUserId() == null || landPurchaseDTO.getBillingType()== null || landPurchaseDTO.getSize() ==null){
      throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing required field");
    }

    return true;
  }


  private InvestorLand convertLandPurchaseDTOToInvestorLand(LandPurchaseDTO landPurchaseDTO, Land land, User user, PaymentPlan paymentPlan){
    //create a new Investor Land
    InvestorLand investorLand= new InvestorLand();
    investorLand.setBillingType(landPurchaseDTO.getBillingType());
    investorLand.setSize(landPurchaseDTO.getSize());
    investorLand.setAmount(landPurchaseDTO.getSize());

    //create a new InvestorLandPaymentPlan
    InvestorLandPaymentPlan investorLandPaymentPlan= new InvestorLandPaymentPlan();
    investorLand.addInvestorLandPaymentPlan(investorLandPaymentPlan);
    paymentPlan.addInvestorLandPaymentPlan(investorLandPaymentPlan);

    //synchronize investorLand with the investor
    user.getInvestor().addInvestorLands(investorLand);
    land.addInvestorLand(investorLand);

    //persist the investor
    Investor investor=  investorService.addInvestor(user.getInvestor());

    return  investorLand;
  }





  private Land convertAddLandDTOtoLand(AddLandDTO addLandDTO){
    Land land=new Land();
    BeanUtils.copyProperties(addLandDTO, land);
    return  land;
  }

  private LandDetailsDTO convertLandToLandDetailsDTO(Land land){
    LandDetailsDTO landDetailsDTO= new LandDetailsDTO();
    BeanUtils.copyProperties(land, landDetailsDTO);

    //create paymentPlanResponseDTO
    Set<PaymentPlanResponseDTO> planResponseDTO= new HashSet<>();
    Set<LandPaymentPlan> landPaymentPlans =land.getLandPaymentPlans();
    if(landPaymentPlans != null){
         landPaymentPlans.forEach(landPaymentPlan -> {
        PaymentPlanResponseDTO p= new PaymentPlanResponseDTO();
        BeanUtils.copyProperties(landPaymentPlan.getPaymentPlan(), p);
        //set duration fields
        p.setDurationType(landPaymentPlan.getPaymentPlan().getDuration().getFrequency());
        p.setDurationWeight(landPaymentPlan.getPaymentPlan().getDuration().getWeight());
        p.setDurationLength(landPaymentPlan.getPaymentPlan().getDuration().getLength());

        planResponseDTO.add(p);
      });

    }
    landDetailsDTO.setLandPaymentPlans(planResponseDTO);
    return landDetailsDTO;
  }



}
