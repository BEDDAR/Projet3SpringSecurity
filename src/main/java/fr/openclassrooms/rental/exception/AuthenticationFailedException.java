package fr.openclassrooms.rental.exception;

// Exception pour les erreurs d'authentification
public class AuthenticationFailedException extends RuntimeException {
    public AuthenticationFailedException(String message) {
        super(message);
    }
}
