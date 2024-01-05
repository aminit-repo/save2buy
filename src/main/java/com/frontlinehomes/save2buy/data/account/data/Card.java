package com.frontlinehomes.save2buy.data.account.data;

import com.frontlinehomes.save2buy.data.account.data.Account;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Setter
@Getter
public class Card {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String number;

    @OneToOne
    private Account account;

}
