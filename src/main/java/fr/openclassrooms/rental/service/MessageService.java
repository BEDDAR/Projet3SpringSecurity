package fr.openclassrooms.rental.service;

import fr.openclassrooms.rental.entite.Message;
import fr.openclassrooms.rental.repository.MessageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MessageService {
    MessageRepository messageRepository;
    public void envoyerMessage(Message message){
        Message messageEnvoye=new Message();
        messageEnvoye.setMessage(message.getMessage());
        messageEnvoye.setUser_id(message.getUser_id());
        messageEnvoye.setRental_id(message.getRental_id());
        this.messageRepository.save(messageEnvoye);
    }
}
