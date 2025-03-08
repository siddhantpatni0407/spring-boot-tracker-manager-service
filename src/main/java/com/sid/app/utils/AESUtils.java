package com.sid.app.utils;

import com.sid.app.constants.AppConstants;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * @author Siddhant Patni
 */
public class AESUtils {

    // Convert Base64 key to SecretKeySpec
    private static SecretKeySpec getSecretKey() {
        byte[] decodedKey = Base64.getDecoder().decode(AppConstants.SECRET_KEY); // ✅ Decode Base64 properly
        return new SecretKeySpec(decodedKey, AppConstants.AES_ALGORITHM);
    }

    // Encrypt password using AES
    public static String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance(AppConstants.AES_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // Decrypt password using AES
    public static String decrypt(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(AppConstants.AES_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey());
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedBytes);
    }

}