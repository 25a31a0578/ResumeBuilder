package model;

/** Our own checked exception: thrown when the user's input is wrong. */
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}
