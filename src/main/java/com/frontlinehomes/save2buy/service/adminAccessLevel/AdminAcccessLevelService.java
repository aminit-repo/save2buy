package com.frontlinehomes.save2buy.service.adminAccessLevel;

import com.frontlinehomes.save2buy.data.users.admin.AdminAccessLevel;
import com.frontlinehomes.save2buy.data.users.admin.Permission;
import com.frontlinehomes.save2buy.repository.AdminAccessLevelRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@Transactional
public class AdminAcccessLevelService {
    @Autowired
    private AdminAccessLevelRepository adminAccessLevelRepository;

    public AdminAccessLevel getAccessLevelByPermission(Permission permission){
        try{
            AdminAccessLevel accessLevel= adminAccessLevelRepository.findByPermission(permission);
            if(accessLevel ==  null) throw  new NoSuchElementException("accessLevel cannot be found with permission");
            return accessLevel;
        }catch (Exception e){
            throw  new NoSuchElementException("accessLevel cannot be found with permission");
        }
    }

    public AdminAccessLevel saveAccessLevel(AdminAccessLevel adminAccessLevel){
        return adminAccessLevelRepository.save(adminAccessLevel);
    }
}
