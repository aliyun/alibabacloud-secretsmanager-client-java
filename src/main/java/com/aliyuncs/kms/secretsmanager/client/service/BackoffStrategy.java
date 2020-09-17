package com.aliyuncs.kms.secretsmanager.client.service;

import com.aliyuncs.kms.secretsmanager.client.exception.CacheSecretException;

/**
 * 规避重试策略接口
 */
public interface BackoffStrategy {

    /**
     * 初始化策略
     *
     * @throws CacheSecretException
     */
    void init() throws CacheSecretException;

    /**
     * 获取规避等待时间
     *
     * @param retryTimes 规避重试次数
     * @return 规避等待时间，时间单位MS
     */
    long getWaitTimeExponential(int retryTimes);
}
