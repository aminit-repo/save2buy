package com.frontlinehomes.save2buy.service;

import com.frontlinehomes.save2buy.data.users.admin.Admin;
import com.frontlinehomes.save2buy.repository.AdminRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AdminService {
    @Autowired
    private AdminRepository adminRepository;

    public Admin saveAdmin(Admin admin){
        return adminRepository.save(admin);
    }
}
