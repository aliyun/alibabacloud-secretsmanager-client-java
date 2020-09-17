package com.aliyuncs.kms.secretsmanager.client.service;

import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.kms.model.v20160120.GetSecretValueRequest;
import com.aliyuncs.kms.model.v20160120.GetSecretValueResponse;
import com.aliyuncs.kms.secretsmanager.client.exception.CacheSecretException;

import java.io.Closeable;

public interface SecretManagerClient extends Closeable {

    /**
     * 初始化Client
     *
     * @throws CacheSecretException
     */
    void init() throws CacheSecretException;

    /**
     * 获取指定凭据信息
     *
     * @param req 获取指定凭据请求
     * @return 指定凭据信息
     */
    GetSecretValueResponse getSecretValue(GetSecretValueRequest req) throws ClientException;
}
