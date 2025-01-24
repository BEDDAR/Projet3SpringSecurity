package fr.openclassrooms.rental.securite;

import fr.openclassrooms.rental.controller.UtilisateurController;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;  // Service qui charge les détails de l'utilisateur (base de données ou autre)
    private static final Logger log = LoggerFactory.getLogger(UtilisateurController.class);


    public JwtFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    // Méthode principale de filtrage des requêtes HTTP
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Récupération du chemin de la requête
        String requestPath = request.getServletPath();

        // Exclure certaines routes publiques de la vérification JWT
        if (requestPath.equals("/auth/register") || requestPath.equals("/auth/login") || requestPath.equals("/auth/me") ||
                requestPath.startsWith("/swagger-ui") || requestPath.startsWith("/v3/api-docs") ||
                requestPath.equals("/swagger-ui.html")) {
            filterChain.doFilter(request, response);  // Si la requête est pour une route publique, on continue sans appliquer le filtre JWT
            return;  // Sortir de la méthode pour ne pas appliquer le filtre JWT
        }

        // Récupération de l'en-tête "Authorization" de la requête HTTP
        final String authHeader = request.getHeader("Authorization");
        String jwt = null;
        String username = null;

        // Si l'en-tête contient un token JWT sous la forme "Bearer <token>"
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);  // Extrait le token en supprimant le préfixe "Bearer "

            // Extraction du nom d'utilisateur à partir du token JWT
            username = jwtService.extractUsername(jwt);
        }

        // Si le token est trouvé et que l'utilisateur n'est pas encore authentifié
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Charger les détails de l'utilisateur via le UserDetailsService
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Si le token JWT est valide pour cet utilisateur
            if (jwtService.isTokenValidForUser(jwt, userDetails)) {
                // Créer un objet d'authentification basé sur les informations de l'utilisateur
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));  // Ajouter des détails de la requête (e.g., adresse IP)

                // Définir l'authentification dans le contexte de sécurité
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        // Continuer à la chaîne de filtres (en appliquant les filtres suivants dans la pipeline)
        filterChain.doFilter(request, response);
    }
}