package com.frontlinehomes.save2buy.repository;

import com.frontlinehomes.save2buy.data.users.User;
import com.frontlinehomes.save2buy.data.users.investor.data.Investor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvestorRepository extends JpaRepository<Investor, Long> {
   public User findByUser(User user);
}
