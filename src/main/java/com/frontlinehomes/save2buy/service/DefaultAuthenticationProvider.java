package com.frontlinehomes.save2buy.service;

import com.frontlinehomes.save2buy.config.GrantedAuthoritiesConfig;
import com.frontlinehomes.save2buy.data.users.User;
import com.frontlinehomes.save2buy.data.users.admin.AdminAccessLevel;
import com.frontlinehomes.save2buy.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.*;

@Service
public class DefaultAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GrantedAuthoritiesConfig userAccessScopeConfig;


    private static Logger log = LogManager.getLogger(DefaultAuthenticationProvider.class);
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        //get users credentials
        String username= authentication.getName();
        String password= authentication.getCredentials().toString();

        System.out.print(authentication.getName());
        //get the users credentials
        User user = userRepository.findByEmail(username);
        if(user!= null){
            String[] splited ;
            if(user.getAdmin()==null){
                //this is an Investor
                splited = userAccessScopeConfig.getUser().split(" ");
            }else{
                //this is an Admin

                //verify if user's email is verified
                if(user.getEnabled() == false) throw  new DisabledException("email verification is required");

                //get all admins scopes from db.
               Set<AdminAccessLevel> adminAccessLevels= user.getAdmin().getAdminAccessLevel();

                ArrayList<String> scopeList= new ArrayList();
                for (AdminAccessLevel adminAccessLevel : adminAccessLevels) {
                    scopeList.add(adminAccessLevel.getPermission().getValue().toString());
                }

                scopeList.addAll(Arrays.stream(userAccessScopeConfig.getAdmin().split(" ")).toList());
                splited= new String[scopeList.size()];
                scopeList.toArray(splited);
            }

            List<String> scopes= Arrays.asList(splited);
            if(HarshService.isEqual(password,user.getPassword())){
                return new UsernamePasswordAuthenticationToken(username, password,getAuthorities(scopes));
            }else{
                log.error("DefaultAuthenticationProvider:authenticate failure authenticating  Credentails on user "+username);
                throw new BadCredentialsException("incorrect password");
            }
        }
        log.error("DefaultAuthenticationProvider:authenticate failure authenticating username with "+username);
        throw new UsernameNotFoundException("User does not exist");
    }


    @Override
    public boolean supports(Class<?> authentication){
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    private Collection<? extends GrantedAuthority> getAuthorities(List scopes) {
        List<GrantedAuthority> authorities
                = new ArrayList<>();
        authorities= AuthorityUtils.createAuthorityList(scopes);
        return authorities;
    }
}
