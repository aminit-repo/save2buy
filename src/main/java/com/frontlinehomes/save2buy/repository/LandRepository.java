package com.frontlinehomes.save2buy.repository;

import com.frontlinehomes.save2buy.data.land.data.Land;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LandRepository extends JpaRepository<Land, Long> {

}
