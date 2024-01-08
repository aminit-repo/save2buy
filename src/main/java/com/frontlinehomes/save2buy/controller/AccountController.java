package com.frontlinehomes.save2buy.controller;

import com.frontlinehomes.save2buy.data.ResponseDTO;
import com.frontlinehomes.save2buy.data.ResponseStatus;
import com.frontlinehomes.save2buy.data.account.data.Transaction;
import com.frontlinehomes.save2buy.data.account.request.TransactionRequest;
import com.frontlinehomes.save2buy.data.account.response.TransactionResponseDTO;
import com.frontlinehomes.save2buy.data.land.data.InvestorLand;
import com.frontlinehomes.save2buy.data.land.data.LandStatus;
import com.frontlinehomes.save2buy.data.users.User;
import com.frontlinehomes.save2buy.service.UserService;
import com.frontlinehomes.save2buy.service.transaction.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
import java.util.*;

@RestController
@RequestMapping("/account")
public class AccountController {
    @Autowired
    private UserService userService;
    @Autowired
    private TransactionService transactionService;

   /* @GetMapping("/transaction")
    public ResponseEntity<ResponseDTO<List<TransactionResponseDTO>>> getInvestorTransactions(@RequestBody TransactionRequest transactionRequest){
        try {

            //check if the user exist
            User user= userService.getUser(transactionRequest.getId());

            //get the investor details
            if(user.getInvestor()== null) return  new ResponseEntity<ResponseDTO<List<TransactionResponseDTO>>>(new ResponseDTO<List<TransactionResponseDTO>>(ResponseStatus.Error, "user is not an investor"), HttpStatus.BAD_REQUEST);

            //get all investors land of the user
            List<InvestorLand>  investorLandList=user.getInvestor().getInvestorLands();

            //transactions performed on a particular account
            List<Transaction> accountTransactions= user.getInvestor().getAccount().getTransactionList();

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



            //retain transactions that are not duplicate
            for (Transaction accountTransaction : accountTransactions) {
                if(!transactionList.contains(accountTransaction)){
                    transactionList.add(accountTransaction);
                }
            }

            //sort all transactions according to date







        }catch (NoSuchElementException e){
            return  new ResponseEntity<ResponseDTO<List<TransactionResponseDTO>>>(new ResponseDTO<List<TransactionResponseDTO>>(ResponseStatus.Error, e.getMessage()), HttpStatus.NOT_FOUND);
        }



    } */

   /* @GetMapping("/transaction/{id}")
    public ResponseEntity getTransactionDetails(@PathVariable  Long id){

    } */
}


