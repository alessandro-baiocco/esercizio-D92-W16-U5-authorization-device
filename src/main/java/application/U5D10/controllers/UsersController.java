package application.U5D10.controllers;


import application.U5D10.entities.Device;
import application.U5D10.entities.User;
import application.U5D10.exceptions.BadRequestException;
import application.U5D10.exceptions.NotUserFoundException;
import application.U5D10.payloads.NewUserDTO;
import application.U5D10.payloads.UsersPutDTO;
import application.U5D10.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UsersController {
    @Autowired
    private UsersService usersService;


    @GetMapping("/{id}")
    public User findById(@PathVariable int id){
        return usersService.findById(id);
    }

    @GetMapping("")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<User> getAllUser(@RequestParam(defaultValue = "0")int page ,
                                 @RequestParam(defaultValue = "10")int size,
                                 @RequestParam(defaultValue = "id")String order){
            return usersService.getAllUser(page , size , order);
    }

    @GetMapping("/{id}/devices")
    public List<Device> getUserDevices(@PathVariable int id){
        return usersService.findUserDevices(id);
    }


    @GetMapping("/me")
    public UserDetails getProfile(@AuthenticationPrincipal UserDetails currentUser){
        return currentUser;
    };

    @PutMapping("/me")
    public UserDetails getProfile(@AuthenticationPrincipal User currentUser, @RequestBody UsersPutDTO body) throws IOException{
        return usersService.findByIdAndUpdate(currentUser.getId(), body);
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void getProfile(@AuthenticationPrincipal User currentUser){
        usersService.findByIdAndDelete(currentUser.getId());
    };


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable int id){
        usersService.findByIdAndDelete(id);
    }



    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public User findByIdAndUpdate(@PathVariable int id, @RequestBody @Validated UsersPutDTO body, BindingResult validation){
        if(validation.hasErrors()){
            throw new BadRequestException(validation.getAllErrors());
        }else {
            try {
                return usersService.findByIdAndUpdate(id, body);
            }catch (IOException ex){
                throw new RuntimeException(ex);
            }

    }}


    @PostMapping("/{id}/upload")
    public User changeUserImg(@PathVariable int id , @RequestParam("avatar") MultipartFile body) throws IOException , NotUserFoundException {
            return usersService.uploadPicture(id, body) ;
    }

}
