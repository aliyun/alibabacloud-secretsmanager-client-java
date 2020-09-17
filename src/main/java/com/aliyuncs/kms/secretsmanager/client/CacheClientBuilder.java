package com.aliyuncs.kms.secretsmanager.client;

import com.aliyuncs.kms.secretsmanager.client.exception.CacheSecretException;

public interface CacheClientBuilder<T> {
    /**
     * 构建T对象，同时对T对象实例进行初始化
     *
     * @return T对象
     * @throws CacheSecretException
     */
    T build() throws CacheSecretException;
}
