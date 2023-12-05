package com.frontlinehomes.save2buy.data.account.data;

import com.frontlinehomes.save2buy.data.land.data.InvestorLand;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CurrentTimestamp;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@NoArgsConstructor
@Setter
@Getter
public class Transaction implements Serializable {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private Long id;
    @ManyToOne
    private InvestorLand investorLand;
    @ManyToOne
    private Account account;

    @CurrentTimestamp
    private Timestamp createdDate;

    private Double amount;

    private String refNumber;


}
