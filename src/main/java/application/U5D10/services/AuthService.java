package application.U5D10.services;

import application.U5D10.entities.User;
import application.U5D10.enums.Role;
import application.U5D10.exceptions.BadRequestException;
import application.U5D10.exceptions.UnauthorizedException;
import application.U5D10.payloads.NewUserDTO;
import application.U5D10.payloads.UserLoginDTO;
import application.U5D10.repositories.UsersRepository;
import application.U5D10.security.JWTTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class AuthService {
    @Autowired
    private UsersService usersService;
    @Autowired
    private UsersRepository userRepo;
    @Autowired
    private JWTTools jwtTools;
    @Autowired
    private PasswordEncoder bcrypt;

    public String authenticateUser(UserLoginDTO body){
        User user = usersService.findByEmail(body.email());


        if(bcrypt.matches(body.password(), user.getPassword())){

            return jwtTools.createToken(user);
        }else {

            throw new UnauthorizedException("credenziali non valide!");
        }


    }


    public User save(NewUserDTO body) throws IOException {
        userRepo.findByEmail(body.email()).ifPresent(user -> {
            throw new BadRequestException("L'email " + user.getEmail() + " è già utilizzata!");
        });

        User newUser = new User();

        newUser.setCognome(body.cognome());
        newUser.setNome(body.nome());
        newUser.setSurname(body.surname());
        newUser.setPassword(bcrypt.encode(body.password()));
        newUser.setEmail(body.email());
        newUser.setUserPicture("https://ui-avatars.com/api/?name=" + body.nome().replace(" " , "") + "+" + body.cognome().replace(" " , ""));
        newUser.setRole(Role.USER);

        return userRepo.save(newUser);

    }





}
