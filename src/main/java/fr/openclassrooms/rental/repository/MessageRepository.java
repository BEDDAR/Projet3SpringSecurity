package fr.openclassrooms.rental.repository;

import fr.openclassrooms.rental.entite.Message;
import org.springframework.data.repository.CrudRepository;

public interface MessageRepository extends CrudRepository<Message, Integer> {

}
