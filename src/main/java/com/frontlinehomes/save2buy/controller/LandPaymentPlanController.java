package com.frontlinehomes.save2buy.controller;

import com.frontlinehomes.save2buy.data.ResponseDTO;
import com.frontlinehomes.save2buy.data.ResponseStatus;
import com.frontlinehomes.save2buy.data.land.data.*;
import com.frontlinehomes.save2buy.data.land.request.CalculatorConfigDTO;
import com.frontlinehomes.save2buy.data.land.request.DurationDTO;
import com.frontlinehomes.save2buy.data.land.request.PaymentPlanDTO;
import com.frontlinehomes.save2buy.data.land.request.PeriodDTO;
import com.frontlinehomes.save2buy.data.land.response.DurationResponseDTO;
import com.frontlinehomes.save2buy.data.land.response.PaymentPlanResponseDTO;
import com.frontlinehomes.save2buy.exception.EntityDuplicationException;
import com.frontlinehomes.save2buy.exception.NotNullFieldException;
import com.frontlinehomes.save2buy.service.land.LandService;
import com.frontlinehomes.save2buy.service.landPaymentPlan.LandPaymentPlanService;
import com.frontlinehomes.save2buy.service.paymentUtil.PaymentUtilService;
import com.frontlinehomes.save2buy.service.utils.DTOUtility;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/payment-plan")
public class LandPaymentPlanController {
    @Autowired
    private LandService landService;

    @Autowired
    private LandPaymentPlanService landPaymentPlanService;

    @Autowired
    private PaymentUtilService paymentUtilService;
    private static Logger log = LogManager.getLogger( LandPaymentPlanController.class);


    /**
     *
     *
     * @param paymentPlanDTO
     * @param id
     * @return ResponseEntity<PaymentPlanResponseDTO>
     *
     *     A controller endpoint that is used to create a payment plan for a land
     */

    @CrossOrigin( allowedHeaders = {"Authorization", "Content-Type"}, methods = {RequestMethod.POST})
    @PostMapping("/create/land/{id}")
    public ResponseEntity<ResponseDTO<PaymentPlanResponseDTO>> createLandPaymentPlan(@RequestBody PaymentPlanDTO paymentPlanDTO, @PathVariable Long id){
        try{

            if(paymentPlanDTO.getDurationLength()==  null ) throw new NotNullFieldException("missing required field durationLength");

            if(paymentPlanDTO.getDurationType()== null) throw new NotNullFieldException("missing required field durationType ");

            Land land= landService.getLand(id);

            if(land!= null){
                Boolean durationFound= false;
                PaymentPlan paymentPlan = DTOUtility.convertPaymentPlanDTOtoPaymentPlan(paymentPlanDTO);
                //check if the durationLength is set and durationType is set and if duration length is set

                log.info("LandPaymentPlanController:createLandPaymentPlan:  paymentPlanDTO converted");

                //check if this duration exist
                Duration duration= paymentUtilService.getByLengthAndType(paymentPlanDTO.getDurationLength(), paymentPlanDTO.getDurationType());

                log.info("LandPaymentPlanController:createLandPaymentPlan:  duration after lookup" +duration);

                Duration durationNew;
                    if(duration == null){
                        durationFound= false;
                        //create a new duration
                        Duration duration1= new Duration();

                        duration1.setLength(paymentPlanDTO.getDurationLength());
                        duration1.setFrequency(paymentPlanDTO.getDurationType());

                        PaymentPlan paymentPlan1= paymentPlan;
                        durationNew= duration1;
                    }else{
                        durationFound=true;
                        durationNew=duration;
                    }

               //duplicate a payment plan
                PaymentPlan paymentPlanClone= new PaymentPlan();
                    BeanUtils.copyProperties(paymentPlan,paymentPlanClone);


                //get the payment plan
               Set<LandPaymentPlan> landPaymentPlanSet= land.getLandPaymentPlans();
               paymentPlanClone.setDuration(duration);

                //check if the payment plan already exist for the specified land
                landPaymentPlanSet.forEach(landPaymentPlan1 -> {
                    if(landPaymentPlan1.getPaymentPlan().equals(paymentPlanClone)){
                        throw new EntityDuplicationException("Payment Plan Already Exist");
                    }
                });

                //check if payment plan already exist
                PaymentPlan paymentPlanManaged= landPaymentPlanService.getPlanByAmountAndFrequencyAndDurationAndCharges(paymentPlanClone.getAmount(),paymentPlanClone.getFrequency(), paymentPlanClone.getDuration(), paymentPlanClone.getCharges());

                if(paymentPlanManaged == null){



                    //save the paymentPlan
                    paymentPlanManaged= landPaymentPlanService.savePaymentPlan(paymentPlan);
                }

                Duration durationManaged= null;

                if(durationFound){
                    durationManaged= durationNew;
                }else{
                    durationManaged=paymentUtilService.addDuration(durationNew);
                }


                durationManaged.addPaymentPlan(paymentPlanManaged);

                //add new land payment plan to land
                LandPaymentPlan landPaymentPlan= new LandPaymentPlan();

                paymentPlanManaged.addLandPaymentPlan(landPaymentPlan);

                land.addLandPaymentPlan(landPaymentPlan);

                //persist the landPayment Plan
                LandPaymentPlan landPaymentPlanManaged= landPaymentPlanService.saveLandPaymentPlan(landPaymentPlan);
                ResponseDTO responseDTO= new ResponseDTO(ResponseStatus.Success, "Successful");
                responseDTO.setBody(DTOUtility.convertPaymentPlanToResponseDTO(paymentPlanManaged));

                return  new ResponseEntity< ResponseDTO<PaymentPlanResponseDTO>>(responseDTO,HttpStatus.OK);

            }else{
                return new ResponseEntity<ResponseDTO<PaymentPlanResponseDTO>>(new ResponseDTO<PaymentPlanResponseDTO>(ResponseStatus.Error, "Land not found"), HttpStatus.NOT_FOUND);
            }
        }catch (EntityDuplicationException e){
            log.info("LandPaymentPlanController:createLandPaymentPlan:  "+e.getMessage());
            return new ResponseEntity<ResponseDTO<PaymentPlanResponseDTO>>(new ResponseDTO<PaymentPlanResponseDTO>(ResponseStatus.Error, e.getMessage()), HttpStatus.BAD_REQUEST);
        }catch (NotNullFieldException e){
            return new ResponseEntity<ResponseDTO<PaymentPlanResponseDTO>>(new ResponseDTO<PaymentPlanResponseDTO>(ResponseStatus.Error, e.getMessage()), HttpStatus.BAD_REQUEST);
        }catch (NoSuchElementException e){
            return new ResponseEntity<ResponseDTO<PaymentPlanResponseDTO>>(new ResponseDTO<PaymentPlanResponseDTO>(ResponseStatus.Error, e.getMessage()), HttpStatus.NOT_FOUND);

        }
    }

    @CrossOrigin( allowedHeaders = {"Authorization"})
    @GetMapping("/land/{id}")
    public ResponseEntity getLandPaymentPlans(@PathVariable Long id){

        //get land
        try{
            Land land= landService.getLand(id);
            if(land== null)
                throw new   ResponseStatusException(HttpStatus.NOT_FOUND, "The requested resource could not be found");

            ArrayList paymentPlans= new ArrayList<PaymentPlanResponseDTO>();

             land.getLandPaymentPlans().forEach(landPaymentPlan -> {
                  paymentPlans.add(DTOUtility.convertPaymentPlanToResponseDTO(landPaymentPlan.getPaymentPlan()));
             });
             ResponseDTO responseDTO= new ResponseDTO(ResponseStatus.Success, "Successful");
             responseDTO.setBody(paymentPlans);
             return new ResponseEntity<ResponseDTO<ArrayList<PaymentPlanResponseDTO>>>(responseDTO,HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<ResponseDTO>(new ResponseDTO(ResponseStatus.Error,e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }


    @CrossOrigin( allowedHeaders = {"Authorization", "Content-Type"}, methods = {RequestMethod.POST})
    @PostMapping("/duration/create")
    public ResponseEntity<ResponseDTO<DurationResponseDTO>> createDuration(DurationDTO durationDTO){

        //validate durationDTO field
        if(durationDTO.getFrequency() == null || durationDTO.getLength() == null) return new ResponseEntity<ResponseDTO<DurationResponseDTO>>(new ResponseDTO<>(ResponseStatus.Error, "frequency and length fields are required"), HttpStatus.BAD_REQUEST);

        Duration duration= DTOUtility.convertDurationDTOtoDuration(durationDTO);

        DurationResponseDTO durationResponseDTO= DTOUtility.convertDurationResponseDTOtoDuration(paymentUtilService.addDuration(duration));
        ResponseDTO<DurationResponseDTO> responseDTO= new ResponseDTO<DurationResponseDTO>(ResponseStatus.Success, "Successful");
        responseDTO.setBody(durationResponseDTO);
        return new ResponseEntity<ResponseDTO<DurationResponseDTO>>(responseDTO, HttpStatus.OK);

    }

    @CrossOrigin( allowedHeaders = {"Authorization", "Content-Type"}, methods = {RequestMethod.POST})
    @PostMapping("/configuration/create")
    public ResponseEntity<ResponseDTO<CalculatorConfigDTO>> createCalculatorConfig(@RequestBody  CalculatorConfigDTO calculatorConfigDTO){

        //validate fields
        if(calculatorConfigDTO.getLandId() == null) return  new ResponseEntity<ResponseDTO<CalculatorConfigDTO>>(new ResponseDTO(ResponseStatus.Error, "landId is required"), HttpStatus.BAD_REQUEST);

        if(calculatorConfigDTO.getDurationList() == null) return new ResponseEntity<ResponseDTO<CalculatorConfigDTO>>(new ResponseDTO(ResponseStatus.Error, "duration is required"), HttpStatus.BAD_REQUEST);

        if(calculatorConfigDTO.getDurationList() == null) return new ResponseEntity<ResponseDTO<CalculatorConfigDTO>>(new ResponseDTO(ResponseStatus.Error, "frequency is required"), HttpStatus.BAD_REQUEST);

        try{
            Land land=landService.getLand(calculatorConfigDTO.getLandId());

           List<DurationDTO> durationDTO= calculatorConfigDTO.getDurationList();

           //get all durations
            List<Duration> durationList = paymentUtilService.getAllDuration();

            Set<LandCalculatorConfigDuration> configDurationSet= new HashSet<LandCalculatorConfigDuration>();

            //scan through all duration
            for (DurationDTO dto : durationDTO) {
                Boolean found =false;
                for (Duration duration : durationList) {

                    if( duration.getFrequency().equals(dto.getFrequency()) && duration.getLength().equals(dto.getLength())){
                        //create an array of LandCalculatorConfigDuration
                        LandCalculatorConfigDuration configDuration= new LandCalculatorConfigDuration();
                        duration.addLandCalculatorConfigDuration(configDuration);
                        configDurationSet.add(configDuration);
                       found= true;
                    }
                }


                if(!found){
                    //create a new duration
                    Duration duration1= new Duration();
                    duration1.setLength(dto.getLength());
                    duration1.setFrequency(dto.getFrequency());

                    //persist the duration
                    Duration durationManaged= paymentUtilService.addDuration(duration1);
                    LandCalculatorConfigDuration configDuration= new LandCalculatorConfigDuration();
                    durationManaged.addLandCalculatorConfigDuration(configDuration);
                    configDurationSet.add(configDuration);
                }
            }


            //get all periods
            List<Period> periodList = paymentUtilService.getAllPeriod();
            List<PeriodDTO> periodDTOList= calculatorConfigDTO.getPeriodList();

            Set<LandCalculatorConfigPeriod> calculatorConfigPeriodSet = new HashSet<>();
            for (PeriodDTO periodDTO : periodDTOList) {
                Boolean found=false;
                for (Period period : periodList) {
                    if(period.getFrequency().equals(periodDTOList)){
                        //create a new calculatorConfigPeriods
                        LandCalculatorConfigPeriod calculatorConfigPeriod= new LandCalculatorConfigPeriod();
                        period.addLandCalculatorConfigPeriod(calculatorConfigPeriod);
                        calculatorConfigPeriodSet.add(calculatorConfigPeriod);
                    }
                }

                if(!found){
                    LandCalculatorConfigPeriod calculatorConfigPeriod= new LandCalculatorConfigPeriod();
                    //insert the period
                    Period period= new Period();
                    period.setFrequency(periodDTO.getFrequency());
                    Period periodManaged= paymentUtilService.addPeriod(period);
                    periodManaged.addLandCalculatorConfigPeriod(calculatorConfigPeriod);
                    calculatorConfigPeriodSet.add(calculatorConfigPeriod);
                }
            }

            //create a new LandCalculatorConfig
            LandCalculatorConfig landCalculatorConfig= new LandCalculatorConfig();
            Double maxLandSize= calculatorConfigDTO.getMaxLandSize() != null ? calculatorConfigDTO.getMinLandSize() : land.getAvailableSize();
            Double minLandSize= calculatorConfigDTO.getMinLandSize() != null ? calculatorConfigDTO.getMinLandSize() : 255.0;
            landCalculatorConfig.setMaxLandSize(maxLandSize);
            landCalculatorConfig.setMinLandSize(minLandSize);
            land.addCalculatorConfig(landCalculatorConfig);

            //persist the landCalculatorConfig
            LandCalculatorConfig landCalculatorConfigManaged= paymentUtilService.addCalculatorConfig(landCalculatorConfig);


            //add all the set of landCalculatorConfigDuration
            for (LandCalculatorConfigDuration calculatorConfigDuration : configDurationSet) {
                landCalculatorConfigManaged.addLandCalculatorConfigDuration(calculatorConfigDuration);
            }

            //update the calculator config
            landCalculatorConfigManaged= paymentUtilService.addCalculatorConfig(landCalculatorConfig);

            for (LandCalculatorConfigPeriod calculatorConfigPeriod : calculatorConfigPeriodSet) {
                landCalculatorConfigManaged.addLandCalculatorConfigPeriod(calculatorConfigPeriod);
            }

            //update the calculator config
            landCalculatorConfigManaged = paymentUtilService.addCalculatorConfig(landCalculatorConfig);

            landService.addLand(land);

            ResponseDTO<CalculatorConfigDTO> responseDTO= new ResponseDTO<CalculatorConfigDTO>(ResponseStatus.Success, "Successful");
            responseDTO.setBody(calculatorConfigDTO);
            return  new ResponseEntity<ResponseDTO<CalculatorConfigDTO>>(responseDTO, HttpStatus.OK);


        }catch (NoSuchElementException e){
            return  new ResponseEntity<ResponseDTO<CalculatorConfigDTO>>(new ResponseDTO(ResponseStatus.Error, e.getMessage()), HttpStatus.NOT_FOUND);
        }

    }

    @CrossOrigin( allowedHeaders = {"Authorization"})
    @DeleteMapping("/land/{id}/{planId}")
    public ResponseEntity<ResponseDTO> removeLandPaymentPlan(@PathVariable Long id, @PathVariable Long planId){
            try{
                Land land= landService.getLand(id);

                //get the land paymentPlanId
                PaymentPlan paymentPlan= landPaymentPlanService.getPaymentPlanById(planId);

                LandPaymentPlan landPaymentPlanManaged= null;
                for (LandPaymentPlan landPaymentPlan : land.getLandPaymentPlans()) {
                    if(paymentPlan.equals(landPaymentPlan.getPaymentPlan())){
                         landPaymentPlanManaged= landPaymentPlan;
                    }
                }

                if(landPaymentPlanManaged == null) return  new ResponseEntity<ResponseDTO>(new ResponseDTO(ResponseStatus.Error,"Land does not contain plan"), HttpStatus.NOT_FOUND);
                paymentPlan.removeLandPaymentPlan(landPaymentPlanManaged);
                land.removeLandPaymentPlan(landPaymentPlanManaged);

                   //update the land
                landService.addLand(land);

                //update the paymentPlan
                landPaymentPlanService.savePaymentPlan(paymentPlan);
                return new ResponseEntity<ResponseDTO>( new ResponseDTO(ResponseStatus.Success,"successfully deleted"), HttpStatus.OK);

            }catch (NoSuchElementException e){
                return  new ResponseEntity<ResponseDTO>(new ResponseDTO(ResponseStatus.Error,e.getMessage()), HttpStatus.NOT_FOUND);
            }
    }




}
