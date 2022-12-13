package com.aliyuncs.kms.secretsmanager.client.utils;

import com.aliyuncs.utils.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Map;

public class ClientKeyUtils {

    private final static String PKCS12 = "PKCS12";

    private ClientKeyUtils() {
        // do nothing
    }

    public static String getPrivateKeyPemFromPk12(byte[] pk12, String password) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException {
        KeyStore keyStore = KeyStore.getInstance(PKCS12);
        keyStore.load(new ByteArrayInputStream(pk12), password.toCharArray());
        Enumeration<String> e = keyStore.aliases();
        String alias = e.nextElement();
        PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, password.toCharArray());
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    public static String getPassword(Map envMap, String envVariableName, String filePathName) {
        String passwordFromEnvName = "";
        if (!StringUtils.isEmpty(envVariableName)) {
            passwordFromEnvName = (String) envMap.getOrDefault(envVariableName, "");
        }
        String password = "";
        if (!StringUtils.isEmpty(passwordFromEnvName)) {
            password = System.getenv(passwordFromEnvName);
        }
        if (StringUtils.isEmpty(password) && !StringUtils.isEmpty(filePathName)) {
            String passwordFilePath = (String) envMap.getOrDefault(filePathName, "");
            if (!StringUtils.isEmpty(passwordFilePath)) {
                password = readPasswordFile(passwordFilePath);
            }
        }
        if (StringUtils.isEmpty(password)) {
            password = System.getenv(CacheClientConstant.DEFAULT_ENV_CLIENT_KEY_PASSWORD_NAME);
        }
        if (StringUtils.isEmpty(password)) {
            throw new IllegalArgumentException("client key password is not provided");
        }
        return password;
    }

    public static String readPasswordFile(String passwordFilePath) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(passwordFilePath));
            return new String(bytes, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
