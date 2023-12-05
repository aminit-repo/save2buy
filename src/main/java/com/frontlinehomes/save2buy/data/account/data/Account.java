package com.frontlinehomes.save2buy.data.account.data;

import com.frontlinehomes.save2buy.data.users.investor.data.Investor;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Account  implements Serializable {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private Long id;
    @Column(precision = 2)
    private Double balance;

    private String name;

    private String number;

    private String bank;
    @OneToOne
    private Investor investor;
    @OneToOne(mappedBy = "account", orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @Setter(AccessLevel.NONE)
    private Card card;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, mappedBy = "account")
    private List<Transaction> transactionList;

    public void addCard(Card card){
        card.setAccount(this);
        this.card= card;
    }

    public void removeCard(Card card){
        this.card=null;
        card.setAccount(null);
    }

    public  void addTransactions(Transaction transaction){
        transaction.setAccount(this);
        this.transactionList.add(transaction);
    }

    public  void removeTransactions(Transaction transaction){
        this.transactionList.remove(transaction);
        transaction.setAccount(null);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return number.equals(account.number) && bank.equals(account.bank);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, bank);
    }


}
