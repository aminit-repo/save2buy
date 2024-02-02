package com.frontlinehomes.save2buy.repository;

import com.frontlinehomes.save2buy.data.users.admin.Permission;
import com.frontlinehomes.save2buy.data.users.admin.Scopes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    public Permission findByValue(Scopes value);
}
