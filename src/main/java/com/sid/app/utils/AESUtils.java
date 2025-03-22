package com.sid.app.utils;

import com.sid.app.config.AESProperties;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * @author Siddhant Patni
 */
@Component
public class AESUtils {

    @Autowired
    private AESProperties aesProperties;

    private SecretKeySpec keySpec;

    @PostConstruct
    private void init() {
        keySpec = new SecretKeySpec(aesProperties.getSecretKey().getBytes(), aesProperties.getAlgorithm());
    }

    // Encrypt password using AES
    public String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance(aesProperties.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // Decrypt password using AES
    public String decrypt(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(aesProperties.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedBytes);
    }

}