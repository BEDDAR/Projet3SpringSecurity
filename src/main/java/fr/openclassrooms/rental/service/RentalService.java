package fr.openclassrooms.rental.service;

import fr.openclassrooms.rental.dto.RentalDTO;
import fr.openclassrooms.rental.entite.Rental;
import fr.openclassrooms.rental.entite.Utilisateur;
import fr.openclassrooms.rental.repository.RentalRepository;
import fr.openclassrooms.rental.repository.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Service
public class RentalService {

    private RentalRepository rentalRepository;
    private UtilisateurRepository utilisateurRepository;

    public Rental createRentalForUser(Integer idUtilisateur, Rental rental) {

        Utilisateur utilisateur = utilisateurRepository.findById(idUtilisateur)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));
        rental.setUtilisateur(utilisateur);
        rental.setCreated_at(LocalDateTime.now());
        rental.setUpdated_at(LocalDateTime.now());
        return rentalRepository.save(rental);
    }

    public List<Rental> getAllRentals (){
        return rentalRepository.findAll();
    }

    public RentalDTO convertToDTO(Rental rental) {
        RentalDTO rentalDTO = new RentalDTO();
        rentalDTO.setId(rental.getId());
        rentalDTO.setName(rental.getName());
        rentalDTO.setSurface(rental.getSurface());
        rentalDTO.setPrice(rental.getPrice());
        rentalDTO.setPicture(rental.getPicture());
        rentalDTO.setDescription(rental.getDescription());
        rentalDTO.setOwner_id(rental.getUtilisateur().getId());
        rentalDTO.setCreated_at(rental.getCreated_at());
        rentalDTO.setUpdated_at(rental.getUpdated_at());

        return rentalDTO;
    }

    public Rental getRentalById(Integer id) {
        return rentalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rental not found with id: " + id));
    }


    public Rental updateRental(Integer id, Rental updateRental) {
        // Recherche de l'entité existante
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rental not found with id: " + id));

        // Mise à jour des champs
        if (updateRental.getName() != null) {
            rental.setName(updateRental.getName());
        }
        if (updateRental.getSurface() != null) {
            rental.setSurface(updateRental.getSurface());
        }
        if (updateRental.getPrice() != null) {
            rental.setPrice(updateRental.getPrice());
        }
        if (updateRental.getPicture() != null) {
            rental.setPicture(updateRental.getPicture());
        }
        if (updateRental.getDescription() != null) {
            rental.setDescription(updateRental.getDescription());
        }

        // Sauvegarde de l'entité mise à jour
        return rentalRepository.save(rental);
    }
}
