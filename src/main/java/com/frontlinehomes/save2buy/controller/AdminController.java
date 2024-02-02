package com.frontlinehomes.save2buy.controller;

import com.frontlinehomes.save2buy.data.ResponseDTO;
import com.frontlinehomes.save2buy.data.ResponseStatus;
import com.frontlinehomes.save2buy.data.users.Phone;
import com.frontlinehomes.save2buy.data.users.User;
import com.frontlinehomes.save2buy.data.users.admin.Admin;
import com.frontlinehomes.save2buy.data.users.admin.AdminAccessLevel;
import com.frontlinehomes.save2buy.data.users.admin.Permission;
import com.frontlinehomes.save2buy.data.users.admin.Scopes;
import com.frontlinehomes.save2buy.data.users.admin.request.CreateAdminDTO;
import com.frontlinehomes.save2buy.data.users.admin.response.AdminDetailsDTO;
import com.frontlinehomes.save2buy.data.users.request.SignUpDTO;
import com.frontlinehomes.save2buy.data.users.response.SignUpResponseDTO;
import com.frontlinehomes.save2buy.events.registration.OnRegistrationCompleteEvent;
import com.frontlinehomes.save2buy.exception.EntityDuplicationException;
import com.frontlinehomes.save2buy.service.AdminService;
import com.frontlinehomes.save2buy.service.CopyUtils;
import com.frontlinehomes.save2buy.service.UserService;
import com.frontlinehomes.save2buy.service.adminAccessLevel.AdminAcccessLevelService;
import com.frontlinehomes.save2buy.service.permission.PermissionService;
import com.frontlinehomes.save2buy.service.utils.DTOUtility;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.*;


/**
 *
 * Custom Errors originated from UserController starts with the code 4XX
 */
@RestController
@RequestMapping("/administrator")
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired

    private PermissionService permissionService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private AdminAcccessLevelService adminAcccessLevelService;

    @Autowired
    ApplicationEventPublisher eventPublisher;
    @PostMapping("/create")
    @CrossOrigin( allowedHeaders = {"Authorization", "Content-Type" })
    public ResponseEntity<ResponseDTO<AdminDetailsDTO>> createAdmin(@RequestBody CreateAdminDTO createAdminDTO){

        //check if email is provided
        if(createAdminDTO.getFirstName() ==null) return new ResponseEntity<ResponseDTO<AdminDetailsDTO>>(new ResponseDTO<AdminDetailsDTO>(ResponseStatus.Error, "first name is required"), HttpStatus.BAD_REQUEST);

        if(createAdminDTO.getLastName() ==null) return new ResponseEntity<ResponseDTO<AdminDetailsDTO>>(new ResponseDTO<AdminDetailsDTO>(ResponseStatus.Error, "last name is required"), HttpStatus.BAD_REQUEST);

        if(createAdminDTO.getEmail() ==null) return new ResponseEntity<ResponseDTO<AdminDetailsDTO>>(new ResponseDTO<AdminDetailsDTO>(ResponseStatus.Error, "email is required"), HttpStatus.BAD_REQUEST);

        //validate the email
        EmailValidator validator = EmailValidator.getInstance();

        if(!(validator.isValid(createAdminDTO.getEmail()))){
            return new ResponseEntity<ResponseDTO<AdminDetailsDTO>>( new ResponseDTO<AdminDetailsDTO>(ResponseStatus.Error,"Enter a valid Email"),HttpStatus.BAD_REQUEST);
        }
        try{

            //create user
            User user= new User();
            BeanUtils.copyProperties(createAdminDTO, user);


            User userManaged= userService.saveUser(user);

            //create admin
            Admin admin= new Admin();
            if(createAdminDTO.getOffice()!= null) admin.setOffice(createAdminDTO.getOffice());

            userManaged.addAdmin(admin);

            //check phone
            if(createAdminDTO.getContact() != null){
                Phone phone= new Phone();
                phone.setPhone(createAdminDTO.getContact());
                userManaged.addPhone(phone);
            }

            //update user
            userManaged= userService.saveUser(userManaged);

            Admin adminManaged= userManaged.getAdmin();

            //create Admin AccessLevels

          Set<AdminAccessLevel> accessLevelList= new HashSet<>();

            //check for accessLevel
            if(createAdminDTO.getAccess() != null){
                //verify if the permission exist.
                for (Scopes scopes : createAdminDTO.getAccess()) {
                    try {

                        Permission permission= permissionService.getPermissionByValue(scopes);
                        //create accessLevel
                        AdminAccessLevel accessLevel= new AdminAccessLevel();
                        permission.addAdminAccessLevel(accessLevel);
                        adminManaged.addAdminAccessLevel(accessLevel);

                        //update the permission
                        permissionService.savePermission(permission);

                    }catch (NoSuchElementException e){
                        //create a new permission

                        Permission permission= new Permission();
                        permission.setValue(scopes);

                        Permission permissionManaged= permissionService.savePermission(permission);
                        AdminAccessLevel accessLevel= new AdminAccessLevel();

                        permissionManaged.addAdminAccessLevel(accessLevel);
                        adminManaged.addAdminAccessLevel(accessLevel);

                        //update the permission
                        permissionService.savePermission(permissionManaged);
                    }

                }
            }



            //update user
            userManaged= userService.saveUser(userManaged);

            ResponseDTO responseDTO= new ResponseDTO(ResponseStatus.Success, "Successful");

            AdminDetailsDTO adminDetailsDTO= DTOUtility.convertAdminToAdminDetailsDTO(userManaged.getAdmin());
            responseDTO.setBody(adminDetailsDTO);
            return new ResponseEntity<ResponseDTO<AdminDetailsDTO>>(responseDTO, HttpStatus.OK);

        }catch (EntityDuplicationException e){
            return new ResponseEntity<ResponseDTO<AdminDetailsDTO>>(new ResponseDTO<AdminDetailsDTO>(ResponseStatus.Error, e.getMessage()), HttpStatus.CONFLICT);

        }

    }

    @CrossOrigin(allowedHeaders = {"Authorization", "Content-Type"}, methods = {RequestMethod.PUT})
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<AdminDetailsDTO>> updateAdmin(@RequestBody CreateAdminDTO createAdminDTO, @PathVariable Long id){

        try{
            //get the user with the id
            User user= userService.getUser(id);

            BeanUtils.copyProperties(createAdminDTO, user, CopyUtils.getNullPropertyNames(createAdminDTO));

            if(createAdminDTO.getOffice() != null) user.getAdmin().setOffice(createAdminDTO.getOffice());

            //update the user
            user= userService.saveUser(user);

            return new ResponseEntity<ResponseDTO<AdminDetailsDTO>>(new ResponseDTO<AdminDetailsDTO>(ResponseStatus.Success, "Successful"), HttpStatus.OK);
        }catch (NoSuchElementException e){
            return new ResponseEntity<ResponseDTO<AdminDetailsDTO>>(new ResponseDTO<AdminDetailsDTO>(ResponseStatus.Error, "user with id "+id+" not found"), HttpStatus.NOT_FOUND);
        }
    }



}
