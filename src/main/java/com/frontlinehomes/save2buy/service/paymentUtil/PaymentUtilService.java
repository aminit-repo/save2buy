package com.frontlinehomes.save2buy.service.paymentUtil;

import com.frontlinehomes.save2buy.controller.LandController;
import com.frontlinehomes.save2buy.data.land.data.*;
import com.frontlinehomes.save2buy.exception.CalculatorConfigException;
import com.frontlinehomes.save2buy.exception.NotNullFieldException;
import com.frontlinehomes.save2buy.repository.DurationRepository;
import com.frontlinehomes.save2buy.repository.InvestorLandPaymentPlanRepository;
import com.frontlinehomes.save2buy.repository.LandCalculatorConfigRepository;
import com.frontlinehomes.save2buy.repository.PeriodRepository;
import com.frontlinehomes.save2buy.service.EncryptionService;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.security.InvalidParameterException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Calendar;
import java.util.Optional;
import java.util.*;


@Service
@Transactional
public class PaymentUtilService {
    @Autowired
    private DurationRepository durationRepository;
    @Autowired
    private EncryptionService encryptionService;
    @Autowired
    private PeriodRepository periodRepository;
    @Autowired
    private LandCalculatorConfigRepository landCalculatorConfigRepository;
    private static Logger log = LogManager.getLogger(PaymentUtilService.class);


    public Duration getDurationById(Long id){
        Optional<Duration> duration= durationRepository.findById(id);
        return duration.get();
    }

    public Duration getByLengthAndType(Integer length, DurationType frequency){
        Duration duration= durationRepository.findByLengthAndFrequency(length,frequency);
        return duration;
    }

    public Duration addDuration(Duration duration){
        Duration duration1= durationRepository.save(duration);
        return duration1;
    }

    public List<Duration> getAllDuration(){
        return durationRepository.findAll();
    }
    public Period addPeriod(Period period){
        return periodRepository.save(period);
    }

    public List<Period> getAllPeriod(){
        return periodRepository.findAll();
    }

    public Integer getDurationWeight(Duration duration){

        if(duration.getFrequency().equals(DurationType.Week)){
            return  duration.getLength() * 7;
        }

        if(duration.getFrequency().equals(DurationType.Day)){
            return duration.getLength();
        }

        if(duration.getFrequency().equals(DurationType.Month)){
            return duration.getLength() * 31;
        }
        throw new InvalidParameterException("Invalid parameter passed");
    }


    public Integer getPeriodWeight(Period period){
        if(period.getFrequency().equals(Frequency.Daily)) return 1;

        if(period.getFrequency().equals(Frequency.Weekly)) return 7;

        if(period.getFrequency().equals(Frequency.OneOff)) return 0;

        if(period.getFrequency().equals(Frequency.Monthly)) return 31;

        throw new InvalidParameterException("Invalid parameter passed");
    }






    public Double calculatePaymentCharge(Double purchaseSize,Frequency frequency,  Integer durationLength, DurationType durationType, Land land) {

        if (purchaseSize == null || frequency == null)
            throw new NotNullFieldException("size and frequency is required");

        if (land == null) throw new CalculatorConfigException("land is required");

        if (purchaseSize == 0) throw new InvalidParameterException("purchaseSize cannot be zero");

        //calculate the oneoff payment
        Double oneOffAmount = land.getPriceInSqm() * purchaseSize;

        if (frequency.equals(Frequency.OneOff)) {
            return oneOffAmount;
        } else {



            //duration mus not be empty
            if (durationLength == null || durationType == null)
                throw new CalculatorConfigException("duration cannot be empty when frequency is  not specified as OneOff");

            //find the specified duration in the land calculator config
            Set<LandCalculatorConfigDuration> calculatorConfigDuration = land.getCalculatorConfig().getDurationList();
            LandCalculatorConfigDuration duration = null;



            for (LandCalculatorConfigDuration landCalculatorConfigDuration : calculatorConfigDuration) {

                if (landCalculatorConfigDuration.getDuration().getFrequency().equals(durationType) && landCalculatorConfigDuration.getDuration().getLength().equals(durationLength)) {
                    duration = landCalculatorConfigDuration;
                }
            }

            // get the period configuration
            Set<LandCalculatorConfigPeriod> calculatorConfigPeriods = land.getCalculatorConfig().getFrequencies();

            Period period = null;


            for (LandCalculatorConfigPeriod calculatorConfigPeriod : calculatorConfigPeriods) {
                if (calculatorConfigPeriod.getPeriod().getFrequency().equals(frequency)) {
                    period = calculatorConfigPeriod.getPeriod();
                }
            }


            if (period == null) throw new CalculatorConfigException("The specified frequency can't be found in the land Calculator configuration");

            if (duration == null)
                throw new CalculatorConfigException("the specified duration length and type can't be found in the land's calculator configuration");

            Double occurance = getDurationWeight(duration.getDuration()).doubleValue() / getPeriodWeight(period);

            Double finalPrice = oneOffAmount / occurance;

            return finalPrice;
        }
    }


    public LandCalculatorConfig addCalculatorConfig(LandCalculatorConfig landCalculatorConfig){
        return landCalculatorConfigRepository.save(landCalculatorConfig);
    }

    public String getRefNumber () {
            SecureRandom random = new SecureRandom();
            byte[] bytes = new byte[10];
            random.nextBytes(bytes);
            String key = Base64.getEncoder().encodeToString(bytes);
            //get the current Data
            Calendar c = Calendar.getInstance();
            return "flh" + key;

        }

}
