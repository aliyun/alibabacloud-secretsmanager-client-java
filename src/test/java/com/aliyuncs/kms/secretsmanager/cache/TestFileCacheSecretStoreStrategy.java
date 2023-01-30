package com.aliyuncs.kms.secretsmanager.cache;

import com.aliyuncs.kms.secretsmanager.client.cache.SecretCacheHook;
import com.aliyuncs.kms.secretsmanager.client.model.CacheSecretInfo;
import com.aliyuncs.kms.secretsmanager.client.cache.DefaultSecretCacheHook;
import com.aliyuncs.kms.secretsmanager.client.utils.CacheClientConstant;
import com.aliyuncs.kms.secretsmanager.client.model.SecretInfo;
import com.aliyuncs.kms.secretsmanager.client.cache.FileCacheSecretStoreStrategy;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;

public class TestFileCacheSecretStoreStrategy {
    private FileCacheSecretStoreStrategy storeStrategy;
    private String secretName = "cache_secret_name";
    private String accessKey = System.getenv("#accessKeyId#");
    private String accessSecret = System.getenv("#accessKeySecret#");

    @Before
    public void init() {
        storeStrategy = new FileCacheSecretStoreStrategy();
    }

    @Test
    public void testStoreAndGet() {
        testStoreSecret();
        testGetCacheSecretInfo();
    }

    private void testStoreSecret() {
        try {
            SecretInfo secretInfo = new SecretInfo(secretName, accessKey, "{\"accessKey\":\"" + accessKey + "\",\"accessSecret\":\"" + accessSecret + "\",\"rotateTimestamp\":\"1593673780000\",\"scheduleRotateTimestamp\":\"1593673780000\",\"refreshInterval\":  10000 , \"driftDurationMs\" : 30000 ,\"ttl\":120000 }", "text", 1593673780000l + "");
            SecretCacheHook hook = new DefaultSecretCacheHook(CacheClientConstant.STAGE_ACS_CURRENT);
            CacheSecretInfo cacheSecretInfo = hook.put(secretInfo);
            storeStrategy.storeSecret(hook.put(secretInfo));
            System.out.println("storeSecret cacheSecretInfo:" + new Gson().toJson(cacheSecretInfo));
        } catch (Exception e) {
            System.out.println("testStoreSecret encrypt error:" + e.getMessage());
        }
    }

    private void testGetCacheSecretInfo() {
        try {
            System.out.println("getCacheSecretInfo:" + new Gson().toJson(storeStrategy.getCacheSecretInfo(secretName)));
        } catch (Exception e) {
            System.out.println("testGetCacheSecretInfo encrypt error:" + e.getMessage());
        }
    }
}
