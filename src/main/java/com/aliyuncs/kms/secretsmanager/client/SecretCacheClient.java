package com.aliyuncs.kms.secretsmanager.client;

import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.kms.model.v20160120.GetSecretValueRequest;
import com.aliyuncs.kms.model.v20160120.GetSecretValueResponse;
import com.aliyuncs.kms.secretsmanager.client.cache.CacheSecretStoreStrategy;
import com.aliyuncs.kms.secretsmanager.client.cache.SecretCacheHook;
import com.aliyuncs.kms.secretsmanager.client.exception.CacheSecretException;
import com.aliyuncs.kms.secretsmanager.client.model.CacheSecretInfo;
import com.aliyuncs.kms.secretsmanager.client.model.SecretInfo;
import com.aliyuncs.kms.secretsmanager.client.service.RefreshSecretStrategy;
import com.aliyuncs.kms.secretsmanager.client.service.SecretManagerClient;
import com.aliyuncs.kms.secretsmanager.client.utils.BackoffUtils;
import com.aliyuncs.kms.secretsmanager.client.utils.ByteBufferUtils;
import com.aliyuncs.kms.secretsmanager.client.utils.CacheClientConstant;
import com.aliyuncs.kms.secretsmanager.client.utils.CommonLogger;
import com.aliyuncs.utils.StringUtils;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class SecretCacheClient implements Closeable {

    /**
     * 默认TTL时间
     */
    private static final long DEFAULT_TTL = 60 * 60 * 1000;

    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(5);
    private final Map<String, ScheduledFuture> scheduledFutureMap = new ConcurrentHashMap<>();

    /**
     * 凭据Version Stage
     */
    protected String stage = CacheClientConstant.STAGE_ACS_CURRENT;

    /**
     * secret value解析TTL字段名称
     */
    protected String jsonTTLPropertyName = "ttl";

    protected SecretManagerClient secretClient;
    protected CacheSecretStoreStrategy cacheSecretStoreStrategy;
    protected RefreshSecretStrategy refreshSecretStrategy;
    protected SecretCacheHook cacheHook;
    protected Map<String, Long> secretTTLMap = new HashMap<>();

    public SecretCacheClient() {

    }

    /**
     * 根据凭据名称获取secretInfo信息
     *
     * @param secretName 指定的凭据名称
     * @return secretInfo信息
     */
    public SecretInfo getSecretInfo(final String secretName) throws CacheSecretException {
        if (StringUtils.isEmpty(secretName)) {
            throw new IllegalArgumentException("the argument[secretName] must not be null");
        }
        CacheSecretInfo cacheSecretInfo = this.cacheSecretStoreStrategy.getCacheSecretInfo(secretName);
        if (cacheSecretInfo != null && !judgeCacheExpire(cacheSecretInfo)) {
            return cacheHook.get(cacheSecretInfo);
        } else {
            synchronized (secretName.intern()) {
                cacheSecretInfo = this.cacheSecretStoreStrategy.getCacheSecretInfo(secretName);
                if (cacheSecretInfo != null && !judgeCacheExpire(cacheSecretInfo)) {
                    return cacheHook.get(cacheSecretInfo);
                } else {
                    SecretInfo secretInfo = getSecretValue(secretName);
                    storeAndRefresh(secretName, secretInfo);
                    return cacheHook.put(secretInfo) == null ? null : cacheHook.put(secretInfo).getSecretInfo();
                }
            }
        }
    }

    /**
     * 根据凭据名称获取凭据存储值文本信息
     *
     * @param secretName 指定的凭据名称
     * @return 凭据存储值文本信息
     */
    public String getStringValue(final String secretName) throws CacheSecretException {
        SecretInfo secretInfo = getSecretInfo(secretName);
        if (secretInfo == null) {
            return null;
        }
        if (!CacheClientConstant.TEXT_DATA_TYPE.equals(secretInfo.getSecretDataType())) {
            throw new IllegalArgumentException(String.format("the secret named[%s] do not support text value", secretName));
        }
        return secretInfo.getSecretValue();
    }

    /**
     * 根据凭据名称获取凭据存储的二进制信息
     *
     * @param secretName 指定的凭据名称
     * @return 凭据存储值二进制信息
     */
    public ByteBuffer getBinaryValue(final String secretName) throws CacheSecretException {
        SecretInfo secretInfo = getSecretInfo(secretName);
        if (secretInfo == null) {
            return null;
        }
        if (!CacheClientConstant.BINARY_DATA_TYPE.equals(secretInfo.getSecretDataType())) {
            throw new IllegalArgumentException(String.format("the secret named[%s] do not support binary value", secretName));
        }
        return ByteBufferUtils.convertStringToByte(secretInfo.getSecretValue());
    }

    /**
     * 强制刷新指定的凭据名称
     *
     * @param secretName 指定的凭据名称
     * @return 刷新是否成功
     * @throws InterruptedException
     */
    public boolean refreshNow(final String secretName) throws InterruptedException {
        if (StringUtils.isEmpty(secretName)) {
            throw new IllegalArgumentException("the argument[secretName] must not be null");
        }
        return refreshNow(secretName, null);
    }

    private boolean refreshNow(final String secretName, SecretInfo secretInfo) throws InterruptedException {
        synchronized (secretName.intern()) {
            try {
                refresh(secretName, secretInfo);
                removeRefreshTask(secretName);
                addRefreshTask(secretName, new RefreshSecretTask(secretName));
            } catch (InterruptedException e) {
                CommonLogger.getCommonLogger(CacheClientConstant.MODE_NAME).errorf("action:refreshNow", e);
                throw e;
            } catch (Exception e) {
                CommonLogger.getCommonLogger(CacheClientConstant.MODE_NAME).errorf("action:refreshNow", e);
                return false;
            }
        }
        return true;
    }

    protected void init() throws CacheSecretException {
        secretClient.init();
        cacheSecretStoreStrategy.init();
        refreshSecretStrategy.init();
        cacheHook.init();
        for (String secretName : secretTTLMap.keySet()) {
            SecretInfo secretInfo = null;
            try {
                secretInfo = getSecretValue(secretName);
            } catch (CacheSecretException e) {
                CommonLogger.getCommonLogger(CacheClientConstant.MODE_NAME).errorf("action:initSecretCacheClient", e);
                if (judgeSkipRefreshException(e)) {
                    throw e;
                }
            }
            storeAndRefresh(secretName, secretInfo);
        }

        CommonLogger.getCommonLogger(CacheClientConstant.MODE_NAME).infof("secretCacheClient init success");
    }

    private boolean judgeCacheExpire(final CacheSecretInfo cacheSecretInfo) {
        long ttl = refreshSecretStrategy.parseTTL(cacheSecretInfo.getSecretInfo());
        if (ttl <= 0) {
            ttl = secretTTLMap.getOrDefault(cacheSecretInfo.getSecretInfo().getSecretName(), DEFAULT_TTL);
        }
        return System.currentTimeMillis() - cacheSecretInfo.getRefreshTimestamp() > ttl;
    }

    private SecretInfo getSecretValue(final String secretName) throws CacheSecretException {
        GetSecretValueRequest request = new GetSecretValueRequest();
        request.setSecretName(secretName);
        request.setVersionStage(stage);
        request.setFetchExtendedConfig(true);
        GetSecretValueResponse resp;
        try {
            resp = secretClient.getSecretValue(request);
        } catch (ClientException e) {
            CommonLogger.getCommonLogger(CacheClientConstant.MODE_NAME).errorf("action:getSecretValue", e);
            if (!BackoffUtils.judgeNeedRecoveryException(e)) {
                throw new CacheSecretException(e);
            }
            try {
                SecretInfo secretInfo = cacheHook.recoveryGetSecret(secretName);
                if (secretInfo != null) {
                    return secretInfo;
                } else {
                    throw e;
                }
            } catch (ClientException ce) {
                CommonLogger.getCommonLogger(CacheClientConstant.MODE_NAME).errorf("action:getSecretValue", e);
                throw new CacheSecretException(e);
            }
        }
        return new SecretInfo(resp.getSecretName(), resp.getVersionId(), resp.getSecretData(), resp.getSecretDataType(), resp.getCreateTime());
    }

    private void storeAndRefresh(final String secretName, final SecretInfo secretInfo) throws CacheSecretException {
        try {
            refreshNow(secretName, secretInfo);
        } catch (InterruptedException ignore) {
            // 此异常忽略不阻碍用户流程
        }
    }

    private void refresh(String secretName, SecretInfo secretInfo) throws CacheSecretException {
        if (secretInfo == null) {
            secretInfo = getSecretValue(secretName);
        }
        CacheSecretInfo cacheSecretInfo = cacheHook.put(secretInfo);
        if (cacheSecretInfo != null) {
            cacheSecretStoreStrategy.storeSecret(cacheSecretInfo);
        }
        CommonLogger.getCommonLogger(CacheClientConstant.MODE_NAME).infof("secretName:{} refresh success", secretName);
    }

    private void addRefreshTask(String secretName, Runnable runnable) throws CacheSecretException {
        CacheSecretInfo cacheSecretInfo = cacheSecretStoreStrategy.getCacheSecretInfo(secretName);
        long executeTime = refreshSecretStrategy.parseNextExecuteTime(cacheSecretInfo);
        if (executeTime <= 0) {
            long refreshTimestamp = cacheSecretInfo.getRefreshTimestamp();
            executeTime = refreshSecretStrategy.getNextExecuteTime(secretName, secretTTLMap.getOrDefault(secretName, DEFAULT_TTL), refreshTimestamp);
            executeTime = Math.max(executeTime, System.currentTimeMillis());
        }
        ScheduledFuture<?> schedule = scheduledThreadPoolExecutor.schedule(runnable, executeTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        scheduledFutureMap.put(secretName, schedule);
        CommonLogger.getCommonLogger(CacheClientConstant.MODE_NAME).infof("secretName:{} addRefreshTask success", secretName);
    }

    private void removeRefreshTask(String secretName) throws InterruptedException {
        if (scheduledFutureMap.get(secretName) != null) {
            scheduledThreadPoolExecutor.remove((RunnableScheduledFuture<?>) scheduledFutureMap.get(secretName));
        }
    }

    private boolean judgeServerException(ClientException e) {
        return BackoffUtils.judgeNeedBackoff(e);
    }

    private boolean judgeSkipRefreshException(CacheSecretException e) {
        return e.getCause() instanceof ClientException && !judgeServerException((ClientException) e.getCause())
                && !(CacheClientConstant.CLIENT_EXCEPTION_ERROR_CODE_FORBIDDEN_IN_DEBT_OVER_DUE.equals(((ClientException) e.getCause()).getErrCode()) || CacheClientConstant.CLIENT_EXCEPTION_ERROR_CODE_FORBIDDEN_IN_DEBT.equals(((ClientException) e.getCause()).getErrCode()));
    }

    @Override
    public void close() throws IOException {
        if (cacheSecretStoreStrategy != null) {
            cacheSecretStoreStrategy.close();
        }
        if (refreshSecretStrategy != null) {
            refreshSecretStrategy.close();
        }
        if (secretClient != null) {
            secretClient.close();
        }
        if (cacheHook != null) {
            cacheHook.close();
        }
        if (!scheduledThreadPoolExecutor.isShutdown()) {
            scheduledThreadPoolExecutor.shutdownNow();
        }
    }

    class RefreshSecretTask implements Runnable {
        private final String secretName;

        public RefreshSecretTask(String secretName) {
            this.secretName = secretName;
        }

        @Override
        public void run() {
            try {
                refresh(secretName, null);
            } catch (CacheSecretException e) {
                CommonLogger.getCommonLogger(CacheClientConstant.MODE_NAME).errorf("action:refreshSecretTask", e);
            }
            try {
                addRefreshTask(secretName, this);
            } catch (CacheSecretException e) {
                CommonLogger.getCommonLogger(CacheClientConstant.MODE_NAME).errorf("action:addRefreshTask", e);
            }
        }
    }
}
