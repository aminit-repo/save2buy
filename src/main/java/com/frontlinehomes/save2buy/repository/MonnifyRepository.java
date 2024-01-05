package com.frontlinehomes.save2buy.repository;

import com.frontlinehomes.save2buy.data.Monnify;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
@Repository
public interface MonnifyRepository extends JpaRepository<Monnify, Long> {

}
