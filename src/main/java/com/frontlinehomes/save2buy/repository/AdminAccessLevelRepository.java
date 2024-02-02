package com.frontlinehomes.save2buy.repository;

import com.frontlinehomes.save2buy.data.users.admin.AdminAccessLevel;
import com.frontlinehomes.save2buy.data.users.admin.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminAccessLevelRepository  extends JpaRepository<AdminAccessLevel, Long> {

    public AdminAccessLevel findByPermission(Permission permission);
}
