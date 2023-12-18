package com.frontlinehomes.save2buy.service.paymentUtil;

import com.frontlinehomes.save2buy.data.land.data.Duration;
import com.frontlinehomes.save2buy.data.land.data.DurationType;
import com.frontlinehomes.save2buy.repository.DurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentUtilService {
    @Autowired
    private DurationRepository durationRepository;
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
}
