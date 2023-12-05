package com.frontlinehomes.save2buy.data.users.request;

import com.frontlinehomes.save2buy.data.users.Gender;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CurrentTimestamp;
import org.hibernate.annotations.NaturalId;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
@Setter
@Getter
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String otherName;
    private String email;
    private List<String> phone;
    private Gender gender;
    private String fontId;
    private String referralCode;
    private String myReferee;

}
