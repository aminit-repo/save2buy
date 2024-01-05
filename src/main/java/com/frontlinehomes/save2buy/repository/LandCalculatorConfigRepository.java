package com.frontlinehomes.save2buy.repository;

import com.frontlinehomes.save2buy.data.land.data.LandCalculatorConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LandCalculatorConfigRepository extends JpaRepository<LandCalculatorConfig, Long> {

}
