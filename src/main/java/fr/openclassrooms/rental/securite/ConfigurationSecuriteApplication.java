package fr.openclassrooms.rental.securite;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

// Classe de configuration de la sécurité de l'application
@Configuration
@EnableWebSecurity
public class ConfigurationSecuriteApplication {

    // Instance pour encoder les mots de passe avec l'algorithme BCrypt
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // Filtre JWT pour valider les tokens dans les requêtes entrantes
    private final JwtFilter jwtFilter;

    public ConfigurationSecuriteApplication(final BCryptPasswordEncoder bCryptPasswordEncoder, JwtFilter jwtFilter) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtFilter = jwtFilter;
    }

    // Configuration de la chaîne de filtres de sécurité
    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity httpSecurity) throws Exception {
        return
                httpSecurity
                        // Désactive la protection CSRF (utile pour les APIs REST, mais attention en production)
                        .csrf(AbstractHttpConfigurer::disable)

                        // Configuration des autorisations pour les requêtes HTTP
                        .authorizeHttpRequests(
                                authorize ->
                                        authorize
                                                // Permet les requêtes POST pour l'enregistrement et la connexion sans authentification
                                                .requestMatchers(POST, "/auth/register").permitAll()
                                                .requestMatchers(POST, "/auth/login").permitAll()
                                                .requestMatchers(GET, "/auth/me").permitAll()
                                                .requestMatchers(POST, "/rentals/**").permitAll()
                                                .requestMatchers(POST, "/messages").permitAll()
                                                .requestMatchers(GET, "/images/**").permitAll()

                                                // Permet les requêtes GET pour les locations et la documentation Swagger sans authentification
                                                .requestMatchers(GET, "/rentals/**").permitAll()
                                                .requestMatchers(GET, "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

                                                // Toute autre requête doit être authentifiée
                                                .anyRequest().authenticated()
                        )

                        // Configure la gestion des sessions pour fonctionner en mode sans état (stateless)
                        .sessionManagement(httpSecuritySessionManagementConfigurer ->
                                httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        )

                        // Ajoute un filtre personnalisé JWT avant le filtre d'authentification par mot de passe/nom d'utilisateur
                        .addFilterBefore(this.jwtFilter, UsernamePasswordAuthenticationFilter.class)

                        // Construit et retourne la chaîne de filtres de sécurité
                        .build();
    }

    // Bean pour configurer l'AuthenticationManager, utilisé pour gérer l'authentification
    @Bean
    public AuthenticationManager authenticationManager(final AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}