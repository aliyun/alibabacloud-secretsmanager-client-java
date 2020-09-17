package com.aliyuncs.kms.secretsmanager.client.model;

import java.io.Serializable;

/**
 * secret缓存对象
 */
public class CacheSecretInfo implements Serializable, Cloneable {

    /**
     * secret对象
     */
    private SecretInfo secretInfo;

    /**
     * the version stage of the secret
     */
    private String stage;

    /**
     * 刷新时间戳
     */
    private long refreshTimestamp;

    public CacheSecretInfo() {
        // do nothing
    }

    public CacheSecretInfo(SecretInfo secretInfo, String stage, long refreshTimestamp) {
        this.secretInfo = secretInfo;
        this.stage = stage;
        this.refreshTimestamp = refreshTimestamp;
    }

    public SecretInfo getSecretInfo() {
        return secretInfo;
    }

    public String getStage() {
        return stage;
    }

    public long getRefreshTimestamp() {
        return refreshTimestamp;
    }

    @Override
    public CacheSecretInfo clone() {
        CacheSecretInfo cacheSecretInfo;
        try {
            cacheSecretInfo = (CacheSecretInfo) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        cacheSecretInfo.secretInfo = secretInfo.clone();
        return cacheSecretInfo;
    }

    @Override
    public String toString() {
        return "CacheSecretInfo{" +
                "secretInfo=" + secretInfo +
                ", stage='" + stage + '\'' +
                ", refreshTimestamp=" + refreshTimestamp +
                '}';
    }
}
