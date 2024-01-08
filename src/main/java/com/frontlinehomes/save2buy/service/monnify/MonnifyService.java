package com.frontlinehomes.save2buy.service.monnify;

import com.frontlinehomes.save2buy.client.monnify.*;
import com.frontlinehomes.save2buy.data.Monnify;
import com.frontlinehomes.save2buy.exception.*;
import com.frontlinehomes.save2buy.repository.MonnifyRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Service
@Transactional
public class MonnifyService {
    @Autowired
    private MonnifyRepository monnifyRepository;

    @Autowired
    private MonnifyClient monnifyClient;

    private static Logger log = LogManager.getLogger( MonnifyService.class);

    public Monnify getExpireTime(){
        try {
            Optional<Monnify> monnify= monnifyRepository.findById(1L);
            if(monnify.isEmpty()) throw new EntityNotFoundException("");
            return monnify.get();
        }catch (EntityNotFoundException e){
            throw new EntityNotFoundException("");
        }
    }

    public void saveExpireTime(Long expireIn){
        //create a timestamp object object;
        LocalDateTime localDateTime= LocalDateTime.of(LocalDate.now(), LocalTime.now());
        localDateTime.plusHours((expireIn-10));

        //check if there is an entity already saved
        try{
           Monnify monnify=  getExpireTime();
           // update the expiry time
            monnify.setExpireTime(localDateTime);
            monnifyRepository.save(monnify);
        }catch (EntityNotFoundException e){
            //create a new Monnify object
            Monnify monnify= new Monnify();
            monnify.setExpireTime(localDateTime);
            monnifyRepository.save(monnify);
        }
    }


    public MonnifyInitResponse initializeTransaction(MonnifyInitRequest initRequest) throws MonnifyServiceException, MonnifyRefException, MonnifyInitException {

        try{
            return  monnifyClient.initializeTransaction(initRequest);
        }catch (MonnifyAuthException e){

            //if regeneration of access token fails, then send, service unavailable
            if(!monnifyClient.regenerateAccessToken()){
                throw  new MonnifyServiceException("service unavailable");
            }
            //try to reinitialize transaction

            try{
               return monnifyClient.initializeTransaction(initRequest);
            }catch (MonnifyServiceException ms){
                log.error("MonnifyService:initializeTransaction: "+ms.getMessage());
                throw  new MonnifyServiceException("service unavailable");
            }catch (MonnifyRefException mr){
                log.error("MonnifyService:initializeTransaction: "+mr.getMessage());
                throw  new MonnifyRefException(mr.getMessage());
            }catch (MonnifyInitException mi){
                log.error("MonnifyService:initializeTransaction: "+mi.getMessage());
                throw new MonnifyInitException("error trying to create transaction");
            }catch (MonnifyAuthException ma){
                throw new MonnifyServiceException("service unavailable ");
            }


        }


    }
    
    public MonnifyChargeCardResponse chargeCard(MonnifyChargeCardRequest monnifyChargeCardRequest){
        try{
            return  monnifyClient.chargeCard(monnifyChargeCardRequest);
        }catch (MonnifyAuthException e){

            //if regeneration of access token fails, then send, service unavailable
            if(!monnifyClient.regenerateAccessToken()){
                throw  new MonnifyServiceException("service unavailable");
            }

            try{
                return  monnifyClient.chargeCard(monnifyChargeCardRequest);
            }catch (MonnifyAuthException ex){
                throw new MonnifyServiceException("service unavailable");
            }catch (MonnifyRefException ex){
                throw  new MonnifyRefException("duplicate transaction");
            }catch (MonnifyServiceException ex){
                throw new MonnifyServiceException("service unavailable");
            }catch (MonnifyBadFieldException ex){
                throw new MonnifyBadFieldException(ex.getMessage());
            }catch (MonnifyCardException ex){
                throw new MonnifyServiceException(ex.getMessage());
            }

        }catch (MonnifyRefException e){
            throw  new MonnifyRefException("duplicate transaction");
        }catch (MonnifyServiceException e){
            throw new MonnifyServiceException("service unavailable");
        }catch (MonnifyBadFieldException e){
            throw new MonnifyBadFieldException(e.getMessage());
        }catch (MonnifyCardException e){
            throw new MonnifyServiceException(e.getMessage());
        }

    }

}
