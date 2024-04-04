package ro.ubbcluj.map.gui.domain.validators;

import ro.ubbcluj.map.gui.domain.Friendship;

public class FriendshipValidator implements Validator<Friendship> {

    @Override
    public void validate(Friendship entity) throws FriendException {
        String exceptionString = "";
        if(entity.getID1()==entity.getID2())
            exceptionString+="Friendship between the same person is not allowed!";
        if(!exceptionString.isEmpty())
            throw new FriendException(exceptionString);
    }
}
