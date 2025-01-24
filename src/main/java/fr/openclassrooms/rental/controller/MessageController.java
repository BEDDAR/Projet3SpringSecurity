package fr.openclassrooms.rental.controller;

import fr.openclassrooms.rental.entite.Message;
import fr.openclassrooms.rental.models.MessageResponse;
import fr.openclassrooms.rental.service.MessageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(path = "/messages")
public class MessageController {
    MessageService messageService;
    private static final Logger log = LoggerFactory.getLogger(MessageController.class);

    @PostMapping
    public ResponseEntity<MessageResponse> createMessage(@RequestBody Message message) {
        try {
            log.info("Envoie de message");
            this.messageService.envoyerMessage(message);
            System.out.println(message);
            MessageResponse messageResponse = new MessageResponse();
            messageResponse.setMessage("Message send with success");
            return ResponseEntity.status(HttpStatus.CREATED).body(messageResponse);
        } catch (Exception e) {
            MessageResponse messageResponse = new MessageResponse();
            messageResponse.setMessage("Erreur d'envoie de message");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageResponse);
        }
    }
}
