package com.frontlinehomes.save2buy.controller;

import com.frontlinehomes.save2buy.data.land.data.Land;
import com.frontlinehomes.save2buy.data.land.request.AddLandDTO;
import com.frontlinehomes.save2buy.data.land.request.ImageDTO;
import com.frontlinehomes.save2buy.data.land.request.LandDetailsDTO;
import com.frontlinehomes.save2buy.data.land.request.UpdateLandDTO;
import com.frontlinehomes.save2buy.data.users.User;
import com.frontlinehomes.save2buy.data.users.investor.data.Investor;
import com.frontlinehomes.save2buy.data.users.investor.request.InvestorDTO;
import com.frontlinehomes.save2buy.service.CopyUtils;
import com.frontlinehomes.save2buy.service.file.FileSystemStorageService;
import com.frontlinehomes.save2buy.service.land.LandService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;
@CrossOrigin
@RestController
@RequestMapping("/land")
public class LandController {
  @Autowired
  private LandService landService;

  @Autowired
  private FileSystemStorageService fileSystemStorageService;

  private static Logger log = LogManager.getLogger(LandController.class);


  @PostMapping("/create")
  public ResponseEntity<LandDetailsDTO>  createLand(@RequestBody AddLandDTO addLandDTO){

    if(addLandDTO.getSize()!=null && addLandDTO.getTitle()!= null&& addLandDTO.getNeigborhood()!= null && addLandDTO.getPriceInSqm()!=null){
      //persist land
      Land land=  landService.addLand(convertAddLandDTOtoLand(addLandDTO));
      LandDetailsDTO landDetailsDTO= convertLandToLandDetailsDTO(land);
      return ResponseEntity.ok(landDetailsDTO);
    }
    String message="The field  "+ addLandDTO.getSize()== null? "size": addLandDTO.getTitle()==null ? "title": addLandDTO.getPriceInSqm()==null?"priceInSqm": "neigborhood";
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
  }

  @PostMapping("/image/{id}")
  public ResponseEntity<ImageDTO> uploadLandImage(@RequestParam("file") MultipartFile file, @PathVariable Long id){
   //get the specified land
    try{
      Land land= landService.getLand(id);
    }catch (NoSuchElementException e){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    log.info("LandController:uploadImage:  land found with id "+id);

    try{
      if(file.isEmpty()){
         throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File cannot be empty");
      }



       String url=fileSystemStorageService.store(file, "land"+id+".jpg", "investor");
      log.info("FileSystemStorageService:store: image uploaded successfully");
       return ResponseEntity.ok(new ImageDTO(url));
    }catch (Exception e){
      log.warn("LandController:uploadImage:  error uploading image "+e.getMessage());
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);

    }
  }

  @PatchMapping("/{id}")
  public ResponseEntity<LandDetailsDTO> updateLand(@PathVariable Long id, @RequestBody UpdateLandDTO updateLandDTO) {
    try{
      Land land= landService.getLand(id);
      BeanUtils.copyProperties(updateLandDTO, land, CopyUtils.getNullPropertyNames(updateLandDTO));
      landService.addLand(land);
      return new ResponseEntity<LandDetailsDTO>(convertLandToLandDetailsDTO(land), HttpStatus.OK);
    }catch (NoSuchElementException e){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Land with id "+id+" not found");
    }

  }


  private Land convertAddLandDTOtoLand(AddLandDTO addLandDTO){
    Land land=new Land();
    BeanUtils.copyProperties(addLandDTO, land);
    return  land;
  }

  private LandDetailsDTO convertLandToLandDetailsDTO(Land land){
    LandDetailsDTO landDetailsDTO= new LandDetailsDTO();
    BeanUtils.copyProperties(land, landDetailsDTO);
    return landDetailsDTO;
  }


}
