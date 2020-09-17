package com.aliyuncs.kms.secretsmanager.client.cache;

import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.kms.secretsmanager.client.exception.CacheSecretException;
import com.aliyuncs.kms.secretsmanager.client.model.CacheSecretInfo;
import com.aliyuncs.kms.secretsmanager.client.model.SecretInfo;

import java.io.IOException;

/**
 * 默认hook,不做特殊操作
 */
public class DefaultSecretCacheHook implements SecretCacheHook {
    /**
     * 缓存的凭据Version Stage
     */
    private String stage;

    public DefaultSecretCacheHook(final String stage) {
        this.stage = stage;
    }

    @Override
    public void init() throws CacheSecretException {
        // do nothing
    }

    @Override
    public CacheSecretInfo put(final SecretInfo secretInfo) {
        return new CacheSecretInfo(secretInfo, stage, System.currentTimeMillis());
    }

    @Override
    public SecretInfo get(final CacheSecretInfo cacheSecretInfo) {
        return cacheSecretInfo.getSecretInfo();
    }

    @Override
    public SecretInfo recoveryGetSecret(final String secretName) throws ClientException {
        return null;
    }

    @Override
    public void close() throws IOException {
        // do nothing
    }
}
