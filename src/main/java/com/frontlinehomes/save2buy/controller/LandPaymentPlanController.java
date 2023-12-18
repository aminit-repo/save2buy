package com.frontlinehomes.save2buy.controller;

import com.frontlinehomes.save2buy.data.land.data.Duration;
import com.frontlinehomes.save2buy.data.land.data.Land;
import com.frontlinehomes.save2buy.data.land.data.LandPaymentPlan;
import com.frontlinehomes.save2buy.data.land.data.PaymentPlan;
import com.frontlinehomes.save2buy.data.land.request.PaymentPlanDTO;
import com.frontlinehomes.save2buy.data.land.response.PaymentPlanResponseDTO;
import com.frontlinehomes.save2buy.service.land.LandService;
import com.frontlinehomes.save2buy.service.landPaymentPlan.LandPaymentPlanService;
import com.frontlinehomes.save2buy.service.paymentUtil.PaymentUtilService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.mapping.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.awt.*;
import java.io.Console;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

@RestController
@RequestMapping("/paymentPlan")
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
    public ResponseEntity<PaymentPlanResponseDTO> createLandPaymentPlan(@RequestBody PaymentPlanDTO paymentPlanDTO, @PathVariable Long id){
        try{

            if(paymentPlanDTO.getDurationLength()==  null  || paymentPlanDTO.getDurationType()== null ){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "duration field is empty");
            }
            Land land= landService.getLand(id);

            if(land!= null){
                Boolean durationFound= false;
                PaymentPlan paymentPlan = convertPaymentPlanDTOtoPaymentPlan(paymentPlanDTO);
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
                        duration1.setWeight(paymentPlanDTO.getDurationWeight());
                        PaymentPlan paymentPlan1= paymentPlan;
                        durationNew= duration1;
                    }else{
                        durationFound=true;
                        durationNew=duration;
                    }
                PaymentPlan paymentPlanClone= new PaymentPlan();
                BeanUtils.copyProperties(paymentPlan, paymentPlanClone);
                Duration durationClone= new Duration();
                BeanUtils.copyProperties(durationNew, durationClone);


                /**
                 *
                 *  revisit this  part of the code for comparison. payment plans should not occur duplicate on db
                 */
                //check if the payment plan exist
                if(land.getLandPaymentPlans()!= null) {
                    Stream<com.frontlinehomes.save2buy.data.land.data.LandPaymentPlan> landPaymentPlanStream = land.getLandPaymentPlans().stream().filter(landPaymentPlan -> {
                        return (landPaymentPlan.getPaymentPlan().equals(paymentPlanClone)) ? true : false;
                    });

                    if (landPaymentPlanStream.count() > 0) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment Plan already exist");
                    }
                }



                //if it didn't exist persist, but if it existed, then update it
                Duration durationManaged=paymentUtilService.addDuration(durationNew);


                //save the paymentPlan
                PaymentPlan paymentPlanManaged= landPaymentPlanService.savePaymentPlan(paymentPlan);

                durationManaged.addPaymentPlan(paymentPlanManaged);

                //add new land payment plan to land
                LandPaymentPlan landPaymentPlan= new LandPaymentPlan();

                paymentPlanManaged.addLandPaymentPlan(landPaymentPlan);

                land.addLandPaymentPlan(landPaymentPlan);

                //persist the landPayment Plan
                LandPaymentPlan landPaymentPlanManaged= landPaymentPlanService.saveLandPaymentPlan(landPaymentPlan);


                return  new ResponseEntity<PaymentPlanResponseDTO>(convertPaymentPlanToResponseDTO(paymentPlanManaged),HttpStatus.OK);

            }else{
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Land cannot be found");
            }
        }catch (Exception e){
            log.info("LandPaymentPlanController:createLandPaymentPlan:  "+e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Land cannot be found");
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
                  paymentPlans.add(convertPaymentPlanToResponseDTO(landPaymentPlan.getPaymentPlan()));
             });
             return new ResponseEntity<ArrayList<PaymentPlanResponseDTO>>(paymentPlans,HttpStatus.OK);
        }catch (Exception e){
            throw  new ResponseStatusException(HttpStatus.NOT_FOUND, "The requested resource could not be found");
        }

    }



    private PaymentPlan convertPaymentPlanDTOtoPaymentPlan(PaymentPlanDTO paymentPlanDTO){
        PaymentPlan paymentPlan= new PaymentPlan();
        BeanUtils.copyProperties(paymentPlanDTO, paymentPlan);
        //create a new Duration Object
        return paymentPlan;
    }

    private PaymentPlanResponseDTO convertPaymentPlanToResponseDTO(PaymentPlan paymentPlan){
        PaymentPlanResponseDTO planResponseDTO = new PaymentPlanResponseDTO();
        BeanUtils.copyProperties(paymentPlan, planResponseDTO);
        planResponseDTO.setDurationLength(paymentPlan.getDuration().getLength());
        planResponseDTO.setDurationWeight(paymentPlan.getDuration().getWeight());
        planResponseDTO.setDurationType(paymentPlan.getDuration().getFrequency());
        return  planResponseDTO;
    }


}
