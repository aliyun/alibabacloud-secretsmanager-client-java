package com.aliyuncs.kms.secretsmanager.client.cache;

import com.aliyuncs.kms.secretsmanager.client.exception.CacheSecretException;
import com.aliyuncs.kms.secretsmanager.client.model.CacheSecretInfo;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存缓存Secret实现
 */
public class MemoryCacheSecretStoreStrategy implements CacheSecretStoreStrategy {

    private final Map<String, CacheSecretInfo> cacheSecretInfoMap = new ConcurrentHashMap<>();

    @Override
    public void init() throws CacheSecretException {
        // do nothing
    }

    @Override
    public void storeSecret(CacheSecretInfo cacheSecretInfo) {
        cacheSecretInfoMap.put(cacheSecretInfo.getSecretInfo().getSecretName(), cacheSecretInfo);
    }

    @Override
    public CacheSecretInfo getCacheSecretInfo(String secretName) {
        return cacheSecretInfoMap.get(secretName);
    }

    @Override
    public void close() throws IOException {
        if (cacheSecretInfoMap != null) {
            cacheSecretInfoMap.clear();
        }
    }
}
