package fr.openclassrooms.rental.service;

import fr.openclassrooms.rental.dto.UtilisateurDTO;
import fr.openclassrooms.rental.entite.Role;
import fr.openclassrooms.rental.entite.Utilisateur;
import fr.openclassrooms.rental.enumer.TypeDeRole;
import fr.openclassrooms.rental.repository.UtilisateurRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import fr.openclassrooms.rental.exception.EmailAlreadyUsedException;
import fr.openclassrooms.rental.exception.InvalidEmailException;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service de gestion des utilisateurs.
 * Fournit des fonctionnalités liées à l'inscription et à l'authentification des utilisateurs.
 */

@AllArgsConstructor
@Service
public class UtilisateurService implements UserDetailsService {

    private UtilisateurRepository utilisateurRepository;
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * Inscrit un nouvel utilisateur en validant ses données et en encodant son mot de passe.
     *
     * @param utilisateur l'objet utilisateur contenant les informations d'inscription.
     * @return un DTO représentant l'utilisateur inscrit.
     * @throws InvalidEmailException si l'email est invalide.
     * @throws EmailAlreadyUsedException si l'email est déjà utilisé.
     */
    public UtilisateurDTO inscription(Utilisateur utilisateur) {

        // Vérifie que l'email est valide.
        if (!isValidEmail(utilisateur.getEmail())) {
            throw new InvalidEmailException("Votre email est invalide.");
        }

        // Vérifie si l'email est déjà utilisé.
        Optional<Utilisateur> utilisateurOptional = this.utilisateurRepository.findByEmail(utilisateur.getEmail());
        if (utilisateurOptional.isPresent()) {
            throw new EmailAlreadyUsedException("Votre email est déjà utilisé.");
        }

        // Encode le mot de passe avant de le sauvegarder.
        String mdpCrypte = this.passwordEncoder.encode(utilisateur.getPassword());
        utilisateur.setPassword(mdpCrypte);

        // Définit le rôle par défaut.
        Role roleUtilisateur = new Role();
        roleUtilisateur.setLibelle(TypeDeRole.UTILISATEUR);
        utilisateur.setRole(roleUtilisateur);

        // Définit les dates.
        utilisateur.setCreated_at(LocalDateTime.now());
        utilisateur.setUpdated_at(LocalDateTime.now());

        // Sauvegarde l'utilisateur dans la base de données.
        Utilisateur savedUtilisateur = this.utilisateurRepository.save(utilisateur);

        // Retourne un DTO.
        return new UtilisateurDTO(
                savedUtilisateur.getId(),
                savedUtilisateur.getName(),
                savedUtilisateur.getEmail(),
                savedUtilisateur.getCreated_at(),
                savedUtilisateur.getUpdated_at()
        );
    }

    /**
     * Valide l'email en vérifiant qu'il contient "@" et ".".
     *
     * @param email l'email à valider.
     * @return true si l'email est valide, sinon false.
     */
    private boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }

    /**
     * Charge un utilisateur par son email (nom d'utilisateur) pour l'authentification.
     * Utilisé par Spring Security lors du processus de login.
     *
     * @param username l'email de l'utilisateur.
     * @return l'utilisateur correspondant à l'email fourni.
     * @throws UsernameNotFoundException si aucun utilisateur n'est trouvé pour cet email.
     */
    @Override
    public Utilisateur loadUserByUsername(String username) throws UsernameNotFoundException {
        Utilisateur utilisateur = this.utilisateurRepository
                .findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Aucun utilisateur ne correspond à cet identifiant"));

        return utilisateur;
    }
}
