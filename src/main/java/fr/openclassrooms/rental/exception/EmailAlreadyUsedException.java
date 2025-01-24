package fr.openclassrooms.rental.exception;

/**
 * Exception levée lorsque l'email fourni est déjà utilisé par un autre utilisateur.
 */
public class EmailAlreadyUsedException extends RuntimeException {
    public EmailAlreadyUsedException(String message) {
        super(message);
    }
}
