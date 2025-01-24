package fr.openclassrooms.rental.controller;

import fr.openclassrooms.rental.dto.AuthentificationDTO;
import fr.openclassrooms.rental.dto.UtilisateurDTO;
import fr.openclassrooms.rental.entite.Utilisateur;
import fr.openclassrooms.rental.exception.AuthenticationFailedException;
import fr.openclassrooms.rental.exception.EmailAlreadyUsedException;
import fr.openclassrooms.rental.exception.InvalidEmailException;
import fr.openclassrooms.rental.securite.JwtService;
import fr.openclassrooms.rental.service.UtilisateurService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// Contrôleur REST pour gérer les utilisateurs, leur inscription, connexion, et les informations utilisateur.
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(path = "/auth")
public class UtilisateurController {
    private UtilisateurService utilisateurService;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;
    private static final Logger log = LoggerFactory.getLogger(UtilisateurController.class);

    // Inscription d'un nouvel utilisateur.
    @PostMapping(path = "register")
    public ResponseEntity<UtilisateurDTO> inscrireUtilisateur(@RequestBody Utilisateur utilisateur) {
        UtilisateurDTO utilisateurDTO = utilisateurService.inscription(utilisateur);
        return ResponseEntity.status(HttpStatus.CREATED).body(utilisateurDTO);
    }

    // Gestion locale des exceptions

    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<String> handleInvalidEmailException(InvalidEmailException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ResponseEntity<String> handleEmailAlreadyUsedException(EmailAlreadyUsedException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> handleUsernameNotFoundException(UsernameNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    // Connexion d'un utilisateur en utilisant ses identifiants.
    @PostMapping(path = "login")
    public Map<String, String> connexion(@RequestBody AuthentificationDTO authentificationDTO) {

        try {
            // Authentifie l'utilisateur
            final Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authentificationDTO.email(), authentificationDTO.password())
            );

            log.info("Résultat {}", authenticate.isAuthenticated());
            if (authenticate.isAuthenticated()) {
                // Génère et retourne un JWT en cas de succès
                return this.jwtService.generate(authentificationDTO.email());
            }
        } catch (Exception ex) {
            // En cas d'échec, lève une exception explicite
            throw new AuthenticationFailedException("Échec de l'authentification : identifiants invalides.");
        }
        return null;
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<String> handleAuthenticationFailed(AuthenticationFailedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMe(@RequestHeader("Authorization") String authHeader) {

        // Vérifie si le header d'autorisation contient un token valide.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token non fourni ou invalide");
        }

        // Extrait le token à partir du header.
        String token = authHeader.substring(7);

        // Valide le token avec le service JWT.
        if (!jwtService.isTokenGloballyValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token invalide");
        }

        // Extrait le nom d'utilisateur (email) à partir du token.
        String username = jwtService.extractUsername(token);

        // Charge les informations de l'utilisateur à partir du service utilisateur.
        Utilisateur utilisateur = utilisateurService.loadUserByUsername(username);

        // Retourne les informations utilisateur.
        return ResponseEntity.ok(utilisateur);
    }
}
