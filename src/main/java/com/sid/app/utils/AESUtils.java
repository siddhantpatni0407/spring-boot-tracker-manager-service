package com.sid.app.utils;

import com.sid.app.constants.AppConstants;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * @author Siddhant Patni
 */
public class AESUtils {

    // Encrypt password using AES
    public static String encrypt(String data) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(AppConstants.SECRET_KEY.getBytes(), AppConstants.AES_ALGORITHM);
        Cipher cipher = Cipher.getInstance(AppConstants.AES_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // Decrypt password using AES
    public static String decrypt(String encryptedData) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(AppConstants.SECRET_KEY.getBytes(), AppConstants.AES_ALGORITHM);
        Cipher cipher = Cipher.getInstance(AppConstants.AES_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedBytes);
    }

}