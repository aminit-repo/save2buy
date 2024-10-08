package com.frontlinehomes.save2buy.data.account.data;

import com.frontlinehomes.save2buy.data.land.data.InvestorLand;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CurrentTimestamp;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

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
    private Timestamp createdTime;

    private Double amount;

    /** Transaction reference is generated by save2buy.ng **/
    private String refNumber;

    /**
     * Channel describes  the integrated payment provider
     */
    private Channel channel;

    /**
     * transactionId is generated by the payment service provider
     */
    private String transactionId;

    private String paymentMethod;


    @Enumerated(EnumType.STRING)
    private BillingType billingType;

    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return createdTime.equals(that.createdTime) && amount.equals(that.amount) && refNumber.equals(that.refNumber) && Objects.equals(transactionId, that.transactionId) && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(createdTime, amount, refNumber, transactionId, type);
    }


}
