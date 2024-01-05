package com.frontlinehomes.save2buy.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
public class Monnify {
    @Id
    @GeneratedValue
    private Long id;

    private LocalDateTime expireTime;
}
