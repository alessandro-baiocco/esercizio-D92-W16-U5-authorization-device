package application.U5D10.services;

import application.U5D10.entities.Device;
import application.U5D10.entities.User;
import application.U5D10.exceptions.BadRequestException;
import application.U5D10.exceptions.NotUserFoundException;
import application.U5D10.payloads.NewUserDTO;
import application.U5D10.payloads.UsersPutDTO;
import application.U5D10.repositories.UsersRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class UsersService {
    @Autowired
    private UsersRepository usersRepo;
    @Autowired
    private Cloudinary cloudinary;

    public User findById(int id) throws NotUserFoundException{
        return usersRepo.findById(id).orElseThrow(() -> new NotUserFoundException(id));
    }

    public Page<User> getAllUser(int page , int size , String order){
        Pageable pageable = PageRequest.of(page, size , Sort.by(order));
        return usersRepo.findAll(pageable);
    }




    public List<Device> findUserDevices(int id) throws NotUserFoundException{
        User found = usersRepo.findById(id).orElseThrow(() -> new NotUserFoundException(id));
        return found.getDevices();
    }

    public User findByEmail(String email){
        return usersRepo.findByEmail(email)
                .orElseThrow(() -> new NotUserFoundException("Utente con email " + email + " non trovato!"));
    }



    public void findByIdAndDelete(int id) throws NotUserFoundException{
        User found = findById(id);
        usersRepo.delete(found);
    }


    public User findByIdAndUpdate(int id , UsersPutDTO body) throws IOException{
        User found = findById(id);
        found.setNome(body.nome() != null ? body.nome() : found.getNome());
        found.setCognome(body.cognome()  != null ? body.cognome() : found.getCognome());
        found.setEmail(body.email()  != null ? body.email() : found.getEmail());
        found.setSurname(body.surname() != null ? body.surname() : found.getSurname());
        return usersRepo.save(found);
    }


    public User uploadPicture(int id , MultipartFile file) throws IOException, BadRequestException {
        try {
            User found = findById(id);
            String newImage = (String) cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap()).get("url");
            found.setUserPicture(newImage);
            return usersRepo.save(found);
        }catch (NotUserFoundException ex){
            throw new NotUserFoundException(id);
        } catch (RuntimeException ex){
            throw new BadRequestException("file vuoto o invalido");
        }

    }




}
