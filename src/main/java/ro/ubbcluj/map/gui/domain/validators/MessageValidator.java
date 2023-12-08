package ro.ubbcluj.map.gui.domain.validators;

import ro.ubbcluj.map.gui.domain.Message;

public class MessageValidator implements Validator<Message> {
    @Override
    public void validate(Message entity) throws ValidationException,MessageException {
        if(entity.getText().isEmpty())
            throw new MessageException("Length must not be 0");
    }
}
