package com.frontlinehomes.save2buy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frontlinehomes.save2buy.client.monnify.*;
import com.frontlinehomes.save2buy.config.MonnifyProperties;
import com.frontlinehomes.save2buy.data.ResponseDTO;
import com.frontlinehomes.save2buy.data.ResponseStatus;
import com.frontlinehomes.save2buy.data.account.data.BillingType;
import com.frontlinehomes.save2buy.data.account.data.Channel;
import com.frontlinehomes.save2buy.data.account.data.Transaction;
import com.frontlinehomes.save2buy.data.account.data.TransactionStatus;
import com.frontlinehomes.save2buy.data.account.request.ChargeCardOTPVerifyRequestDTO;
import com.frontlinehomes.save2buy.data.account.request.ChargeCardRequestDTO;
import com.frontlinehomes.save2buy.data.account.request.InitTransactionRequestDTO;
import com.frontlinehomes.save2buy.data.account.response.ChargeCardResponseDTO;
import com.frontlinehomes.save2buy.data.account.response.InitTransactionResponseDTO;
import com.frontlinehomes.save2buy.data.land.data.InvestorLand;
import com.frontlinehomes.save2buy.data.land.data.InvestorLandPaymentPlan;
import com.frontlinehomes.save2buy.data.land.data.LandStatus;
import com.frontlinehomes.save2buy.data.land.data.PaymentPlan;
import com.frontlinehomes.save2buy.data.users.User;
import com.frontlinehomes.save2buy.events.payement.OnPaymentCompleteEvent;
import com.frontlinehomes.save2buy.exception.*;
import com.frontlinehomes.save2buy.service.CopyUtils;
import com.frontlinehomes.save2buy.service.investorLand.InvestorLandService;
import com.frontlinehomes.save2buy.service.landPaymentPlan.LandPaymentPlanService;
import com.frontlinehomes.save2buy.service.monnify.MonnifyService;
import com.frontlinehomes.save2buy.service.paymentUtil.PaymentUtilService;
import com.frontlinehomes.save2buy.service.transaction.TransactionService;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.List;


@RestController
@RequestMapping("/pay-with-monfy/")
public class MonnifyController {
    @Autowired
    private MonnifyClient monnifyClient;
    @Autowired
    private InvestorLandService investorLandService;
    @Autowired

    private LandPaymentPlanService landPaymentPlanService;
    @Autowired
    private PaymentUtilService paymentUtilService;
    @Autowired
    private MonnifyProperties monnifyProperties;
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private MonnifyService monnifyService;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private static Logger log = LogManager.getLogger(MonnifyController.class);

    @GetMapping("/login")
    public String login(){
        return monnifyClient.getAccessToken().getAccessToken();
    }

    @CrossOrigin(allowedHeaders = {"Authorization", "Content-Type"})
    @PostMapping("/initiate-transaction")
    public ResponseEntity<ResponseDTO<InitTransactionResponseDTO>> init(@RequestBody InitTransactionRequestDTO initTransactionRequestDTO){

        //verify if this user is allowed to process this request

        //very required fields are not empty
        if(initTransactionRequestDTO.getPurchaseId()== null) return  new ResponseEntity<>((new  ResponseDTO<InitTransactionResponseDTO>(ResponseStatus.Error, "purchaseId cannot be empty")), HttpStatus.BAD_REQUEST);

        if(initTransactionRequestDTO.getPaymentMethod() == null) return  new ResponseEntity<>((new  ResponseDTO<InitTransactionResponseDTO>(ResponseStatus.Error, "purchaseId cannot be empty")), HttpStatus.BAD_REQUEST);

        if(initTransactionRequestDTO.getPaymentPlanId()== null) return  new ResponseEntity<>((new  ResponseDTO<InitTransactionResponseDTO>(ResponseStatus.Error, "paymentPlanId cannot be empty")), HttpStatus.BAD_REQUEST);

        //get the investorLand
        try{
            //verify if user's credentials has been uploaded


            //get the investorLand
            InvestorLand investorLand= investorLandService.getInvestorLand(initTransactionRequestDTO.getPurchaseId());

            //verify the status of this investor's Land
            if(investorLand.getLandStatus()!= LandStatus.CheckOut)
                return new ResponseEntity<>(new  ResponseDTO<InitTransactionResponseDTO>(ResponseStatus.Error, "land can not be found in checkout list"), HttpStatus.NOT_FOUND);

            //get investor's details
            User user= investorLand.getInvestor().getUser();

            //check if user's credentials and passport photograph has been uploaded
            if(user.getInvestor().getIdCardUrl()== null) return new ResponseEntity<>(new  ResponseDTO<InitTransactionResponseDTO>(ResponseStatus.Error, "upload a valid identification credential"), HttpStatus.NOT_ACCEPTABLE);

            //check if user's credentials and passport photograph has been uploaded
            if(user.getInvestor().getPassportUrl()== null) return new ResponseEntity<>(new  ResponseDTO<InitTransactionResponseDTO>(ResponseStatus.Error, "upload a recent passport photograph"), HttpStatus.NOT_ACCEPTABLE);

            //check if user profile has been set
            if(user.getFirstName()== null || user.getLastName()== null || investorLand.getInvestor().getNextOfKinName() == null ){
                return new ResponseEntity<>(new  ResponseDTO<InitTransactionResponseDTO>(ResponseStatus.Error, "user's profile not complete"), HttpStatus.NOT_ACCEPTABLE);
            }

            if(!user.getEnabled()) return new ResponseEntity<>(new  ResponseDTO<InitTransactionResponseDTO>(ResponseStatus.Error, "user's  email not verified"), HttpStatus.NOT_ACCEPTABLE);

            List<InvestorLandPaymentPlan> landPaymentPlanArrayList= investorLand.getInvestorLandPaymentPlan();

            PaymentPlan paymentPlan= landPaymentPlanService.getPaymentPlanById(initTransactionRequestDTO.getPaymentPlanId());

            Boolean paymentPlanSearch= false;

            //check if land is found
            for (InvestorLandPaymentPlan investorLandPaymentPlan : landPaymentPlanArrayList) {
                if(investorLandPaymentPlan.getPaymentPlan().equals(paymentPlan)){
                    paymentPlanSearch= true;
                }
            }

            if(!paymentPlanSearch) return new ResponseEntity<>(new  ResponseDTO<InitTransactionResponseDTO>(ResponseStatus.Error, "payment plan not found"), HttpStatus.NOT_FOUND);

            //create an Object of MonnifyInitRequest
            MonnifyInitRequest initRequest= new MonnifyInitRequest();
            initRequest.setAmount(paymentPlan.getCharges());
            initRequest.setCustomerName(user.getFirstName()+" "+user.getLastName());

            String paymentRef= paymentUtilService.getRefNumber();

            initRequest.setPaymentReference(paymentRef);
            initRequest.setCustomerEmail(user.getEmail());
            initRequest.setCurrencyCode("NGN");
            initRequest.setContractCode(monnifyProperties.getContractCode());

            initRequest.setRedirectUrl("https://my-merchants-page.com/transaction/confirm");

            initRequest.setPaymentDescription("Subscription Charge");
            ArrayList list= new ArrayList<>();
            list.add(initTransactionRequestDTO.getPaymentMethod()== BillingType.Card ? "CARD" : initTransactionRequestDTO.getPaymentMethod()== BillingType.Account ? "ACCOUNT_TRANSFER" : "");
            initRequest.setPaymentMethods(list);

            MonnifyInitResponse initResponse= monnifyService.initializeTransaction(initRequest);

            //create a new Transaction
            Transaction transaction= new Transaction();
            transaction.setRefNumber(paymentRef);
            transaction.setTransactionId(initResponse.getResponseBody().getTransactionReference());
            transaction.setBillingType(initTransactionRequestDTO.getPaymentMethod());
            transaction.setTransactionStatus(TransactionStatus.Initiated);

            //add investorLand
            investorLand.addTransaction(transaction);
            transaction.setChannel(Channel.Monnify);
            transaction.setAmount(initRequest.getAmount());

            //persist the transaction value
            transactionService.save(transaction);

            //update the investorLand
            investorLand.setBillingType(initTransactionRequestDTO.getPaymentMethod());
            //since merge operation is not cascaded btw transaction and investorLand, we do it manually
            investorLandService.addInvestorLand(investorLand);


            InitTransactionResponseDTO initTransactionResponseDTO=  new InitTransactionResponseDTO(investorLand.getId(), paymentRef, initTransactionRequestDTO.getPaymentMethod());

            ResponseDTO<InitTransactionResponseDTO> responseDTO= new ResponseDTO<InitTransactionResponseDTO>();
            responseDTO.setStatus(ResponseStatus.Success);
            responseDTO.setBody(initTransactionResponseDTO);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        }catch (NoSuchElementException e){
            log.error("MonnifyController: init:NoSuchElementException: "+e.getMessage());
            return new ResponseEntity<>(new  ResponseDTO<InitTransactionResponseDTO>(ResponseStatus.Error, e.getMessage()), HttpStatus.NOT_FOUND);
        }catch (MonnifyInitException e){
            log.error("MonnifyController: init: MonnifyInitException: "+e.getMessage()+" with purchaseId = "+initTransactionRequestDTO.getPurchaseId());
            return new ResponseEntity<>(new  ResponseDTO<InitTransactionResponseDTO>(ResponseStatus.Error, e.getMessage()), HttpStatus.SERVICE_UNAVAILABLE);
        }catch (MonnifyServiceException e){
            return new ResponseEntity<>(new  ResponseDTO<InitTransactionResponseDTO>(ResponseStatus.Error, 50, e.getMessage()), HttpStatus.SERVICE_UNAVAILABLE);
        }catch(MonnifyRefException e){
            //duplicate transaction
            return new ResponseEntity<>(new  ResponseDTO<InitTransactionResponseDTO>(ResponseStatus.Error, 51, e.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
        }catch (MonnifyBadFieldException e){
            return new ResponseEntity<>(new  ResponseDTO<InitTransactionResponseDTO>(ResponseStatus.Error,  e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }


    @CrossOrigin(allowedHeaders = {"Authorization", "Content-Type"})
    @PostMapping("/cards/charge")
    public ResponseEntity<ResponseDTO<ChargeCardResponseDTO>> chargeCard(@RequestBody ChargeCardRequestDTO cardRequestDTO){

        //check for null fields
      String[] nullFileds=  CopyUtils.getNullPropertyNames(cardRequestDTO);
      if(nullFileds.length >= 1) return  new ResponseEntity<>((new  ResponseDTO<ChargeCardResponseDTO>(ResponseStatus.Error, nullFileds[0]+" cannot be empty")), HttpStatus.BAD_REQUEST);

      try {
          //get the investorLand
          InvestorLand investorLand= investorLandService.getInvestorLand(cardRequestDTO.getPurchaseId());

          if(investorLand.getLandStatus() != LandStatus.CheckOut) return new ResponseEntity<>(new  ResponseDTO<ChargeCardResponseDTO>(ResponseStatus.Error, "land can not be found in checkout list"), HttpStatus.NOT_ACCEPTABLE);

           //check for investorLand transaction with the specified refNumber
         List<Transaction> transactionList= investorLand.getTransactionList();

         Boolean refFound= false;
         String transactionRef="";

          for (Transaction transaction : transactionList) {
              if(transaction.getRefNumber().equals(cardRequestDTO.getRefNumber())){
                  refFound=true;
                  transactionRef= transaction.getTransactionId();
              }
          }

          if(!refFound)  return  new ResponseEntity<>((new  ResponseDTO<ChargeCardResponseDTO>(ResponseStatus.Error, "refNumber not found")), HttpStatus.NOT_FOUND);
          MonnifyCard monnifyCard= new MonnifyCard();
          BeanUtils.copyProperties(cardRequestDTO,monnifyCard);

          MonnifyChargeCardRequest monnifyChargeCardRequest= new MonnifyChargeCardRequest();
          monnifyChargeCardRequest.setCard(monnifyCard);
          monnifyChargeCardRequest.setCollectionChannel("API_NOTIFICATION");
          monnifyChargeCardRequest.setTransactionReference(transactionRef);

          //update transaction status to pending
          Transaction transactionManaged= transactionService.getTransactionByRefNumber(cardRequestDTO.getRefNumber());
          transactionManaged.setTransactionStatus(TransactionStatus.Pending);
          transactionService.save(transactionManaged);


          MonnifyChargeCardResponse monnifyChargeCardResponse= monnifyService.chargeCard(monnifyChargeCardRequest);

          //check for failed transaction
          if(!monnifyChargeCardResponse.getRequestSuccessful()){
              ResponseDTO responseDTO= new ResponseDTO();
              responseDTO.setBody(null);
              responseDTO.setStatus(ResponseStatus.Error);
              responseDTO.setMessage(monnifyChargeCardResponse.getResponseMessage());
              transactionManaged.setTransactionStatus(TransactionStatus.Failed);
              //return a response that the transaction failed
              return new ResponseEntity<ResponseDTO<ChargeCardResponseDTO>>(responseDTO, HttpStatus.EXPECTATION_FAILED);
          }




          //if request was successful
          Boolean isOTPEnableed= false;
          ChargeCardResponseDTO chargeCardResponseDTO;
          if(monnifyChargeCardResponse.getResponseBody().getOtpData().getId() != null){
              //otp is enabled

            isOTPEnableed=true;
          }else if(monnifyChargeCardResponse.getResponseBody().getSecure3dData().getId() != null){
              //Secure3dData verification is required



          }else{
              isOTPEnableed= false;

              //successful transaction
              eventPublisher.publishEvent(new OnPaymentCompleteEvent(investorLand, monnifyChargeCardResponse.getResponseBody().getAuthorizedAmount(), monnifyChargeCardResponse.getResponseBody().getPaymentReference()));

          }

          chargeCardResponseDTO= new ChargeCardResponseDTO(monnifyChargeCardResponse.getResponseBody().getAuthorizedAmount(),
                  isOTPEnableed, monnifyChargeCardResponse.getResponseBody().getOtpData().getId(),cardRequestDTO.getRefNumber(),cardRequestDTO.getPurchaseId());
          ResponseDTO<ChargeCardResponseDTO> responseDTO = new ResponseDTO<>();
          responseDTO.setBody(chargeCardResponseDTO);
          responseDTO.setStatus(ResponseStatus.Success);
          responseDTO.setMessage(monnifyChargeCardResponse.getResponseBody().getMessage());
          return new ResponseEntity<ResponseDTO<ChargeCardResponseDTO>>(responseDTO, HttpStatus.OK);


      }catch (MonnifyCardException e){
          log.error("MonnifyController: chargeCard: MonnifyCardException: "+e.getMessage()+" with refNumber = "+cardRequestDTO.getRefNumber());
          //update the transaction status as failed
          return new ResponseEntity<>(new  ResponseDTO<ChargeCardResponseDTO>(ResponseStatus.Error, e.getMessage()), HttpStatus.SERVICE_UNAVAILABLE);
      }catch (NoSuchElementException e){
          log.error("MonnifyController: chargeCard: NoSuchElementException: "+e.getMessage()+" with refNumber = "+cardRequestDTO.getRefNumber());
          return  new ResponseEntity<>((new  ResponseDTO<ChargeCardResponseDTO>(ResponseStatus.Error, e.getMessage())), HttpStatus.NOT_FOUND);
      }catch (MonnifyBadFieldException e){
          return  new ResponseEntity<>((new  ResponseDTO<ChargeCardResponseDTO>(ResponseStatus.Error, 53,e.getMessage())), HttpStatus.BAD_REQUEST);
      }catch (MonnifyServiceException e){
          return  new ResponseEntity<>((new  ResponseDTO<ChargeCardResponseDTO>(ResponseStatus.Error, e.getMessage())), HttpStatus.SERVICE_UNAVAILABLE);
      }catch (MonnifyRefException e){
          return  new ResponseEntity<>((new  ResponseDTO<ChargeCardResponseDTO>(ResponseStatus.Error, 51, e.getMessage())), HttpStatus.UNPROCESSABLE_ENTITY);
      }

    }

    @CrossOrigin(allowedHeaders = {"Authorization", "Content-Type"})

    @PostMapping("/cards/charge/otp-verify")

    public ResponseEntity<ResponseDTO<ChargeCardResponseDTO>> verifyOTP(ChargeCardOTPVerifyRequestDTO otpVerifyRequestDTO){
        try {
            //check for null fields
            String[] nullFileds = CopyUtils.getNullPropertyNames(otpVerifyRequestDTO);
            if (nullFileds.length >= 1)
                return new ResponseEntity<>((new ResponseDTO<ChargeCardResponseDTO>(ResponseStatus.Error, nullFileds[0] + " cannot be empty")), HttpStatus.BAD_REQUEST);

            //get the investorLand
            InvestorLand investorLand = investorLandService.getInvestorLand(otpVerifyRequestDTO.getPurchaseId());

            if (investorLand.getLandStatus() != LandStatus.CheckOut)
                return new ResponseEntity<>(new ResponseDTO<ChargeCardResponseDTO>(ResponseStatus.Error, "land can not be found in checkout list"), HttpStatus.NOT_ACCEPTABLE);

            //check for investorLand transaction with the specified refNumber
            List<Transaction> transactionList = investorLand.getTransactionList();

            Boolean refFound = false;
            String transactionRef = "";

            //verify if the refNumber is valid
            for (Transaction transaction : transactionList) {
                if (transaction.getRefNumber().equals(otpVerifyRequestDTO.getRefNumber())) {
                    refFound = true;
                    transactionRef = transaction.getTransactionId();
                }
            }

            if (!refFound)
                return new ResponseEntity<>((new ResponseDTO<ChargeCardResponseDTO>(ResponseStatus.Error, "refNumber not found")), HttpStatus.NOT_FOUND);

            MonnifyChargeCardRequest monnifyChargeCardRequest = new MonnifyChargeCardRequest();
            monnifyChargeCardRequest.setTransactionReference(transactionRef);
            monnifyChargeCardRequest.setToken(otpVerifyRequestDTO.getToken());
            monnifyChargeCardRequest.setTokenId(otpVerifyRequestDTO.getTokenId());
            monnifyChargeCardRequest.setCard(null);
            monnifyChargeCardRequest.setCollectionChannel("API_NOTIFICATION");
            //update transaction status to success
            Transaction transactionManaged= transactionService.getTransactionByRefNumber(otpVerifyRequestDTO.getRefNumber());
            transactionManaged.setTransactionStatus(TransactionStatus.Pending);
            //update the transaction status
            transactionManaged= transactionService.save(transactionManaged);

            MonnifyChargeCardResponse monnifyChargeCardResponse = monnifyClient.chargeCard(monnifyChargeCardRequest);

            ResponseDTO<ChargeCardResponseDTO> responseDTO = new ResponseDTO<>();
            //check for failed transaction
            if(monnifyChargeCardResponse.getRequestSuccessful()){
                //if transaction is successful
                ChargeCardResponseDTO chargeCardResponseDTO = new ChargeCardResponseDTO(monnifyChargeCardResponse.getResponseBody().getAuthorizedAmount(),
                        false, monnifyChargeCardResponse.getResponseBody().getOtpData().getId(), otpVerifyRequestDTO.getRefNumber(), otpVerifyRequestDTO.getPurchaseId());
                responseDTO.setBody(chargeCardResponseDTO);
                responseDTO.setStatus(ResponseStatus.Success);
                responseDTO.setMessage(monnifyChargeCardResponse.getResponseBody().getMessage());

                transactionManaged.setTransactionStatus(TransactionStatus.Successful);

            }else{//if transaction failed
                responseDTO.setBody(null);
                responseDTO.setStatus(ResponseStatus.Error);
                responseDTO.setMessage(monnifyChargeCardResponse.getResponseMessage());
                transactionManaged.setTransactionStatus(TransactionStatus.Failed);
            }

            transactionService.save(transactionManaged);

            return new ResponseEntity<ResponseDTO<ChargeCardResponseDTO>>(responseDTO, HttpStatus.OK);


        }catch (NoSuchElementException e){
            log.error("MonnifyController: verifyOTP: NoSuchElementException: "+e.getMessage()+" with refNumber = "+otpVerifyRequestDTO.getRefNumber());
            return new ResponseEntity<>(new  ResponseDTO<ChargeCardResponseDTO>(ResponseStatus.Error, e.getMessage()), HttpStatus.FAILED_DEPENDENCY);
        }catch (MonnifyCardException e){
            log.error("MonnifyController: verifyOTP: MonnifyCardException: "+e.getMessage()+" with refNumber = "+otpVerifyRequestDTO.getRefNumber());
            return  new ResponseEntity<>((new  ResponseDTO<ChargeCardResponseDTO>(ResponseStatus.Error, e.getMessage())), HttpStatus.NOT_FOUND);
        }

    }






}
