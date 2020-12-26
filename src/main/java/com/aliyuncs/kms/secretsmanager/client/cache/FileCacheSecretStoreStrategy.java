package com.aliyuncs.kms.secretsmanager.client.cache;


import com.aliyuncs.kms.secretsmanager.client.exception.CacheSecretException;
import com.aliyuncs.kms.secretsmanager.client.model.CacheSecretInfo;
import com.aliyuncs.kms.secretsmanager.client.model.SecretInfo;
import com.aliyuncs.kms.secretsmanager.client.utils.AES256Utils;
import com.aliyuncs.kms.secretsmanager.client.utils.ArrayUtils;
import com.aliyuncs.kms.secretsmanager.client.utils.CacheClientConstant;
import com.aliyuncs.kms.secretsmanager.client.utils.JsonIOUtils;
import com.aliyuncs.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地文件存储Secret实现
 */
public class FileCacheSecretStoreStrategy implements CacheSecretStoreStrategy {

    private final static String JSON_FILE_NAME_PREFIX = "stage_";
    private final static String JSON_FILE_NAME_SUFFIX = ".json";

    /**
     * 缓存凭据文件路径
     */
    private String cacheSecretPath;

    /**
     * 首次启动时候是否允许从文件进行加载，true为允许
     */
    private boolean reloadOnStart;

    /**
     * 加解密过程中使用的salt
     */
    private String salt;

    private Set<String> reloadedSet = new HashSet<>();
    private final Map<String, CacheSecretInfo> cacheSecretInfoMap = new ConcurrentHashMap<>();


    public FileCacheSecretStoreStrategy() {
        // do nothing
    }

    public FileCacheSecretStoreStrategy(String cacheSecretPath, boolean reloadOnStart, String salt) {
        this.cacheSecretPath = cacheSecretPath;
        this.reloadOnStart = reloadOnStart;
        this.salt = salt;
    }

    @Override
    public void init() throws CacheSecretException {
        if (StringUtils.isEmpty(cacheSecretPath)) {
            cacheSecretPath = ".";
        }
        if (StringUtils.isEmpty(salt)) {
            throw new IllegalArgumentException("the argument[salt] must not be null");
        }
    }

    @Override
    public void storeSecret(CacheSecretInfo cacheSecretInfo) throws CacheSecretException {
	    CacheSecretInfo memoryCacheSecretInfo = cacheSecretInfo.clone();
        CacheSecretInfo fileCacheSecretInfo = cacheSecretInfo.clone();
        SecretInfo secretInfo = fileCacheSecretInfo.getSecretInfo();
        String secretValue = secretInfo.getSecretValue();
        secretInfo.setSecretValue(encryptSecretVale(secretValue, generateRandomKey()));
        String fileName = (JSON_FILE_NAME_PREFIX + cacheSecretInfo.getStage() + JSON_FILE_NAME_SUFFIX).toLowerCase();
        String cacheSecretPath = this.cacheSecretPath + File.separatorChar + secretInfo.getSecretName();
        if (JsonIOUtils.exists(cacheSecretPath, fileName)) {
            JsonIOUtils.delete(cacheSecretPath, fileName);
        }
        JsonIOUtils.writeObject(cacheSecretPath, fileName, fileCacheSecretInfo);
        cacheSecretInfoMap.put(secretInfo.getSecretName(), memoryCacheSecretInfo);
        reloadedSet.add(cacheSecretInfo.getSecretInfo().getSecretName());
    }

    @Override
    public CacheSecretInfo getCacheSecretInfo(String secretName) throws CacheSecretException {
        if (!reloadOnStart && !reloadedSet.contains(secretName)) {
            return null;
        }
        CacheSecretInfo memoryCacheSecretInfo = cacheSecretInfoMap.get(secretName);
        if (memoryCacheSecretInfo != null) {
            return memoryCacheSecretInfo;
        }
        String fileName = (JSON_FILE_NAME_PREFIX + CacheClientConstant.STAGE_ACS_CURRENT + JSON_FILE_NAME_SUFFIX).toLowerCase();
        String cacheSecretPath = this.cacheSecretPath + File.separatorChar + secretName;
        CacheSecretInfo cacheSecretInfo = JsonIOUtils.readObject(cacheSecretPath, fileName, CacheSecretInfo.class);
        if (cacheSecretInfo != null) {
            SecretInfo secretInfo = cacheSecretInfo.getSecretInfo();
            secretInfo.setSecretValue(decryptSecretVale(secretInfo.getSecretValue()));
            cacheSecretInfoMap.put(secretInfo.getSecretName(), cacheSecretInfo);
        }
        return cacheSecretInfo;
    }

    private String encryptSecretVale(String secretValue, byte[] key) throws CacheSecretException {
        byte[] iv = new byte[CacheClientConstant.IV_LENGTH];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.setSeed(System.currentTimeMillis());
        secureRandom.nextBytes(iv);
        byte[] bytes = ArrayUtils.concatAll(AES256Utils.AES_256_CBC_MODE_KEY.getBytes(), key, iv, AES256Utils.encrypt(AES256Utils.AES_256_CBC_MODE_KEY, secretValue.getBytes(), key, iv, salt));
        return Base64.getEncoder().encodeToString(bytes);
    }

    private String decryptSecretVale(String secretValue) throws CacheSecretException {
        byte[] decodeBytes = Base64.getDecoder().decode(secretValue);
        byte[] modeKeyBytes = Arrays.copyOfRange(decodeBytes, 0, AES256Utils.AES_256_CBC_MODE_KEY.getBytes().length);
        byte[] keyBytes = Arrays.copyOfRange(decodeBytes, AES256Utils.AES_256_CBC_MODE_KEY.getBytes().length, AES256Utils.AES_256_CBC_MODE_KEY.getBytes().length + CacheClientConstant.RANDOM_KEY_LENGTH);
        byte[] ivBytes = Arrays.copyOfRange(decodeBytes, AES256Utils.AES_256_CBC_MODE_KEY.getBytes().length + CacheClientConstant.RANDOM_KEY_LENGTH, AES256Utils.AES_256_CBC_MODE_KEY.getBytes().length + CacheClientConstant.RANDOM_KEY_LENGTH + CacheClientConstant.IV_LENGTH);
        byte[] secretBytes = Arrays.copyOfRange(decodeBytes, AES256Utils.AES_256_CBC_MODE_KEY.getBytes().length + CacheClientConstant.RANDOM_KEY_LENGTH + CacheClientConstant.IV_LENGTH, decodeBytes.length);
        return AES256Utils.decrypt(new String(modeKeyBytes), secretBytes, keyBytes, ivBytes, salt);
    }

    private byte[] generateRandomKey() {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[CacheClientConstant.RANDOM_KEY_LENGTH];
        random.nextBytes(bytes);
        return bytes;
    }

    @Override
    public void close() throws IOException {
        // do nothing
    }
}
