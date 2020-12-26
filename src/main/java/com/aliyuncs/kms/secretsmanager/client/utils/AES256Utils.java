package com.aliyuncs.kms.secretsmanager.client.utils;

import com.aliyuncs.kms.secretsmanager.client.exception.CacheSecretException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.HashMap;
import java.util.Map;

public class AES256Utils {
    public final static String AES_256_CBC_MODE_KEY = "001";
    public final static String PBKDF2WithHmacSHA256 = "PBKDF2WithHmacSHA256";
    private final static Map<String, String> modeMap = new HashMap<String, String>() {
        {
            put(AES_256_CBC_MODE_KEY, "AES/CBC/PKCS5PADDING");
        }
    };

    private AES256Utils() {
        // do nothing
    }

    public static byte[] encrypt(String mode, byte[] data, byte[] secret, byte[] iv, String salt) throws CacheSecretException {
        try {
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance(modeMap.get(mode));
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKeySpec(secret, salt), ivSpec);
            return cipher.doFinal(data);
        } catch (Exception e) {
            CommonLogger.getCommonLogger(CacheClientConstant.MODE_NAME).errorf("action:AES256Encrypt", e);
            throw new CacheSecretException("AES256 encrypt fail", e);
        }
    }

    public static String decrypt(String mode, byte[] data, byte[] secret, byte[] iv, String salt) throws CacheSecretException {
        try {
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance(modeMap.get(mode));
            cipher.init(Cipher.DECRYPT_MODE, getSecretKeySpec(secret, salt), ivSpec);
            return new String(cipher.doFinal(data));
        } catch (Exception e) {
            CommonLogger.getCommonLogger(CacheClientConstant.MODE_NAME).errorf("action:AES256Decrypt", e);
            throw new CacheSecretException("AES256 decrypt fail", e);
        }
    }

    private static SecretKeySpec getSecretKeySpec(byte[] secret, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(PBKDF2WithHmacSHA256);
        KeySpec spec = new PBEKeySpec(new String(secret).toCharArray(), salt.getBytes(), 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }
}
