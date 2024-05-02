package com.example.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.repository.AccountRepository;
import com.example.repository.MessageRepository;

@Service
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository, AccountRepository accountRepository) {
        this.messageRepository = messageRepository;
        this.accountRepository = accountRepository;
    }

    public ResponseEntity<Message> createMessage(Message message) {
        if (isInvalidMessage(message)) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(messageRepository.save(message));
    }

    public ResponseEntity<List<Message>> getAllMessages() {
        return ResponseEntity.ok(messageRepository.findAll());
    }

    public ResponseEntity<Message> getMessageById(int id) {
        Optional<Message> searchedMessage = messageRepository.findById(id);
        if (searchedMessage.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(searchedMessage.get());
        }
        return null;
    }

    public ResponseEntity<Integer> deleteMessageById(int id) {
        if (!messageRepository.existsById(id)) {
            return ResponseEntity.ok(null);
        }
        messageRepository.deleteById(id);
        return ResponseEntity.ok(1);
    }

    public ResponseEntity<Integer> updateMessageById(int messageId, String messageText) {
        Optional<Message> searchedMessage = messageRepository.findById(messageId);
        if (searchedMessage.isEmpty() || isInvalidMessageText(messageText)) {
            return ResponseEntity.badRequest().body(null);
        }
        Message message = searchedMessage.get();
        message.setMessageText(messageText);
        messageRepository.save(message);
        return ResponseEntity.ok(1);
    }

    public ResponseEntity<List<Message>> getAllMessagesByAccountId(int accountId) {
        return ResponseEntity.ok(messageRepository.findAllMessagesByPostedBy(accountId));
    }

    private boolean isInvalidMessage(Message message) {
        return message.getMessageText().isEmpty() || message.getMessageText().length() >= 255
                || accountRepository.findById(message.getPostedBy()).isEmpty();
    }

    private boolean isInvalidMessageText(String messageText) {
        return messageText.isEmpty() || messageText.length() > 255;
    }
}
