package com.frontlinehomes.save2buy.controller;

import com.frontlinehomes.save2buy.data.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/administrator")
public class AdminController {

    @PostMapping("/create")
    public ResponseEntity<ResponseDTO> createAdmin(){
        ResponseDTO responseDTO= new ResponseDTO();

        return new ResponseEntity<ResponseDTO>(responseDTO, HttpStatus.OK);
    }
}
