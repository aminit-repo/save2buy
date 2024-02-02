package com.frontlinehomes.save2buy.data.users.admin;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
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
    @NaturalId
    @Enumerated(EnumType.STRING)
    private Scopes value;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permission that = (Permission) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
