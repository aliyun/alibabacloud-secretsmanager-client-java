package com.aliyuncs.kms.secretsmanager.client.service;

import com.aliyuncs.kms.secretsmanager.client.exception.CacheSecretException;
import com.aliyuncs.kms.secretsmanager.client.model.CacheSecretInfo;
import com.aliyuncs.kms.secretsmanager.client.model.SecretInfo;

import java.io.Closeable;

/**
 * 刷新Secret的策略
 */
public interface RefreshSecretStrategy extends Closeable {

    /**
     * 初始化刷新策略
     *
     * @throws CacheSecretException
     */
    void init() throws CacheSecretException;

    /**
     * 获取下一次secret刷新执行的时间
     *
     * @param secretName      指定secret名称
     * @param ttl             轮转时间间隔
     * @param offsetTimestamp 上一次secret刷新时间
     * @return
     */
    long getNextExecuteTime(String secretName, long ttl, long offsetTimestamp);

    /**
     * 通过secret信息解析下一次secret刷新执行的时间
     *
     * @param cacheSecretInfo secret信息
     * @return 下一次secret刷新执行的时间
     */
    long parseNextExecuteTime(CacheSecretInfo cacheSecretInfo);

    /**
     * 根据凭据信息解析轮转时间间隔
     *
     * @param secretInfo
     * @return 凭据信息解析轮转时间间隔，单位MS
     */
    long parseTTL(SecretInfo secretInfo);
}
