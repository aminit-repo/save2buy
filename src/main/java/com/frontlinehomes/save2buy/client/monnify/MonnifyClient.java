package com.frontlinehomes.save2buy.client.monnify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frontlinehomes.save2buy.client.elasticMail.ElasticMail;
import com.frontlinehomes.save2buy.config.MonnifyProperties;
import com.frontlinehomes.save2buy.controller.InvestorController;
import com.frontlinehomes.save2buy.exception.*;
import com.frontlinehomes.save2buy.service.monnify.MonnifyService;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.net.URI;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Component
public class MonnifyClient {


    private MonnifyProperties  monnifyProperties;

    private WebClient  webClient;




    private static Logger log = LogManager.getLogger( MonnifyClient.class);


    public static ExchangeFilterFunction errorHandler() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().is5xxServerError()) {
                return clientResponse.bodyToMono(MonnifyErrorResponse.class)
                        .flatMap(errorBody -> Mono.error(new MonnifyServiceException(errorBody.getResponseMessage())));
            } else if (clientResponse.statusCode() == HttpStatus.UNAUTHORIZED) {
                return clientResponse.bodyToMono(MonnifyErrorResponse.class)
                        .flatMap(errorBody -> Mono.error(new MonnifyAuthException(errorBody.getResponseMessage())));
            } else if(clientResponse.statusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                return clientResponse.bodyToMono(MonnifyErrorResponse.class)
                        .flatMap(errorBody -> Mono.error(new MonnifyRefException(errorBody.getResponseMessage())));
            }else if(clientResponse.statusCode() == HttpStatus.BAD_REQUEST){
                return clientResponse.bodyToMono(MonnifyErrorResponse.class)
                        .flatMap(errorBody -> Mono.error(new MonnifyBadFieldException(errorBody.getResponseMessage())));
            }else {
                return Mono.just(clientResponse);
            }
        });
    }


    public MonnifyClient(WebClient.Builder builder, MonnifyProperties monnifyProperties) {
        this.monnifyProperties = monnifyProperties;
        this.webClient= builder.baseUrl(monnifyProperties.getBaseUrl()).filter(errorHandler()).build();
    }


    public MonnifyAuthBody getAccessToken() throws MonnifyAuthException{
        //get the base64(ApiKey:SecretKey)
        String basicAuth=  Base64.getEncoder().encodeToString( (monnifyProperties.getApiKey()+":"+monnifyProperties.getSecretKey()).getBytes());

        try{
            MonnifyAuth monnifyAuth= webClient.post().uri("/api/v1/auth/login")
                    .header("Authorization","Basic "+basicAuth)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(" ")
                    .retrieve()
                    .bodyToMono(MonnifyAuth.class)
                    .block();

            //set a new access token to the configuration file
            monnifyProperties.setAccessToken(monnifyAuth.getResponseBody().getAccessToken());

            log.info("MonnifyClient:getAccessToken:  success: "+ monnifyAuth.getResponseMessage());
            return monnifyAuth.getResponseBody();
        }catch (Exception e){
            log.error("MonnifyClient:getAccessToken: error:  "+ e.getMessage());
            throw  e;
        }
    }






    public Boolean regenerateAccessToken(){
        Boolean endPointAccess= true;
        try {
            MonnifyAuthBody monnifyAuthBody= getAccessToken();
            log.info("MonnifyClient:regenerateAccessToken: trying to obtain access token successful");
        }catch (Exception e){
            try {

                //if trying to get accessToken fails, try again one more time
                MonnifyAuthBody monnifyAuthBody= getAccessToken();
                log.info("MonnifyClient:regenerateAccessToken: trying to obtain access token successful");
            }catch (Exception ex){

                log.info("MonnifyClient:regenerateAccessToken: trying to obtain access token failed after two trials");
                log.error(ex.getMessage());
                endPointAccess= false;
            }
        }
        return  endPointAccess;
    }




    public MonnifyInitResponse initializeTransaction(MonnifyInitRequest initRequest) throws MonnifyInitException,
            MonnifyServiceException, MonnifyAuthException{

        try{

            MonnifyInitResponse initResponse = webClient.post().uri("/api/v1/merchant/transactions/init-transaction")
                    .header("Authorization","bearer "+monnifyProperties.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(initRequest)
                    .retrieve()
                    .bodyToMono(MonnifyInitResponse.class)
                    .block();

            log.info("MonnifyClient:initializeTransaction:  success: initializing transaction");
            return initResponse;

        }catch (MonnifyAuthException e){
            log.info("MonnifyClient:initializeTransaction:  error: "+e.getMessage());
            throw  new MonnifyAuthException(e.getMessage());
        }catch (MonnifyServiceException e){
            log.info("MonnifyClient:initializeTransaction:  error: "+e.getMessage());
            throw new MonnifyServiceException(e.getMessage());
        }catch (MonnifyBadFieldException e){
            log.info("MonnifyClient:initializeTransaction:  error: "+e.getMessage());
            throw new MonnifyBadFieldException(e.getMessage());
        }catch (MonnifyRefException e){
            log.info("MonnifyClient:initializeTransaction:  error: "+e.getMessage());
            throw  new MonnifyRefException(e.getMessage());
        }
        catch (Exception e){
            log.info("MonnifyClient:initializeTransaction:  error: "+e.getMessage());
            // throw  new MonnifyInitException(e.getMessage());
             throw new MonnifyInitException("error creating transaction");
        }

    }



    public MonnifyChargeCardResponse chargeCard(MonnifyChargeCardRequest chargeCardRequest) throws MonnifyCardException{

            try{
                MonnifyChargeCardResponse chargeCardResponse= webClient.post().uri("/api/v1/merchant/cards/charge")
                        .header("Authorization","bearer "+monnifyProperties.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(chargeCardRequest)
                        .retrieve()
                        .bodyToMono(MonnifyChargeCardResponse.class)
                        .block();

                return chargeCardResponse;
            }catch (MonnifyAuthException e){
                log.info("MonnifyClient:chargeCard:  error: "+e.getMessage());
                throw  new MonnifyAuthException(e.getMessage());
            }catch (MonnifyServiceException e){
                log.info("MonnifyClient:chargeCard:  error: "+e.getMessage());
                throw new MonnifyServiceException(e.getMessage());
            }catch (MonnifyBadFieldException e){
                log.info("MonnifyClient:chargeCard:  error: "+e.getMessage());
                throw new MonnifyBadFieldException(e.getMessage());
            }catch (MonnifyRefException e){
                log.info("MonnifyClient:chargeCard:  error: "+e.getMessage());
                throw  new MonnifyRefException(e.getMessage());
            }
            catch (Exception e){

                //calling this endpoint multiple times with the same transactionReference creates an uprocessableEntity status.
                log.error("MonnifyClient:chargeCard: error:  "+ e.getMessage());
                throw  new MonnifyCardException("error charging card");
            }



    }

    /**
     *
     *
     * @param transactionId
     * @return
     */


    public void verifyTransactionStatus(String transactionId){

    }
















}
