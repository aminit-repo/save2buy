package com.frontlinehomes.save2buy.data.users.admin;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Permission implements Serializable {
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    @Id
    private Long id;

    private String name;

    private String value;
    @OneToMany(mappedBy = "permission", cascade = {CascadeType.ALL})
    private Set<AdminAccessLevel> adminAccessLevel=new HashSet<>();

    public void addAdminAccessLevel(AdminAccessLevel adminAccessLevel){
        adminAccessLevel.setPermission(this);
        this.adminAccessLevel.add(adminAccessLevel);
    }

    public void removeAdminAccessLevel(AdminAccessLevel adminAccessLevel){
        this.adminAccessLevel.remove(adminAccessLevel);
        adminAccessLevel.setPermission(null);
    }
}
