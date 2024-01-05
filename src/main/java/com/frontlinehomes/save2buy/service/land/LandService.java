package com.frontlinehomes.save2buy.service.land;

import com.frontlinehomes.save2buy.data.land.data.Land;
import com.frontlinehomes.save2buy.data.users.User;
import com.frontlinehomes.save2buy.repository.LandRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;

import javax.swing.text.html.Option;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class LandService {
    @Autowired
   private LandRepository landRepository;
    public Land addLand(Land land){
        return  landRepository.save(land);
    }

    public Land getLand(Long id) throws NoSuchElementException{

        try{
            Optional<Land> land= landRepository.findById(id);
            if(land.get()== null) throw new NoSuchElementException("Land with id "+id+"Cannot be found");
            return land.get();
        }catch (NoSuchElementException e){
            throw e;
        }
    }

    public void deleteLand(Land land){
         landRepository.delete(land);
    }

    public List<Land> getAllLand(){
        List<Land> lands= landRepository.findAll();
        return lands;
    }


}
