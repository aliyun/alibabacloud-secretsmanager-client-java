package com.aliyuncs.kms.secretsmanager.client.service;

import com.aliyuncs.kms.secretsmanager.client.exception.CacheSecretException;
import com.aliyuncs.kms.secretsmanager.client.utils.CacheClientConstant;

public class FullJitterBackoffStrategy implements BackoffStrategy {
    /**
     * 重试最大尝试次数
     */
    private Long retryMaxAttempts;

    /**
     * 重试时间间隔，单位ms
     */
    private Long retryInitialIntervalMills;

    /**
     * 最大等待时间，单位ms
     */
    private Long capacity;

    public FullJitterBackoffStrategy() {
    }

    public FullJitterBackoffStrategy(long retryMaxAttempts, long retryInitialIntervalMills, long capacity) {
        this.retryMaxAttempts = retryMaxAttempts;
        this.retryInitialIntervalMills = retryInitialIntervalMills;
        this.capacity = capacity;
    }

    @Override
    public void init() throws CacheSecretException {
        this.retryMaxAttempts = this.retryMaxAttempts == null ? this.retryMaxAttempts = CacheClientConstant.DEFAULT_RETRY_MAX_ATTEMPTS : this.retryMaxAttempts;
        this.retryInitialIntervalMills = this.retryInitialIntervalMills == null ? this.retryInitialIntervalMills = CacheClientConstant.DEFAULT_RETRY_INITIAL_INTERVAL_MILLS : this.retryInitialIntervalMills;
        this.capacity = this.capacity == null ? this.capacity = CacheClientConstant.DEFAULT_CAPACITY : this.capacity;
    }

    @Override
    public long getWaitTimeExponential(int retryTimes) {
        if (retryTimes > retryMaxAttempts) {
            return -1;
        }
        return Math.min(capacity, (long) (Math.pow(2, retryTimes) * retryInitialIntervalMills));
    }

}
