package com.aliyuncs.kms.secretsmanager.client.service;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.auth.AlibabaCloudCredentialsProvider;
import com.aliyuncs.auth.InstanceProfileCredentialsProvider;
import com.aliyuncs.auth.STSAssumeRoleSessionCredentialsProvider;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.HttpClientConfig;
import com.aliyuncs.kms.model.v20160120.GetSecretValueRequest;
import com.aliyuncs.kms.model.v20160120.GetSecretValueResponse;
import com.aliyuncs.kms.secretsmanager.client.exception.CacheSecretException;
import com.aliyuncs.kms.secretsmanager.client.model.RegionInfo;
import com.aliyuncs.kms.secretsmanager.client.utils.*;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.utils.StringUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class DefaultSecretManagerClientBuilder extends BaseSecretManagerClientBuilder {

    private List<RegionInfo> regionInfos = new ArrayList<>();

    private AlibabaCloudCredentialsProvider provider;

    private BackoffStrategy backoffStrategy;


    public DefaultSecretManagerClientBuilder withToken(String tokenId, String token) {
        this.provider = CredentialsProviderUtils.withToken(tokenId, token);
        return this;
    }

    public DefaultSecretManagerClientBuilder withAccessKey(String accessKeyId, String accessKeySecret) {
        this.provider = CredentialsProviderUtils.withAccessKey(accessKeyId, accessKeySecret);
        return this;
    }

    public DefaultSecretManagerClientBuilder withCredentialsProvider(AlibabaCloudCredentialsProvider provider) {
        this.provider = provider;
        return this;
    }

    /**
     * 指定调用地域Id
     *
     * @param regionId 调用地域Id
     * @return
     */
    public DefaultSecretManagerClientBuilder addRegion(String regionId) {
        return addRegion(new RegionInfo(regionId));
    }

    /**
     * 指定调用地域信息
     *
     * @param regionInfo 调用地域信息
     * @return
     */
    public DefaultSecretManagerClientBuilder addRegion(RegionInfo regionInfo) {
        this.regionInfos.add(regionInfo);
        return this;
    }

    /**
     * 指定多个调用地域Id
     *
     * @param regionIds 多个调用地域Id
     * @return
     */
    public DefaultSecretManagerClientBuilder withRegion(String... regionIds) {
        for (String regionId : regionIds) {
            addRegion(new RegionInfo(regionId));
        }
        return this;
    }


    public DefaultSecretManagerClientBuilder withBackoffStrategy(BackoffStrategy backoffStrategy) {
        this.backoffStrategy = backoffStrategy;
        return this;
    }

    public SecretManagerClient build() throws CacheSecretException {
        DefaultSecretManagerClient client = new DefaultSecretManagerClient();
        return client;
    }

    class DefaultSecretManagerClient implements SecretManagerClient {
        /**
         * 请求等待时间
         */
        private final static long REQUEST_WAITING_TIME = 10 * 60 * 1000L;

        private ScheduledThreadPoolExecutor pool = new ScheduledThreadPoolExecutor(5);
        private Map<String, DefaultAcsClient> clientMap = new HashMap<>();


        public GetSecretValueResponse getSecretValue(GetSecretValueRequest req) throws ClientException {
            List<Future<GetSecretValueResponse>> futures = new ArrayList<>();
            CountDownLatch count = null;
            AtomicInteger finished = null;
            for (int i = 0; i < regionInfos.size(); i++) {
                if (i == 0) {
                    try {
                        return getSecretValue(regionInfos.get(i), req);
                    } catch (ClientException e) {
                        CommonLogger.getCommonLogger(CacheClientConstant.modeName).errorf("getSecretValue error", e);
                        if (!BackoffUtils.judgeNeedBackoff(e)) {
                            throw e;
                        }
                        count = new CountDownLatch(1);
                        finished = new AtomicInteger(regionInfos.size());
                    }
                }
                Future<GetSecretValueResponse> future = pool.submit(new RetryGetSecretValueTask(req, regionInfos.get(i), count, finished));
                futures.add(future);
            }

            GetSecretValueResponse getSecretValueResponse = null;
            try {
                count.await(REQUEST_WAITING_TIME, TimeUnit.MILLISECONDS);
                for (Future<GetSecretValueResponse> future : futures) {
                    if (!future.isDone()) {
                        future.cancel(true);
                    } else {
                        getSecretValueResponse = future.get();
                    }
                }
            } catch (InterruptedException e) {
                CommonLogger.getCommonLogger(CacheClientConstant.modeName).errorf("await retryGetSecretValueTask countDown error", e);
                throw new ClientException(e);
            } catch (ExecutionException e) {
                CommonLogger.getCommonLogger(CacheClientConstant.modeName).errorf("retryGetSecretValueTask future get error", e);
                throw new ClientException(e);
            }
            return getSecretValueResponse;
        }

        @Override
        public void close() throws IOException {
            if (pool != null) {
                pool.shutdown();
            }
            if (clientMap != null && clientMap.size() > 0) {
                for (Map.Entry<String, DefaultAcsClient> clientEntry : clientMap.entrySet()) {
                    try {
                        DefaultAcsClient client = clientEntry.getValue();
                        client.getHttpClient().close();
                    } catch (Exception ignore) {
                        // do nothing
                    }
                }
            }
        }

        public GetSecretValueResponse getSecretValue(RegionInfo regionInfo, GetSecretValueRequest req) throws ClientException {
            return getClient(regionInfo).getAcsResponse(req);
        }

        private DefaultAcsClient getClient(RegionInfo regionInfo) {
            if (clientMap.get(regionInfo.getRegionId()) != null) {
                return clientMap.get(regionInfo.getRegionId());
            }
            synchronized (regionInfo) {
                if (clientMap.get(regionInfo.getRegionId()) != null) {
                    return clientMap.get(regionInfo.getRegionId());
                }
                IClientProfile profile = DefaultProfile.getProfile(regionInfo.getRegionId());
                if (!StringUtils.isEmpty(regionInfo.getEndpoint())) {
                    DefaultProfile.addEndpoint(regionInfo.getRegionId(), CacheClientConstant.PRODUCT_NAME, regionInfo.getEndpoint());
                } else if (regionInfo.getVpc()) {
                    DefaultProfile.addEndpoint(regionInfo.getRegionId(), CacheClientConstant.PRODUCT_NAME, KmsEndpointUtils.getVPCEndpoint(regionInfo.getRegionId()));
                }
                HttpClientConfig clientConfig = HttpClientConfig.getDefault();
                clientConfig.setIgnoreSSLCerts(true);
                profile.setHttpClientConfig(clientConfig);
                clientMap.put(regionInfo.getRegionId(), new DefaultAcsClient(profile, provider));
            }
            return clientMap.get(regionInfo.getRegionId());
        }

        public void init() throws CacheSecretException {
            initEnv();
            if (backoffStrategy == null) {
                backoffStrategy = new FullJitterBackoffStrategy();
            }
            backoffStrategy.init();
            regionInfos = sortRegionInfos(regionInfos);
        }

        private void initEnv() throws CacheSecretException {
            Map<String, String> envMap = System.getenv();
            if (regionInfos.size() == 0) {
                String regionJson = envMap.get(CacheClientConstant.ENV_CACHE_CLIENT_REGION_ID_KEY);
                checkEnvParamNull(regionJson, CacheClientConstant.ENV_CACHE_CLIENT_REGION_ID_KEY);
                try {
                    List<Map<String, Object>> list = new Gson().fromJson(regionJson, List.class);
                    for (Map<String, Object> map : list) {
                        RegionInfo regionInfo = new RegionInfo();
                        regionInfo.setEndpoint(TypeUtils.parseString(map.get("endPoint")));
                        regionInfo.setRegionId(TypeUtils.parseString(map.get("regionId")));
                        regionInfo.setVpc(TypeUtils.parseBoolean(map.get("vpc")));
                        regionInfos.add(regionInfo);
                    }
                } catch (Exception e) {
                    throw new IllegalArgumentException(String.format("env param[%s] is illegal", CacheClientConstant.ENV_CACHE_CLIENT_REGION_ID_KEY));
                }
            }
            if (provider == null) {
                String credentialsType = envMap.get(CacheClientConstant.ENV_CREDENTIALS_TYPE_KEY);
                checkEnvParamNull(credentialsType, CacheClientConstant.ENV_CREDENTIALS_TYPE_KEY);
                String accessKeyId = envMap.get(CacheClientConstant.ENV_CREDENTIALS_ACCESS_KEY_ID_KEY);
                String accessSecret = envMap.get(CacheClientConstant.ENV_CREDENTIALS_ACCESS_SECRET_KEY);

                AlibabaCloudCredentialsProvider provider;
                switch (credentialsType) {
                    case "ak":
                        checkEnvParamNull(accessKeyId, CacheClientConstant.ENV_CREDENTIALS_ACCESS_KEY_ID_KEY);
                        checkEnvParamNull(accessSecret, CacheClientConstant.ENV_CREDENTIALS_ACCESS_SECRET_KEY);
                        provider = CredentialsProviderUtils.withAccessKey(accessKeyId, accessSecret);
                        break;
                    case "token":
                        String credentialsAccessTokenId = envMap.get(CacheClientConstant.ENV_CREDENTIALS_ACCESS_TOKEN_ID_KEY);
                        String credentialsAccessToken = envMap.get(CacheClientConstant.ENV_CREDENTIALS_ACCESS_TOKEN_KEY);
                        checkEnvParamNull(credentialsAccessTokenId, CacheClientConstant.ENV_CREDENTIALS_ACCESS_TOKEN_ID_KEY);
                        checkEnvParamNull(credentialsAccessToken, CacheClientConstant.ENV_CREDENTIALS_ACCESS_TOKEN_KEY);
                        provider = CredentialsProviderUtils.withToken(credentialsAccessTokenId, credentialsAccessToken);
                        break;
                    case "sts":
                    case "ram_role":
                        String roleSessionName = envMap.get(CacheClientConstant.ENV_CREDENTIALS_ROLE_SESSION_NAME_KEY);
                        String roleArn = envMap.get(CacheClientConstant.ENV_CREDENTIALS_ROLE_ARN_KEY);
                        String policy = envMap.get(CacheClientConstant.ENV_CREDENTIALS_POLICY_KEY);
                        checkEnvParamNull(accessKeyId, CacheClientConstant.ENV_CREDENTIALS_ACCESS_KEY_ID_KEY);
                        checkEnvParamNull(accessSecret, CacheClientConstant.ENV_CREDENTIALS_ACCESS_SECRET_KEY);
                        checkEnvParamNull(roleSessionName, CacheClientConstant.ENV_CREDENTIALS_ROLE_SESSION_NAME_KEY);
                        checkEnvParamNull(roleArn, CacheClientConstant.ENV_CREDENTIALS_ROLE_ARN_KEY);
                        provider = new STSAssumeRoleSessionCredentialsProvider(accessKeyId, accessSecret, roleSessionName, roleArn, regionInfos.get(0).getRegionId(), policy);
                        break;
                    case "ecs_ram_role":
                        String roleName = envMap.get(CacheClientConstant.ENV_CREDENTIALS_ROLE_NAME_KEY);
                        checkEnvParamNull(roleName, CacheClientConstant.ENV_CREDENTIALS_ROLE_NAME_KEY);
                        provider = new InstanceProfileCredentialsProvider(roleName);
                        break;
                    default:
                        throw new IllegalArgumentException(String.format("env param[%s] is illegal", CacheClientConstant.ENV_CREDENTIALS_TYPE_KEY));
                }
                withCredentialsProvider(provider);
            }
        }

        private void checkEnvParamNull(String param, String paramName) {
            if (StringUtils.isEmpty(param)) {
                throw new IllegalArgumentException(String.format("env param[%s] is required", paramName));
            }
        }

        class RetryGetSecretValueTask implements Callable<GetSecretValueResponse> {
            final private GetSecretValueRequest req;
            final private RegionInfo regionInfo;
            final private CountDownLatch countDownLatch;
            final private AtomicInteger finished;

            public RetryGetSecretValueTask(GetSecretValueRequest req, RegionInfo regionInfo, CountDownLatch countDownLatch, AtomicInteger finished) {
                this.req = req;
                this.regionInfo = regionInfo;
                this.countDownLatch = countDownLatch;
                this.finished = finished;
            }

            @Override
            public GetSecretValueResponse call() throws Exception {
                try {
                    GetSecretValueResponse resp = retryGetSecretValue(req, regionInfo, 0);
                    countDownLatch.countDown();
                    return resp;
                } finally {
                    if (finished.decrementAndGet() == 0) {
                        countDownLatch.countDown();
                    }
                }
            }

            private GetSecretValueResponse retryGetSecretValue(GetSecretValueRequest req, RegionInfo regionInfo, int retryTimes) throws ClientException {
                long waitTimeExponential = backoffStrategy.getWaitTimeExponential(retryTimes);
                if (waitTimeExponential < 0) {
                    throw new ClientException("Times limit exceeded");
                }
                try {
                    Thread.sleep(waitTimeExponential);
                } catch (InterruptedException ignore) {
                }
                try {
                    return getSecretValue(regionInfo, req);
                } catch (ClientException e) {
                    CommonLogger.getCommonLogger(CacheClientConstant.modeName).errorf("getSecretValue error", e);
                    if (!BackoffUtils.judgeNeedBackoff(e)) {
                        throw e;
                    }
                }
                return retryGetSecretValue(req, regionInfo, retryTimes + 1);
            }
        }
    }

    private List<RegionInfo> sortRegionInfos(List<RegionInfo> regionInfos) {
        List<RegionInfoExtend> regionInfoExtends = new ArrayList<>();
        for (RegionInfo regionInfo : regionInfos) {
            long start = System.currentTimeMillis();
            boolean isReachable;
            if (!StringUtils.isEmpty(regionInfo.getEndpoint())) {
                isReachable = PingUtils.ping(regionInfo.getEndpoint());
            } else if (regionInfo.getVpc()) {
                isReachable = PingUtils.ping(KmsEndpointUtils.getVPCEndpoint(regionInfo.getRegionId()));
            } else {
                isReachable = PingUtils.ping(KmsEndpointUtils.getEndPoint(regionInfo.getRegionId()));
            }
            long escaped = System.currentTimeMillis() - start;
            RegionInfoExtend regionInfoExtend = new RegionInfoExtend(regionInfo);
            regionInfoExtend.setReachable(isReachable);
            regionInfoExtend.setEscaped(escaped);
            regionInfoExtends.add(regionInfoExtend);
        }
        return regionInfoExtends.stream().sorted(Comparator.comparing((RegionInfoExtend regionInfoExtend) -> !regionInfoExtend.getReachable())
                .thenComparing(RegionInfoExtend::getEscaped))
                .map(regionInfoExtend -> new RegionInfo(regionInfoExtend.getRegionId(), regionInfoExtend.getVpc(), regionInfoExtend.getEndpoint()))
                .collect(Collectors.toList());
    }

    class RegionInfoExtend {

        private boolean reachable;
        private long escaped;
        private String regionId;
        private boolean vpc;
        private String endpoint;

        public RegionInfoExtend(RegionInfo regionInfo) {
            this.regionId = regionInfo.getRegionId();
            this.vpc = regionInfo.getVpc();
            this.endpoint = regionInfo.getEndpoint();
        }

        public String getRegionId() {
            return regionId;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public boolean getVpc() {
            return this.vpc;
        }

        public boolean getReachable() {
            return this.reachable;
        }

        public void setReachable(boolean reachable) {
            this.reachable = reachable;
        }

        public long getEscaped() {
            return this.escaped;
        }

        public void setEscaped(long escaped) {
            this.escaped = escaped;
        }
    }

}