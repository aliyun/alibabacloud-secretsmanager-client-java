package com.aliyuncs.kms.secretsmanager.client;

import com.aliyuncs.kms.secretsmanager.client.cache.CacheSecretStoreStrategy;
import com.aliyuncs.kms.secretsmanager.client.cache.DefaultSecretCacheHook;
import com.aliyuncs.kms.secretsmanager.client.cache.MemoryCacheSecretStoreStrategy;
import com.aliyuncs.kms.secretsmanager.client.cache.SecretCacheHook;
import com.aliyuncs.kms.secretsmanager.client.exception.CacheSecretException;
import com.aliyuncs.kms.secretsmanager.client.service.BaseSecretManagerClientBuilder;
import com.aliyuncs.kms.secretsmanager.client.service.DefaultRefreshSecretStrategy;
import com.aliyuncs.kms.secretsmanager.client.service.RefreshSecretStrategy;
import com.aliyuncs.kms.secretsmanager.client.service.SecretManagerClient;
import com.aliyuncs.kms.secretsmanager.client.utils.CacheClientConstant;
import com.aliyuncs.kms.secretsmanager.client.utils.CommonLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SecretCacheClientBuilder implements CacheClientBuilder<SecretCacheClient> {

    private SecretCacheClient secretCacheClient;

    /**
     * 构建一个Secret Cache Client
     *
     * @return Secret Cache Client
     * @throws CacheSecretException
     */
    public static SecretCacheClient newClient() throws CacheSecretException {
        SecretCacheClientBuilder builder = new SecretCacheClientBuilder();
        return builder.build();
    }

    /**
     * 根据指定的Secret Manager Client构建一个Cache Client Builder
     *
     * @param client 指定的Secret Manager Client
     * @return Cache Client Builder
     */
    public static SecretCacheClientBuilder newCacheClientBuilder(SecretManagerClient client) {
        SecretCacheClientBuilder builder = new SecretCacheClientBuilder();
        builder.buildSecretCacheClient();
        builder.secretCacheClient.secretClient = client;
        return builder;
    }

    /**
     * 设定指定凭据名称的凭据TTL
     *
     * @param secretName 指定凭据名称
     * @param ttl        凭据轮转周期，单位ms
     * @return builder对象本身
     */
    public SecretCacheClientBuilder withSecretTTL(String secretName, long ttl) {
        buildSecretCacheClient();
        secretCacheClient.secretTTLMap.put(secretName, ttl);
        return this;
    }

    /**
     * 设定secret value解析TTL字段名称
     *
     * @param jsonTTLPropertyName secret value解析TTL字段名称
     * @return builder对象本身
     */
    public SecretCacheClientBuilder withParseJSONTTL(String jsonTTLPropertyName) {
        buildSecretCacheClient();
        secretCacheClient.jsonTTLPropertyName = jsonTTLPropertyName;
        return this;
    }

    /**
     * 设定secret刷新策略
     *
     * @param refreshSecretStrategy secret刷新策略
     * @return builder对象本身
     */
    public SecretCacheClientBuilder withRefreshSecretStrategy(RefreshSecretStrategy refreshSecretStrategy) {
        buildSecretCacheClient();
        secretCacheClient.refreshSecretStrategy = refreshSecretStrategy;
        return this;
    }

    /**
     * 设定secret缓存策略
     *
     * @param cacheSecretStrategy secret缓存策略
     * @return builder对象本身
     */
    public SecretCacheClientBuilder withCacheSecretStrategy(CacheSecretStoreStrategy cacheSecretStrategy) {
        buildSecretCacheClient();
        secretCacheClient.cacheSecretStoreStrategy = cacheSecretStrategy;
        return this;
    }

    /**
     * 指定凭据Version Stage
     *
     * @param stage 凭据Version Stage
     * @return builder对象本身
     */
    public SecretCacheClientBuilder withCacheStage(String stage) {
        buildSecretCacheClient();
        secretCacheClient.stage = stage;
        return this;
    }

    /**
     * 指定凭据Cache Hook
     *
     * @param hook 凭据Cache Hook
     * @return builder对象本身
     */
    public SecretCacheClientBuilder withSecretCacheHook(SecretCacheHook hook) {
        buildSecretCacheClient();
        secretCacheClient.cacheHook = hook;
        return this;
    }

    /**
     * 指定输出日志
     *
     * @param logger 输出日志
     * @return builder对象本身
     */
    public SecretCacheClientBuilder withLogger(Logger logger) {
        CommonLogger.registerLogger(CacheClientConstant.modeName, logger);
        return this;
    }

    /**
     * 构建Cache Client对象，同时对secretClient、cacheSecretStoreStrategy
     *
     * @return
     * @throws CacheSecretException
     */
    public SecretCacheClient build() throws CacheSecretException {
        buildSecretCacheClient();
        if (!CommonLogger.isRegistered(CacheClientConstant.modeName)) {
            CommonLogger.registerLogger(CacheClientConstant.modeName, LoggerFactory.getLogger(CacheClientConstant.modeName));
        }
        if (secretCacheClient.secretClient == null) {
            secretCacheClient.secretClient = BaseSecretManagerClientBuilder.standard().build();
        }
        if (secretCacheClient.cacheSecretStoreStrategy == null) {
            secretCacheClient.cacheSecretStoreStrategy = new MemoryCacheSecretStoreStrategy();
        }
        if (secretCacheClient.refreshSecretStrategy == null) {
            secretCacheClient.refreshSecretStrategy = new DefaultRefreshSecretStrategy(secretCacheClient.jsonTTLPropertyName);
        }
        if (secretCacheClient.cacheHook == null) {
            secretCacheClient.cacheHook = new DefaultSecretCacheHook(secretCacheClient.stage);
        }
        secretCacheClient.init();
        CommonLogger.getCommonLogger(CacheClientConstant.modeName).infof("SecretCacheClientBuilder build success");
        return secretCacheClient;
    }

    private void buildSecretCacheClient() {
        if (secretCacheClient == null) {
            secretCacheClient = new SecretCacheClient();
        }
    }
}
