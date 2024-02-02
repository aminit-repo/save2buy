package com.frontlinehomes.save2buy.service.permission;

import com.frontlinehomes.save2buy.data.users.admin.Permission;
import com.frontlinehomes.save2buy.data.users.admin.Scopes;
import com.frontlinehomes.save2buy.exception.EntityDuplicationException;
import com.frontlinehomes.save2buy.repository.PermissionRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.NoSuchElementException;

@Service
@Transactional
public class PermissionService {
    @Autowired
    private PermissionRepository permissionRepository;
    public Permission savePermission(Permission permission){

        try{
            return permissionRepository.save(permission);
        }catch (Exception e){
            throw  new EntityDuplicationException("permission already exist");
        }
    }

    public Permission getPermissionByValue(Scopes value){
        try{
            Permission permission=permissionRepository.findByValue(value);
            if(permission== null) throw new NoSuchElementException("permission with value ="+ value+" not found");
            return permission;
        }catch (Exception e){
            throw new NoSuchElementException("permission with value ="+ value+" not found");
        }
    }

    public List<Permission> getAllPermission(){
        return permissionRepository.findAll();
    }


}
