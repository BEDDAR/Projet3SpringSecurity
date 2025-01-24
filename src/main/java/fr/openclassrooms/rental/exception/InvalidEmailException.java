package fr.openclassrooms.rental.exception;

/**
 * Exception levée lorsque l'email fourni est invalide.
 */
public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException(String message) {
        super(message);
    }
}
