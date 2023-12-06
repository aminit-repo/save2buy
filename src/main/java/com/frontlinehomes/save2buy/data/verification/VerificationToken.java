package com.frontlinehomes.save2buy.data.verification;

import com.frontlinehomes.save2buy.data.users.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

@Setter
@Entity
@Getter
@NoArgsConstructor
public class VerificationToken {
    private static final int EXPIRATION = 60 * 24;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    private Date expiryDate;

    public VerificationToken(String token, User user, Date date) {
        this.token = token;
        this.user = user;
        this.expiryDate= date;
    }



    // standard constructors, getters and setters

}
