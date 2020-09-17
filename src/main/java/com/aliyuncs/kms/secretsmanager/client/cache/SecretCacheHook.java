package com.aliyuncs.kms.secretsmanager.client.cache;

import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.kms.secretsmanager.client.exception.CacheSecretException;
import com.aliyuncs.kms.secretsmanager.client.model.CacheSecretInfo;
import com.aliyuncs.kms.secretsmanager.client.model.SecretInfo;

import java.io.Closeable;

/**
 * 凭据缓存Hook接口
 */
public interface SecretCacheHook extends Closeable {

    /**
     * 初始化Hook
     *
     * @throws CacheSecretException
     */
    void init() throws CacheSecretException;

    /**
     * 将secret对象转化为Cache secret对象
     *
     * @param o secret对象
     * @return Cache secret对象
     */
    CacheSecretInfo put(final SecretInfo o);

    /**
     * 将Cache secret对象转化为secret对象
     *
     * @param cachedObject Cache secret对象
     * @return secret对象
     */
    SecretInfo get(final CacheSecretInfo cachedObject);

    /**
     * @param secretName
     * @return
     * @throws ClientException
     */
    SecretInfo recoveryGetSecret(final String secretName) throws ClientException;
}
