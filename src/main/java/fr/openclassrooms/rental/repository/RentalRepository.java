package fr.openclassrooms.rental.repository;

import fr.openclassrooms.rental.entite.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Integer> {
    Optional<Rental> findById(Integer integer);
}
