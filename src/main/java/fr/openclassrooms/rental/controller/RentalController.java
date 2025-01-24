package fr.openclassrooms.rental.controller;

import fr.openclassrooms.rental.dto.RentalDTO;
import fr.openclassrooms.rental.entite.Rental;
import fr.openclassrooms.rental.entite.Utilisateur;
import fr.openclassrooms.rental.models.RentalsResponse;
import fr.openclassrooms.rental.securite.JwtService;
import fr.openclassrooms.rental.service.RentalService;
import fr.openclassrooms.rental.service.UtilisateurService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rentals")
public class RentalController {

    private final RentalService rentalService;
    private UtilisateurService utilisateurService;
    private JwtService jwtService;

    public RentalController(RentalService rentalService,UtilisateurService utilisateurService,JwtService jwtService) {
        this.rentalService = rentalService;
        this.utilisateurService =utilisateurService;
        this.jwtService=jwtService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Rental> createRental(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("name") String name,
            @RequestParam("surface") String surface,
            @RequestParam("price") String price,
            @RequestParam("description") String description,
            @RequestParam("picture") MultipartFile imageFile) {


        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String token = authHeader.substring(7);
        UserDetails userDetails;
        // Validez le token
        if (!jwtService.isTokenGloballyValid(token) ){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        // Extrait le nom d'utilisateur du token
        String username = jwtService.extractUsername(token);

        // Récupère les informations utilisateur
        Utilisateur utilisateur = utilisateurService.loadUserByUsername(username);

        Rental rental = new Rental();
        rental.setName(name);
        rental.setSurface(surface);
        rental.setPrice(price);
        rental.setDescription(description);
        String baseUrl = "http://localhost:3001/api/images/";
        rental.setPicture(baseUrl + imageFile.getOriginalFilename());

        Rental createdRental = rentalService.createRentalForUser(utilisateur.getId(), rental);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRental);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RentalsResponse> getRentals() {
        List<Rental> rentals = rentalService.getAllRentals();  // Récupère les locations depuis le service
        RentalsResponse rentalsResponse = new RentalsResponse();  // Crée une réponse pour les locations
        List<RentalDTO> rentalsDto = new ArrayList<>();

        if (rentals != null && !rentals.isEmpty()) {
            rentalsDto = rentals.stream()
                    .map(rental -> rentalService.convertToDTO(rental))
                    .collect(Collectors.toList());
        }

        if (!rentalsDto.isEmpty()) {
            rentalsResponse.setRentals(rentalsDto);
            return ResponseEntity.status(HttpStatus.OK).body(rentalsResponse);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{id_rental}")
    public ResponseEntity<RentalDTO> getRentalById(@PathVariable("id_rental") Integer idRental) {
        Rental rental = rentalService.getRentalById(idRental);

        if (rental != null) {
            return ResponseEntity.status(HttpStatus.OK).body(rentalService.convertToDTO(rental));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping(value="/{id}" ,consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Rental> updateRental(
            @PathVariable Integer id,
            @RequestParam("name") String name,
            @RequestParam("surface") String surface,
            @RequestParam("price") String price,
            @RequestParam("description") String description) {

        Rental rental = new Rental();
        rental.setName(name);
        rental.setSurface(surface);
        rental.setPrice(price);
        rental.setDescription(description);

        Rental updatedRental = rentalService.updateRental(id, rental);
        return ResponseEntity.ok(updatedRental);
    }
}