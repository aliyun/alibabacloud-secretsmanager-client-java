package com.aliyuncs.kms.secretsmanager.client.service;

import com.aliyuncs.kms.secretsmanager.client.exception.CacheSecretException;
import com.aliyuncs.kms.secretsmanager.client.model.CacheSecretInfo;
import com.aliyuncs.kms.secretsmanager.client.model.SecretInfo;
import com.aliyuncs.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.Map;

/**
 * 默认Secret刷新策略
 */
public class DefaultRefreshSecretStrategy implements RefreshSecretStrategy {

    private final static Gson gson = new Gson();

    /**
     * secret value解析TTL字段名称
     */
    private String jsonTTLPropertyName;

    public DefaultRefreshSecretStrategy() {
        // do nothing
    }

    public DefaultRefreshSecretStrategy(String jsonTTLPropertyName) {
        this.jsonTTLPropertyName = jsonTTLPropertyName;

    }

    @Override
    public void init() throws CacheSecretException {
        // do nothing
    }

    @Override
    public long getNextExecuteTime(String secretName, long ttl, long offsetTimestamp) {
        long now = System.currentTimeMillis();
        if (ttl + offsetTimestamp > now) {
            return ttl + offsetTimestamp;
        } else {
            return now + ttl;
        }
    }

    @Override
    public long parseNextExecuteTime(CacheSecretInfo cacheSecretInfo) {
        SecretInfo secretInfo = cacheSecretInfo.getSecretInfo();
        long ttl = parseTTL(secretInfo);
        if (ttl <= 0) return ttl;
        return getNextExecuteTime(secretInfo.getSecretName(), ttl, cacheSecretInfo.getRefreshTimestamp());
    }

    @Override
    public long parseTTL(SecretInfo secretInfo) {
        if (StringUtils.isEmpty(jsonTTLPropertyName)) {
            return -1;
        }
        try {
            Map<String, Object> map = gson.fromJson(secretInfo.getSecretValue(), Map.class);
            if (map.get(jsonTTLPropertyName) == null) {
                return -1;
            }
            return ((Double) map.get(jsonTTLPropertyName)).longValue();
        } catch (JsonSyntaxException ignore) {
            return -1;
        }
    }

    @Override
    public void close() throws IOException {
        // do nothing
    }
}
