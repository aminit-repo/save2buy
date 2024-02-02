package com.frontlinehomes.save2buy.controller;

import com.frontlinehomes.save2buy.data.ResponseDTO;
import com.frontlinehomes.save2buy.data.ResponseStatus;
import com.frontlinehomes.save2buy.data.account.data.Channel;
import com.frontlinehomes.save2buy.data.account.data.Transaction;
import com.frontlinehomes.save2buy.data.account.data.TransactionStatus;
import com.frontlinehomes.save2buy.data.account.request.CreateTransacionRequest;
import com.frontlinehomes.save2buy.data.account.request.TransactionRequest;
import com.frontlinehomes.save2buy.data.account.response.CreateTransactionResponseDTO;
import com.frontlinehomes.save2buy.data.account.response.TransactionResponseDTO;
import com.frontlinehomes.save2buy.data.land.data.InvestorLand;
import com.frontlinehomes.save2buy.data.land.data.LandStatus;
import com.frontlinehomes.save2buy.data.land.response.MileStoneResponseDTO;
import com.frontlinehomes.save2buy.data.users.User;
import com.frontlinehomes.save2buy.data.verification.JwtDetails;
import com.frontlinehomes.save2buy.service.JWTService;
import com.frontlinehomes.save2buy.service.UserService;
import com.frontlinehomes.save2buy.service.investorLand.InvestorLandService;
import com.frontlinehomes.save2buy.service.paymentUtil.PaymentUtilService;
import com.frontlinehomes.save2buy.service.transaction.TransactionService;
import com.frontlinehomes.save2buy.service.utils.DTOUtility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.NoSuchElementException;
import java.util.*;

/**
 *
 * Custom Errors originated from AccountController starts with the code 6XX
 */

@RestController
@RequestMapping("/account")
public class AccountController {
    @Autowired
    private UserService userService;
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private InvestorLandService investorLandService;
    @Autowired
    private PaymentUtilService paymentUtilService;

    @Autowired
    private JWTService jwtService;
    private static Logger log = LogManager.getLogger(AccountController.class);
    @CrossOrigin(allowedHeaders = {"Authorization"})
    @GetMapping("/transactions/{id}")
    public ResponseEntity<ResponseDTO<List<TransactionResponseDTO>>> getInvestorTransactions(@RequestHeader("Authorization") String token, @PathVariable Long id){

        try {
            if(id == null)  return  new ResponseEntity<ResponseDTO<List<TransactionResponseDTO>>>(new ResponseDTO<List<TransactionResponseDTO>>(ResponseStatus.Error, "user id is required"), HttpStatus.BAD_REQUEST);

            //JwtDetails details =jwtService.getTokenDetails(token);

            //check if the user exist
            User user=null;

            user= userService.getUser(id);

            if(user.getInvestor()== null) return  new ResponseEntity<ResponseDTO<List<TransactionResponseDTO>>>(new ResponseDTO<List<TransactionResponseDTO>>(ResponseStatus.Error, "Account is not an Investor's account"), HttpStatus.BAD_REQUEST);

            /*if(details.getIsAdmin()){
                //this is an admin

            } else {
                //investors should access only their resources
                if(!user.getEmail().equals(details.getUsername()))  return  new ResponseEntity<ResponseDTO<List<TransactionResponseDTO>>>(new ResponseDTO<List<TransactionResponseDTO>>(ResponseStatus.Error, "invalid access token"), HttpStatus.UNAUTHORIZED);
            } */


            //get all investors land of the user
            List<InvestorLand>  investorLandList=user.getInvestor().getInvestorLands();

            List<Transaction> accountTransactions=null;
            boolean accountExist= false;
            if(user.getInvestor().getAccount() != null){
                //transactions performed on a particular account
                accountTransactions= user.getInvestor().getAccount().getTransactionList();
                accountExist= true;
            }


            List<Transaction>  transactionList= new ArrayList<Transaction>();

            for (InvestorLand investorLand : investorLandList) {
                //sort out transactions for only initiated or, purchased items
                if(investorLand.getLandStatus() != LandStatus.Wishlist || investorLand.getLandStatus() != LandStatus.CheckOut){
                    List<Transaction> list= transactionService.getAllTransactionByInvestorLand(investorLand);
                    if(!list.isEmpty()){
                        //copy transaction to upper scoped list
                        transactionList.addAll(list);
                    }
                }
            }


            if(accountExist){
                //retain transactions that are not duplicate
                for (Transaction accountTransaction : accountTransactions) {
                    if(!transactionList.contains(accountTransaction)){
                        transactionList.add(accountTransaction);
                    }
                }

                //sort the array according to date
               /* if(transactionList.size() > 1){
                    //sort all transactions according to date
                    Timestamp LateTimestamp= transactionList.get(0).getCreatedTime();

                } */
            }
            ResponseDTO<List<TransactionResponseDTO>> transactionResponseDTOResponseDTO= new ResponseDTO<List<TransactionResponseDTO>>(ResponseStatus.Success, "Successful" );
            transactionResponseDTOResponseDTO.setBody(DTOUtility.convertTransactionToTransactionResponseDTO(transactionList));

            return new ResponseEntity<ResponseDTO<List<TransactionResponseDTO>>>(transactionResponseDTOResponseDTO, HttpStatus.OK);

        }catch (NoSuchElementException e){
            return  new ResponseEntity<ResponseDTO<List<TransactionResponseDTO>>>(new ResponseDTO<List<TransactionResponseDTO>>(ResponseStatus.Error, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }


    @CrossOrigin(allowedHeaders = {"Authorization"})
    @GetMapping("/initiated-transactions/{id}")
    public ResponseEntity<ResponseDTO<List<TransactionResponseDTO>>> getInvestorInitTransactions(@RequestHeader("Authorization") String token, @PathVariable Long id){

        try {

            if(id == null)  return  new ResponseEntity<ResponseDTO<List<TransactionResponseDTO>>>(new ResponseDTO<List<TransactionResponseDTO>>(ResponseStatus.Error, "user id is required"), HttpStatus.BAD_REQUEST);

            //JwtDetails details =jwtService.getTokenDetails(token);

            //check if the user exist
            User user=null;

            user= userService.getUser(id);

            if(user.getInvestor()== null) return  new ResponseEntity<ResponseDTO<List<TransactionResponseDTO>>>(new ResponseDTO<List<TransactionResponseDTO>>(ResponseStatus.Error, "Account is not an Investor's account"), HttpStatus.BAD_REQUEST);

            /*if(details.getIsAdmin()){
                //this is an admin

            } else {
                //investors should access only their resources
                if(!user.getEmail().equals(details.getUsername()))  return  new ResponseEntity<ResponseDTO<List<TransactionResponseDTO>>>(new ResponseDTO<List<TransactionResponseDTO>>(ResponseStatus.Error, "invalid access token"), HttpStatus.UNAUTHORIZED);
            } */


            //get all investors land of the user
            List<InvestorLand>  investorLandList=user.getInvestor().getInvestorLands();

            List<Transaction> accountTransactions=null;
            boolean accountExist= false;
            if(user.getInvestor().getAccount() != null){
                //transactions performed on a particular account
                accountTransactions= user.getInvestor().getAccount().getTransactionList();
                accountExist= true;
            }


            List<Transaction>  transactionList= new ArrayList<Transaction>();

            for (InvestorLand investorLand : investorLandList) {
                //sort out transactions for only initiated or, purchased items
                if(investorLand.getLandStatus() != LandStatus.Wishlist || investorLand.getLandStatus() != LandStatus.CheckOut){
                    List<Transaction> list= transactionService.getAllTransactionByInvestorLand(investorLand);
                    if(!list.isEmpty()){
                        //copy transaction to upper scoped list
                        for (Transaction transaction : list) {
                            if(transaction.getTransactionStatus().equals(TransactionStatus.Pending) ||  transaction.getTransactionStatus().equals(TransactionStatus.Successful) || transaction.getTransactionStatus().equals(TransactionStatus.Failed)){
                                transactionList.add(transaction);
                            }
                        }

                    }
                }
            }


            if(accountExist){
                //retain transactions that are not duplicate
                for (Transaction accountTransaction : accountTransactions) {
                    if(!transactionList.contains(accountTransaction)){
                        transactionList.add(accountTransaction);
                    }
                }

                //sort the array according to date
               /* if(transactionList.size() > 1){
                    //sort all transactions according to date
                    Timestamp LateTimestamp= transactionList.get(0).getCreatedTime();

                } */
            }
            ResponseDTO<List<TransactionResponseDTO>> transactionResponseDTOResponseDTO= new ResponseDTO<List<TransactionResponseDTO>>(ResponseStatus.Success, "Successful" );
            transactionResponseDTOResponseDTO.setBody(DTOUtility.convertTransactionToTransactionResponseDTO(transactionList));

            return new ResponseEntity<ResponseDTO<List<TransactionResponseDTO>>>(transactionResponseDTOResponseDTO, HttpStatus.OK);

        }catch (NoSuchElementException e){
            return  new ResponseEntity<ResponseDTO<List<TransactionResponseDTO>>>(new ResponseDTO<List<TransactionResponseDTO>>(ResponseStatus.Error, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @CrossOrigin( allowedHeaders = {"Authorization", "Content-Type"}, methods = {RequestMethod.PUT})
    @PostMapping("/create-transaction/{id}")
   public ResponseEntity createTransaction(@RequestBody CreateTransacionRequest transacionRequest, @PathVariable Long id){

        //check for required field
        if(transacionRequest.getPaymentMethod() == null)  return new ResponseEntity<ResponseDTO>(new ResponseDTO<>(ResponseStatus.Error, "Payment Method is required"), HttpStatus.BAD_REQUEST);

     //   if(transacionRequest.getPaymentPlanId() == null) return new ResponseEntity<ResponseDTO>(new ResponseDTO<>(ResponseStatus.Error, "Payment Plan is required"), HttpStatus.BAD_REQUEST);

        if(transacionRequest.getAmount() == null) return new ResponseEntity<ResponseDTO>(new ResponseDTO<>(ResponseStatus.Error, "Amount is required"), HttpStatus.BAD_REQUEST);

        User user=null;

        user= userService.getUser(id);

        if(user.getInvestor()== null) return  new ResponseEntity<ResponseDTO<List<TransactionResponseDTO>>>(new ResponseDTO<List<TransactionResponseDTO>>(ResponseStatus.Error, "Account is not an Investor's account"), HttpStatus.BAD_REQUEST);

        //get the investor's land
        InvestorLand investorLand= investorLandService.getInvestorLand(transacionRequest.getPurchaseId());

        //verify that the land is initiated already

        if(!investorLand.getLandStatus().equals(LandStatus.Initiated))   return new ResponseEntity<ResponseDTO>(new ResponseDTO<>(ResponseStatus.Error, "Land purchase not initiated"), HttpStatus.NOT_ACCEPTABLE);



        //create a new Transaction
        Transaction transaction= new Transaction();
        transaction.setBillingType(transacionRequest.getPaymentMethod());
        String paymentRef= paymentUtilService.getRefNumber();
        transaction.setRefNumber(paymentRef);
        transaction.setChannel(transacionRequest.getChannel());
        transaction.setTransactionStatus(transacionRequest.getTransactionStatus());
        transaction.setType(transacionRequest.getTransactionType());
        transaction.setTransactionId(transacionRequest.getTransactionId());

        transaction.setAmount(transacionRequest.getAmount());

        Transaction transactionManaged= transactionService.save(transaction);


        //
        investorLand.addTransaction(transaction);

        investorLand = investorLandService.addInvestorLand(investorLand);


        //set milestone

              //calculate the milestone covered.

              if(investorLand.getMilestone()== null){
                  investorLand.setMilestone(1);
              }else{
                  //calculate all the completed transactions on this investorLand
                  Double paid=0.0;

                  //get all transactions for the investors land
                  for (Transaction t : investorLand.getTransactionList()) {
                      //check the transaction status if is successfull.
                      if(t.getTransactionStatus().equals(TransactionStatus.Successful)){
                          paid+= t.getAmount();
                      }
                  }


                  Double percentage= (paid / investorLand.getAmount()) * 100;
                  if(percentage >= 70 && percentage < 90 ){
                      investorLand.setMilestone(3);
                  }

                  if(percentage >= 90 && percentage <=99 ){
                      investorLand.setMilestone(4);
                  }

                  if(percentage > 99){
                      investorLand.setMilestone(5);
                  }
              }


              investorLand.setMilestone(1);


              //update the investor land again

        investorLandService.addInvestorLand(investorLand);

        ResponseDTO<CreateTransactionResponseDTO> responseDTO= new ResponseDTO<>(ResponseStatus.Success, "Successful");
        responseDTO.setBody(DTOUtility.convertTransactionToCreateTransactionResponseDTO(transactionManaged));

        return  new ResponseEntity< ResponseDTO<CreateTransactionResponseDTO>>(responseDTO, HttpStatus.OK);

    }

   /* @CrossOrigin(allowedHeaders = {"Authorization"})
    @GetMapping("/balance-details/{id}")
    public ResponseEntity getWalletBalanceDetails(@PathVariable Long id){
        //check if the user exist
        User user=null;

        user= userService.getUser(id);

        if(user.getInvestor()== null) return  new ResponseEntity<ResponseDTO<List<TransactionResponseDTO>>>(new ResponseDTO<List<TransactionResponseDTO>>(ResponseStatus.Error, "Account is not an Investor's account"), HttpStatus.BAD_REQUEST);



    } */

   /* @GetMapping("/transactions/{id}")
    public ResponseEntity getTransactionDetails(@PathVariable  Long id){

    } */


}


