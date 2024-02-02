package com.frontlinehomes.save2buy.controller;

import com.frontlinehomes.save2buy.data.ResponseDTO;
import com.frontlinehomes.save2buy.data.ResponseStatus;
import com.frontlinehomes.save2buy.data.users.User;
import com.frontlinehomes.save2buy.data.users.admin.Admin;
import com.frontlinehomes.save2buy.data.users.admin.AdminAccessLevel;
import com.frontlinehomes.save2buy.data.users.admin.Permission;
import com.frontlinehomes.save2buy.data.users.admin.Scopes;
import com.frontlinehomes.save2buy.data.users.request.SignUpDTO;
import com.frontlinehomes.save2buy.data.users.response.SignUpResponseDTO;
import com.frontlinehomes.save2buy.events.registration.OnRegistrationCompleteEvent;
import com.frontlinehomes.save2buy.exception.EntityDuplicationException;
import com.frontlinehomes.save2buy.service.AdminService;
import com.frontlinehomes.save2buy.service.HarshService;
import com.frontlinehomes.save2buy.service.UserService;
import com.frontlinehomes.save2buy.service.adminAccessLevel.AdminAcccessLevelService;
import com.frontlinehomes.save2buy.service.permission.PermissionService;
import com.frontlinehomes.save2buy.service.utils.DTOUtility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.NoSuchElementException;

/**
 *
 * Custom Errors originated from InitController starts with the code 8XX
 */
@Controller

public class InitController {
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
    private static Logger log = LogManager.getLogger(AccountController.class);

    @Value("${save2buy.adminUrl}")
    private String adminUrl;

    @Value("${save2buy.initKey}")
    private String initKey;

    @GetMapping("/init")
    public String getInit(@ModelAttribute("user") SignUpDTO user, Model model){
        //check if admin has been initialized.

        try{
            //verify if there is no user with a create administrator role.
            Permission permission= permissionService.getPermissionByValue(Scopes.create_admin);

            AdminAccessLevel accessLevel=   adminAcccessLevelService.getAccessLevelByPermission(permission);
            if(accessLevel.getAdmin()!= null  && accessLevel.getAdmin().getUser().getEnabled()){
                return "redirect:"+adminUrl;
            }
        }catch (NoSuchElementException e){

        }

        return "signup";
    }

    @PostMapping("/init")
    public String createRootAdmin(@ModelAttribute("user") SignUpDTO signUpDTO, Model model){

        String errorMessage=null;
        String successMessage= null;

        if(signUpDTO.getPassword() == null || signUpDTO.getConfirmPassword() == null){
            errorMessage= "Password and confirm password is required";
            model.addAttribute("successMessage",successMessage);
            model.addAttribute("errorMessage",errorMessage);
            model.addAttribute("loginLink", adminUrl);
            return "signup";
        }

        if(!initKey.equals(signUpDTO.getCode())){
            errorMessage= "invalid code";
            model.addAttribute("successMessage",successMessage);
            model.addAttribute("errorMessage",errorMessage);
            model.addAttribute("loginLink", adminUrl);
            return "signup";
        }


        if( ! signUpDTO.getPassword().equals( signUpDTO.getConfirmPassword())){
           errorMessage="Password does not match";
            model.addAttribute("successMessage",successMessage);
            model.addAttribute("errorMessage",errorMessage);
            model.addAttribute("loginLink", adminUrl);
           return "signup";
        }

        if(signUpDTO.getEmail() == null){
          errorMessage= "email is required";
            model.addAttribute("successMessage",successMessage);
            model.addAttribute("errorMessage",errorMessage);
            model.addAttribute("loginLink", adminUrl);
            return "signup";
        }

        if(signUpDTO.getFirstName() == null){
            errorMessage="first name is required";
            model.addAttribute("successMessage",successMessage);
            model.addAttribute("errorMessage",errorMessage);
            model.addAttribute("loginLink", adminUrl);
            return "signup";
        }

        if(signUpDTO.getLastName() == null){
            errorMessage= "last name is required";
            model.addAttribute("successMessage",successMessage);
            model.addAttribute("errorMessage",errorMessage);
            model.addAttribute("loginLink", adminUrl);
            return "signup";
        }

        try {
            //verify if there is no user with a create administrator role.
            Permission permission= permissionService.getPermissionByValue(Scopes.create_admin);

            AdminAccessLevel accessLevel=   adminAcccessLevelService.getAccessLevelByPermission(permission);

            if(accessLevel.getAdmin()!=null) {
                if (accessLevel.getAdmin().getUser().getEnabled() == true) {
                    //redirect user to administratorLogin
                    errorMessage="root admin already exist";
                    model.addAttribute("successMessage",successMessage);
                    model.addAttribute("errorMessage",errorMessage);
                    model.addAttribute("loginLink", adminUrl);
                    return "signup";

                }
            }

        }catch (NoSuchElementException e){
        }

        try{
            createRootAdminHelper(signUpDTO);
            successMessage="You successfully signed up! an email was sent to "+signUpDTO.getEmail()+" click the link to activate your account";
        }catch (EntityDuplicationException e){
            errorMessage= "user with email "+signUpDTO.getEmail()+" already exist";
        }

        model.addAttribute("successMessage",successMessage);
        model.addAttribute("errorMessage",errorMessage);
        model.addAttribute("loginLink", adminUrl);
        return "signup";
    }

    private SignUpResponseDTO createRootAdminHelper(SignUpDTO signUpDTO){

        //create a new User.
        User user = DTOUtility.convertSignUpDTOtoUser(signUpDTO);

        Admin admin= new Admin();

        user.addAdmin(admin);

        //update the user
        user.setPassword(HarshService.getSecuredPassword(signUpDTO.getPassword()));
        user = userService.saveUser(user);

        //create all accessLevels for the root admin

        //get all permission
        List<Permission> permissionList= permissionService.getAllPermission();


        for (Scopes value : Scopes.values()) {
            Permission permission = new Permission();
            permission.setValue(value);
            if(permissionList.contains(permission)){
                //create a new AdminAccessLevel
                AdminAccessLevel accessLevel= new AdminAccessLevel();

                AdminAccessLevel  accessLevelManaged= adminAcccessLevelService.saveAccessLevel(accessLevel);
                int index= permissionList.indexOf(permission);
                permissionList.get(index).addAdminAccessLevel(accessLevelManaged);

                admin.addAdminAccessLevel(accessLevelManaged);

            }else{
                //create a new permission with the specified scope
                Permission permissionManaged= permissionService.savePermission(permission);
                AdminAccessLevel accessLevel= new AdminAccessLevel();
                AdminAccessLevel  accessLevelManaged= adminAcccessLevelService.saveAccessLevel(accessLevel);
                permissionManaged.addAdminAccessLevel(accessLevelManaged);

                admin.addAdminAccessLevel(accessLevelManaged);

                //update the permission
                permissionManaged=  permissionService.savePermission(permission);
                permissionList.add(permissionManaged);
            }
        }

        User userManaged= userService.saveUser(user);

        //public event
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(userManaged, "https:save2buy.ng/page-confirm-mail/", "admin-verification"));

        SignUpResponseDTO signUpResponseDTO= DTOUtility.convertUserToSignUpResponseDTO(user);

        return signUpResponseDTO;

    }
}
