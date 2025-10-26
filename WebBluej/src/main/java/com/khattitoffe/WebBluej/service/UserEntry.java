package com.khattitoffe.WebBluej.service;
import com.khattitoffe.WebBluej.entity.UserData;
import com.khattitoffe.WebBluej.entity.UserLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.khattitoffe.WebBluej.repository.DynamoDbRepo;
@Service
public class UserEntry {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DynamoDbRepo dynamoDB;

    public boolean saveUser(UserData user) {

        String password = user.getPassword();
        String bcryptPassword=passwordEncoder.encode(password);
        user.setPassword(bcryptPassword);

        try {
            dynamoDB.addUser(user);
            return true;
        } catch (Exception e) {
            return false;
        }

    }
    /*
    public boolean userExists(User user)
    {
        if(createUserRepo.existsByemail((user.getEmail())))
            return true;
        return false;
    }
        
    */
    // email vvalidation.. takees email and then verify if it is valid or not using javax.mail lib
    public boolean verifyEmail(UserData user){
        try {
            InternetAddress eAddress = new InternetAddress(user.getEmail());
            eAddress.validate();// vverifies email if invalid throws addressexception

            if(dynamoDB.userExists(user.getEmail()))
                // if email is already in DB throws exception
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Email already exists");

            return true; // returns true jab username is not existing in db and too
        }
        catch(AddressException e){
            return false;
        }
    }

    public boolean userExists(UserLogin user)
    {
        System.out.println("emial"+user.getEmail());
        if(dynamoDB.userExists(user.getEmail()))
        {
            UserData userDB= dynamoDB.getUserByEmail(user.getEmail());// not completed yet

            String password = user.getPassword();
            //System.out.println("user pass"+password);
            //String bcryptPassword=passwordEncoder.encode(password);
            //System.out.println("user pass"+bcryptPassword);

            boolean matches=passwordEncoder.matches(password, userDB.getPassword());

            if(matches) {
                System.out.println("login success");
                return true;
            }
        }
        return false;
    }


}
