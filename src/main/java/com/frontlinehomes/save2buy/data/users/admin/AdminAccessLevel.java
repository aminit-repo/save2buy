package com.frontlinehomes.save2buy.data.users.admin;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Setter
@Getter
public class AdminAccessLevel implements Serializable {
    @Id
    @GeneratedValue
    @Setter(lombok.AccessLevel.NONE)
    private Long id;
    @ManyToOne
    private Permission permission;
    @ManyToOne
    private Admin admin;


}
