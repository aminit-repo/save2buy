package com.frontlinehomes.save2buy.repository;

import com.frontlinehomes.save2buy.data.land.data.Duration;
import com.frontlinehomes.save2buy.data.land.data.DurationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DurationRepository extends JpaRepository<Duration, Long> {
    public Duration findByLengthAndFrequency(Integer length, DurationType frequency);
}
