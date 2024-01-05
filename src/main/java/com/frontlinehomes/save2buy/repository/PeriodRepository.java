package com.frontlinehomes.save2buy.repository;

import com.frontlinehomes.save2buy.data.land.data.Period;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PeriodRepository extends JpaRepository<Period, Long> {
}
