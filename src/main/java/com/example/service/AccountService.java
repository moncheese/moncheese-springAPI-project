package com.example.service;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.entity.Account;
import com.example.repository.AccountRepository;

@Service
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public ResponseEntity<Account> registerAccount(Account account) {
        if (isInvalidRegistration(account)) {
            return ResponseEntity.badRequest().body(null);
        }
        Optional<Account> existingAccount = accountRepository.findByUsername(account.getUsername());
        if (existingAccount.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        return ResponseEntity.ok(accountRepository.save(account));
    }

    public ResponseEntity<Account> loginAccount(Account account) {
        Optional<Account> existingAccount = accountRepository.findByUsernameAndPassword(account.getUsername(), account.getPassword());
        return existingAccount.map(acc -> ResponseEntity.ok(acc)).orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null));
    }

    private boolean isInvalidRegistration(Account account) {
        return account.getUsername().isEmpty() || account.getPassword().length() < 4;
    }
}
