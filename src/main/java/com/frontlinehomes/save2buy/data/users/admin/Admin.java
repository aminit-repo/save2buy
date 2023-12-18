package com.frontlinehomes.save2buy.data.users.admin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.frontlinehomes.save2buy.data.users.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Admin implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    @JsonIgnore
    @OneToOne
    private User user;

    private String office;
    @OneToMany(mappedBy = "admin", cascade = {CascadeType.ALL})
    private Set<AdminAccessLevel> adminAccessLevel=new HashSet<>();

    public void addAdminAccessLevel(AdminAccessLevel adminAccessLevel){
        adminAccessLevel.setAdmin(this);
        this.adminAccessLevel.add(adminAccessLevel);
    }

    public  void removeAdminAccessLevel(AdminAccessLevel adminAccessLevel){
        this.adminAccessLevel.remove(adminAccessLevel);
        adminAccessLevel.setAdmin(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Admin admin = (Admin) o;
        return user.equals(admin.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }
}
