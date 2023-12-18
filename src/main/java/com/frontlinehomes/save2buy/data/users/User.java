package com.frontlinehomes.save2buy.data.users;

import com.frontlinehomes.save2buy.data.users.admin.Admin;
import com.frontlinehomes.save2buy.data.users.investor.data.Investor;
import com.frontlinehomes.save2buy.data.verification.VerificationToken;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CurrentTimestamp;
import org.hibernate.annotations.NaturalId;
import org.hibernate.type.YesNoConverter;

import java.sql.Timestamp;
import java.util.List;

@Entity(name = "profile")
@NoArgsConstructor
@Setter
@Getter
public class User{
    @Id
    @GeneratedValue
    private Long id;
    private String firstName;
    private String lastName;
    private String otherName;
    @Column(nullable = false)
    private String password;
    @NaturalId
    @Column(nullable = false)
    private String email;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
    @Setter(AccessLevel.NONE)
    private List<Phone> phone;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
    @Setter(AccessLevel.NONE)
    private Investor investor;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
    @Setter(AccessLevel.NONE)
    private Admin admin;
    private String fontId;
    private String referralCode;
    private String myReferee;
    @Convert(converter = YesNoConverter.class)
    private Boolean enabled=false;
    @CurrentTimestamp
    private Timestamp creationDate;

    public void addAdmin(Admin admin){
        admin.setUser(this);
        this.admin=admin;
    }

    public void removeAdmin(Admin admin){
        admin.setUser(null);
        this.admin=null;
    }


    public void addInvestor(Investor investor){
        investor.setUser(this);
        this.investor= investor;
    }
     public void removeInvestor(Investor investor){
        investor.setUser(null);
        this.investor= null;
    }

    public void addPhone(Phone phone){
        phone.setUser(this);
        this.phone.add(phone);
    }

    public void removePhone(Phone phone){
        phone.setUser(null);
        this.phone= null;
    }

}
