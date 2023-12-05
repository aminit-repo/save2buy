package com.frontlinehomes.save2buy.service.land;

import com.frontlinehomes.save2buy.data.land.data.Land;
import com.frontlinehomes.save2buy.data.users.User;
import com.frontlinehomes.save2buy.repository.LandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class LandService {
    @Autowired
   private LandRepository landRepository;
    public Land addLand(Land land){
        return  landRepository.save(land);
    }

    public Land getLand(Long id){
        try{
            Optional<Land> land= landRepository.findById(id);
            return  land.get();
        }catch (NoSuchElementException e){
            throw e;
        }
    }

    public void deleteLand(Land land){
         landRepository.delete(land);
    }


}
