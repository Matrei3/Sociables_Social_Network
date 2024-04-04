package ro.ubbcluj.map.gui.domain.validators;

import ro.ubbcluj.map.gui.domain.User;

public class UserValidator implements Validator<User> {
    @Override
    public void validate(User entity) throws UserException {
        String exceptionString = "";
        if(entity.getId()<0)
            exceptionString+="Id must be positive!\n";
        if(entity.getFirstName().isEmpty())
            exceptionString+="First name must have a length!\n";
        if(entity.getLastName().isEmpty())
            exceptionString+="Last name must have a length!";
        if(entity.getUsername().isEmpty())
            exceptionString+="Username must have a length!\n";
        if(entity.getPassword().length==0)
            exceptionString+="Password must have a length!\n";
        if(!exceptionString.isEmpty())
            throw new UserException("Problem id: " + entity.getId() + "\n" + exceptionString);



    }
}

