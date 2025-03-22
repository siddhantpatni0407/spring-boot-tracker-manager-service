package com.sid.app.service;

import com.sid.app.config.AESProperties;
import com.sid.app.entity.EncryptionKey;
import com.sid.app.repository.EncryptionKeyRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EncryptionKeyService {

    @Autowired
    private EncryptionKeyRepository repository;

    @Autowired
    private AESProperties aesProperties;

    @Getter
    private EncryptionKey latestKey;

    @PostConstruct
    public void init() {
        latestKey = repository.findTopByOrderByKeyVersionDesc();

        if (latestKey == null || !latestKey.getSecretKey().equals(aesProperties.getSecretKey())) {
            // If no key exists OR key has changed, insert a new record
            EncryptionKey newKey = new EncryptionKey();
            newKey.setKeyVersion(latestKey == null ? 1 : latestKey.getKeyVersion() + 1);
            newKey.setSecretKey(aesProperties.getSecretKey());

            latestKey = repository.save(newKey);
        }
    }

}