package com.frontlinehomes.save2buy.service;

import com.frontlinehomes.save2buy.data.users.User;
import com.frontlinehomes.save2buy.exception.EntityDuplicationException;
import com.frontlinehomes.save2buy.exception.NotNullFieldException;
import com.frontlinehomes.save2buy.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User saveUser(User user) throws NotNullFieldException {
        //check for required fields
        if(user.getEmail() == null){
            throw new NotNullFieldException("Email field cannot be empty");
        }

        if(user.getPassword()==null){
            throw new NotNullFieldException("Password field cannot be empty");
        }
        try{
            return  (User) userRepository.save(user);
        }catch (Exception e){
            throw  new EntityDuplicationException("User entity already exist");
        }
    }

    public User getUser(Long id){
        try{
            Optional<User> user= userRepository.findById(id);
            return  user.get();
        }catch (NoSuchElementException e){
            throw e;
        }

    }


    public List<User> getAllUser(){
        return userRepository.findAll();
    }

    public User getUserByEmail(String email){
        try{
            return  userRepository.findByEmail(email);
        }catch (NoSuchElementException e){
             throw e;
        }
    }

}
