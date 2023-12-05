package com.frontlinehomes.save2buy.repository;

import com.frontlinehomes.save2buy.data.users.User;
import com.frontlinehomes.save2buy.data.verification.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {


   public VerificationToken findByToken(String token);

   public VerificationToken findByUser(User user);

}
