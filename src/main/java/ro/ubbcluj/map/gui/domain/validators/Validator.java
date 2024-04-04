package ro.ubbcluj.map.gui.domain.validators;

@FunctionalInterface
public interface Validator<T> {
    void validate(T entity) throws ValidationException, FriendException,MessageException;
}