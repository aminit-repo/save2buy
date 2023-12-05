package com.frontlinehomes.save2buy.data.users.admin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.frontlinehomes.save2buy.data.users.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
@Entity
@Setter
@Getter
@NoArgsConstructor
public class Admin  {
    @Id
    @GeneratedValue
    private Long id;

    @JsonIgnore
    @OneToOne
    private User user;

    private String office;
}
