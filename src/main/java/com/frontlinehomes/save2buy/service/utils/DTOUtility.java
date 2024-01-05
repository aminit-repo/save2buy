package com.frontlinehomes.save2buy.service.utils;

import com.frontlinehomes.save2buy.data.land.data.*;
import com.frontlinehomes.save2buy.data.land.request.*;
import com.frontlinehomes.save2buy.data.land.response.DurationResponseDTO;
import com.frontlinehomes.save2buy.data.land.response.InvestorLandResponseDTO;
import com.frontlinehomes.save2buy.data.land.response.PaymentPlanResponseDTO;
import com.frontlinehomes.save2buy.data.users.Phone;
import com.frontlinehomes.save2buy.data.users.User;
import com.frontlinehomes.save2buy.data.users.investor.data.Investor;
import com.frontlinehomes.save2buy.data.users.investor.request.InvestorDTO;
import com.frontlinehomes.save2buy.data.users.investor.response.InvestorResponseDTO;
import com.frontlinehomes.save2buy.data.users.request.SignUpDTO;
import com.frontlinehomes.save2buy.data.users.request.UserDTO;
import com.frontlinehomes.save2buy.data.users.response.LoginResponseDTO;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Array;
import java.util.*;

public class DTOUtility {


    public static InvestorResponseDTO convertUserToInvestorResponseDTO(User user){
        InvestorResponseDTO investorResponseDTO= new InvestorResponseDTO();
        BeanUtils.copyProperties(user, investorResponseDTO);
        //copy the from Investor object investorDTO
        BeanUtils.copyProperties(user.getInvestor(), investorResponseDTO);
        String line=null;
        //set the primaryline
        if(!user.getPhone().isEmpty()){
            line= user.getPhone().get(0).getPhone();
        }
        investorResponseDTO.setPrimaryLine(line);
        investorResponseDTO.setId(user.getId());
        return investorResponseDTO;
    }



    public static InvestorDTO convertUserToInvestorDTO(User user){
        InvestorDTO investorDTO= new InvestorDTO();
        BeanUtils.copyProperties(user, investorDTO);
        String line=null;
        //check if user phone exist
        if(!user.getPhone().isEmpty()){
            line= user.getPhone().get(0).getPhone();
        }
        investorDTO.setPrimaryLine(line);
        //copy the from Investor object investorDTO
        BeanUtils.copyProperties(user.getInvestor(), investorDTO);
        return investorDTO;
    }

    public static User convertInvestorDTOtoUser(InvestorDTO investorDTO){
        User user= new User();
        Investor investor= new Investor();
        BeanUtils.copyProperties(investorDTO, user);
        BeanUtils.copyProperties(investorDTO, investor);
        //get phone provided
        Phone phone= new Phone();
        if(investorDTO.getPrimaryLine()!=null){
            phone.setPhone(investorDTO.getPrimaryLine());
            user.addPhone(phone);
        }
        user.addInvestor(investor);
        return user;
    }


    public static InvestorDTO convertInvestorToInvestorDTO(Investor investor){
        InvestorDTO investorDTO= new InvestorDTO();
        BeanUtils.copyProperties(investor, investorDTO);
        return investorDTO;
    }


    public static Investor convertInvestorDTOtoInvestor(InvestorDTO investorDTO){
        Investor investor= new Investor();
        BeanUtils.copyProperties(investorDTO, investor);
        return investor;
    }

    public static User convertSignUpDTOtoUser(SignUpDTO signUpDTO){
        User user= new User();
        BeanUtils.copyProperties(signUpDTO, user);
        return user;
    }

    public static SignUpDTO convertUserToSigUpDTO(User user){
        SignUpDTO signUpDTO= new SignUpDTO();
        BeanUtils.copyProperties(user, signUpDTO);
        return signUpDTO;
    }


    /**
     *
     * DTO's for LandController
     */
    public static InvestorLand convertLandPurchaseDTOToInvestorLand(LandPurchaseDTO landPurchaseDTO, Land land, Investor investor, PaymentPlan paymentPlan, LandStatus landStatus){
        //create a new Investor Land
        InvestorLand investorLand= new InvestorLand();

        //create a new InvestorLandPaymentPlan
        InvestorLandPaymentPlan investorLandPaymentPlan= new InvestorLandPaymentPlan();
        investorLand.addInvestorLandPaymentPlan(investorLandPaymentPlan);

        if(paymentPlan != null)
         paymentPlan.addInvestorLandPaymentPlan(investorLandPaymentPlan);

        investorLand.setLandStatus(landStatus);

        //synchronize investorLand with the investor
        investor.addInvestorLands(investorLand);
        land.addInvestorLand(investorLand);

        return  investorLand;
    }

    public static  PaymentPlan  convertLandPurchasePaymentPlanDTOTOPaymentPlan(LandPurchasePaymentPlanDTO purchasePaymentPlanDTO){
        PaymentPlan paymentPlan= new PaymentPlan();
        BeanUtils.copyProperties(purchasePaymentPlanDTO, paymentPlan);
        return paymentPlan;
    }





    public static Land convertAddLandDTOtoLand(AddLandDTO addLandDTO){
        Land land=new Land();
        BeanUtils.copyProperties(addLandDTO, land);
        return  land;
    }

    public static LandDetailsDTO convertLandToLandDetailsDTO(Land land){
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
                p.setDurationLength(landPaymentPlan.getPaymentPlan().getDuration().getLength());

                planResponseDTO.add(p);
            });

        }

        //create calculatorConfigDTO
       ArrayList<DurationDTO> durationDTOList= new ArrayList<>();
       ArrayList<PeriodDTO> periodDTOList= new ArrayList<>();
       CalculatorConfigDTO calculatorConfigDTO= new CalculatorConfigDTO();

        if(land.getCalculatorConfig() != null){
            calculatorConfigDTO.setMaxLandSize(land.getCalculatorConfig().getMaxLandSize());
            calculatorConfigDTO.setMinLandSize(land.getCalculatorConfig().getMinLandSize());
            calculatorConfigDTO.setLandId(land.getId());

            Set<LandCalculatorConfigDuration> configDurationSet= land.getCalculatorConfig().getDurationList();
            configDurationSet.forEach(landCalculatorConfigDuration -> {
                DurationDTO durationDTO= new DurationDTO();
                durationDTO.setFrequency(landCalculatorConfigDuration.getDuration().getFrequency());
                durationDTO.setLength(landCalculatorConfigDuration.getDuration().getLength());
                durationDTOList.add(durationDTO);
            });

            //add the period list

            Set<LandCalculatorConfigPeriod> configPeriodSet= land.getCalculatorConfig().getFrequencies();
            configPeriodSet.forEach(landCalculatorConfigPeriod -> {
                PeriodDTO periodDTO= new PeriodDTO();
                periodDTO.setFrequency(landCalculatorConfigPeriod.getPeriod().getFrequency());
                periodDTOList.add(periodDTO);
            });
            //set durationList
            calculatorConfigDTO.setDurationList(durationDTOList);

            //set periodList
            calculatorConfigDTO.setPeriodList(periodDTOList);

        }
        //set calculatorCong
        landDetailsDTO.setCalculatorConfig(calculatorConfigDTO);

        //create
        landDetailsDTO.setLandPaymentPlans(planResponseDTO);
        return landDetailsDTO;
    }



    public static InvestorLandResponseDTO convertInvestorLandToInvestorLandResponseDTO(InvestorLand investorLand){
        InvestorLandResponseDTO investorLandResponseDTO= new InvestorLandResponseDTO();
        BeanUtils.copyProperties(investorLand, investorLandResponseDTO);
        investorLandResponseDTO.setBillingType(investorLand.getBillingType());
        investorLandResponseDTO.setAmount(investorLand.getAmount());
        investorLandResponseDTO.setLandId(investorLand.getLand().getId());

        PaymentPlan paymentPlan=null;
        List<InvestorLandPaymentPlan> investorLandPaymentPlanList =    investorLand.getInvestorLandPaymentPlan();

        for (InvestorLandPaymentPlan investorLandPaymentPlan : investorLandPaymentPlanList) {
            if(investorLandPaymentPlan.getStatus().equals(PaymentPlanStatus.Active))
                paymentPlan= investorLandPaymentPlan.getPaymentPlan();
        }

        if (paymentPlan!= null){
            investorLandResponseDTO.setPaymentPlanId(paymentPlan.getId());
            investorLandResponseDTO.setCreationDate(investorLand.getCreationDate());
        }

        return  investorLandResponseDTO;
    }


    public static PaymentPlan convertPaymentPlanDTOToPaymentPlan(PaymentPlanDTO paymentPlanDTO){
        PaymentPlan paymentPlan= new PaymentPlan();
        BeanUtils.copyProperties(paymentPlanDTO, paymentPlan);
        return paymentPlan;
    }

    /**
     *
     * DTO for LandPaymentPlanController
     *
     */


    public static PaymentPlan convertPaymentPlanDTOtoPaymentPlan(PaymentPlanDTO paymentPlanDTO){
        PaymentPlan paymentPlan= new PaymentPlan();
        BeanUtils.copyProperties(paymentPlanDTO, paymentPlan);
        //create a new Duration Object
        return paymentPlan;
    }

    public static PaymentPlanResponseDTO convertPaymentPlanToResponseDTO(PaymentPlan paymentPlan){
        PaymentPlanResponseDTO planResponseDTO = new PaymentPlanResponseDTO();
        BeanUtils.copyProperties(paymentPlan, planResponseDTO);
        planResponseDTO.setDurationLength(paymentPlan.getDuration().getLength());
        planResponseDTO.setDurationType(paymentPlan.getDuration().getFrequency());
        return  planResponseDTO;
    }

    /**
     * DTO for  UserController
     */


    public static LoginResponseDTO convertUserTOLoginResponseDTO(User user){
        LoginResponseDTO loginResponseDTO= new LoginResponseDTO();
        BeanUtils.copyProperties(user, loginResponseDTO);
        return loginResponseDTO;
    }


    public static User convertUserDTOtoUser(UserDTO userDTO){
        User user= new User();
        BeanUtils.copyProperties(userDTO, user);
        return user;
    }

    public static UserDTO convertUserToUserDTO(User user){
        UserDTO userDTO= new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }

    public static Duration convertDurationDTOtoDuration(DurationDTO durationDTO){
        Duration duration = new Duration();
        BeanUtils.copyProperties(durationDTO, duration);
        return duration;
    }

    public static DurationResponseDTO convertDurationResponseDTOtoDuration(Duration duration){
        DurationResponseDTO durationResponseDTO= new DurationResponseDTO();
        BeanUtils.copyProperties(duration, durationResponseDTO);
        return durationResponseDTO;
    }


}
