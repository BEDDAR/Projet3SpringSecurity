package fr.openclassrooms.rental.securite;

import fr.openclassrooms.rental.controller.UtilisateurController;
import fr.openclassrooms.rental.dto.UtilisateurDTO;
import fr.openclassrooms.rental.entite.Utilisateur;
import fr.openclassrooms.rental.service.UtilisateurService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@AllArgsConstructor
@Service
public class JwtService {
    // Clé secrète utilisée pour signer et vérifier les tokens JWT
    private final String ENCRIPTION_KEY = "608f36e92dc66d97d5933f0e6371493cb4fc05b1aa8f8de64014732472303a7c";
    private UtilisateurService utilisateurService;  // Service pour charger les utilisateurs
    private static final Logger log = LoggerFactory.getLogger(UtilisateurController.class);  // Logger pour afficher des messages d'information

    /**
     * Génère un token JWT pour un utilisateur donné (identifié par son username).
     * @param username Le nom d'utilisateur (souvent l'email) pour lequel générer un JWT.
     * @return Une carte contenant le token sous forme d'une chaîne.
     */
    public Map<String, String> generate(String username) {
        // Charge les informations utilisateur depuis le service
        Utilisateur utilisateur = this.utilisateurService.loadUserByUsername(username);
        return this.generateJwt(utilisateur);  // Génère un token pour cet utilisateur
    }

    /**
     * Extrait le nom d'utilisateur (subject) du token JWT.
     * @param token Le token JWT.
     * @return Le nom d'utilisateur (email) contenu dans le token.
     */
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(this.getKey())  // Utilise la clé secrète pour parser le token
                .build()
                .parseClaimsJws(token)  // Parse et valide le token
                .getBody()
                .getSubject();  // Extrait le sujet (username/email)
    }

    /**
     * Vérifie si un token est expiré.
     * @param token Le token JWT.
     * @return True si le token est expiré, sinon False.
     */
    public boolean isTokenExpired(String token) {
        Date expirationDate = getExpirationDateFromToken(token);  // Récupère la date d'expiration
        return expirationDate.before(new Date());  // Compare avec la date actuelle
    }

    /**
     * Récupère la date d'expiration à partir du token JWT.
     * @param token Le token JWT.
     * @return La date d'expiration.
     */
    private Date getExpirationDateFromToken(String token) {
        return this.getClaim(token, Claims::getExpiration);  // Extrait la réclamation "Expiration"
    }

    /**
     * Méthode générique pour extraire un "claim" du token.
     * @param token Le token JWT.
     * @param function La fonction pour extraire un claim spécifique.
     * @return La valeur du claim extrait.
     */
    private <T> T getClaim(String token, Function<Claims, T> function) {
        Claims claims = getAllClaims(token);  // Récupère toutes les réclamations (claims) du token
        return function.apply(claims);  // Applique la fonction sur les claims
    }

    /**
     * Récupère toutes les "claims" contenues dans le token JWT.
     * @param token Le token JWT.
     * @return Les claims sous forme d'un objet Claims.
     */
    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(this.getKey())  // Utilise la clé secrète pour parser
                .build()
                .parseClaimsJws(token)
                .getBody();  // Renvoie les claims
    }

    /**
     * Génère un token JWT contenant les informations de l'utilisateur.
     * @param utilisateur L'utilisateur pour lequel générer un token.
     * @return Une carte contenant le token sous forme d'une chaîne.
     */
    private Map<String, String> generateJwt(Utilisateur utilisateur) {
        final long currentTime = System.currentTimeMillis();  // Temps actuel en millisecondes
        final long expirationTime = currentTime + 30 * 60 * 1000;  // Expiration dans 30 minutes

        // Création des claims
        final Map<String, Object> claims = Map.of(
                "name", utilisateur.getName(),  // Nom de l'utilisateur
                "email", utilisateur.getEmail(),  // Email de l'utilisateur
                Claims.EXPIRATION, new Date(expirationTime),  // Date d'expiration
                Claims.SUBJECT, utilisateur.getEmail()  // Sujet (identifiant principal)
        );

        // Construction du token JWT
        final String bearer = Jwts.builder()
                .setIssuedAt(new Date(currentTime))  // Date de création
                .setExpiration(new Date(expirationTime))  // Date d'expiration
                .setSubject(utilisateur.getEmail())  // Sujet principal
                .setClaims(claims)  // Ajout des claims
                .signWith(getKey(), SignatureAlgorithm.HS256)  // Signature avec l'algorithme HS256
                .compact();

        return Map.of("token", bearer);  // Retourne le token dans une carte
    }

    /**
     * Récupère la clé de signature à partir de la clé secrète codée en base64.
     * @return Une instance de Key.
     */
    private Key getKey() {
        final byte[] decoder = Decoders.BASE64.decode(ENCRIPTION_KEY);  // Décodage de la clé base64
        return Keys.hmacShaKeyFor(decoder);  // Génère une clé HMAC-SHA
    }

    /**
     * Vérifie si un token est valide pour un utilisateur donné.
     * @param token Le token JWT.
     * @param userDetails Les détails de l'utilisateur.
     * @return True si le token est valide, sinon False.
     */
    public boolean isTokenValidForUser(String token, UserDetails userDetails) {
        final String username = extractUsername(token);  // Extrait le username du token
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));  // Vérifie le username et l'expiration
    }

    /**
     * Vérifie globalement si un token est valide, sans se soucier de l'utilisateur.
     * @param token Le token JWT.
     * @return True si le token est valide, sinon False.
     */
    public boolean isTokenGloballyValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getKey())  // Vérifie le token avec la clé
                    .build()
                    .parseClaimsJws(token);  // Valide et parse le token
            return true;  // Token valide
        } catch (JwtException | IllegalArgumentException e) {
            // En cas d'erreur de validation
            System.out.println("Invalid JWT: " + e.getMessage());
            return false;  // Token invalide
        }
    }
}