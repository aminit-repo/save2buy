package com.frontlinehomes.save2buy.data.users;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@NoArgsConstructor
@Setter
@Getter
@RequiredArgsConstructor
public class Phone {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne
    private User user;

    @NonNull
    private String Phone;

}
