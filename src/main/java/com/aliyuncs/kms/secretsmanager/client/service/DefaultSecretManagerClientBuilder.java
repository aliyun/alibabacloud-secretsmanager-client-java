package com.aliyuncs.kms.secretsmanager.client.service;

import com.aliyun.dkms.gcs.openapi.models.Config;
import com.aliyun.kms.KmsTransferAcsClient;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.auth.AlibabaCloudCredentialsProvider;
import com.aliyuncs.auth.InstanceProfileCredentialsProvider;
import com.aliyuncs.auth.STSAssumeRoleSessionCredentialsProvider;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.HttpClientConfig;
import com.aliyuncs.kms.model.v20160120.GetSecretValueRequest;
import com.aliyuncs.kms.model.v20160120.GetSecretValueResponse;
import com.aliyuncs.kms.secretsmanager.client.exception.CacheSecretException;
import com.aliyuncs.kms.secretsmanager.client.model.CredentialsProperties;
import com.aliyuncs.kms.secretsmanager.client.model.DKmsConfig;
import com.aliyuncs.kms.secretsmanager.client.model.RegionInfo;
import com.aliyuncs.kms.secretsmanager.client.utils.*;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class DefaultSecretManagerClientBuilder extends BaseSecretManagerClientBuilder {

    private List<RegionInfo> regionInfos = new ArrayList<>();

    private Map<RegionInfo, DKmsConfig> dKmsConfigsMap = new HashMap<>();

    private AlibabaCloudCredentialsProvider provider;

    private BackoffStrategy backoffStrategy;

    private String customConfigFile;

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

    public DefaultSecretManagerClientBuilder addDKmsConfig(DKmsConfig dKmsConfig) {
        if (StringUtils.isEmpty(dKmsConfig.getRegionId()) || StringUtils.isEmpty(dKmsConfig.getEndpoint())) {
            throw new IllegalArgumentException("param[regionId or endpoint] is null");
        }
        RegionInfo regionInfo = new RegionInfo();
        regionInfo.setKmsType(CacheClientConstant.DKMS_TYPE);
        regionInfo.setRegionId(dKmsConfig.getRegionId());
        regionInfo.setEndpoint(dKmsConfig.getEndpoint());
        dKmsConfigsMap.put(regionInfo, dKmsConfig);
        regionInfos.add(regionInfo);
        return this;
    }

    public DefaultSecretManagerClientBuilder withCustomConfigFile(String customConfigFile) {
        this.customConfigFile = customConfigFile;
        return this;
    }

    public SecretManagerClient build() throws CacheSecretException {
        DefaultSecretManagerClient client = new DefaultSecretManagerClient();
        return client;
    }

    class DefaultSecretManagerClient implements SecretManagerClient {

        private ScheduledThreadPoolExecutor pool = new ScheduledThreadPoolExecutor(5);
        private Map<RegionInfo, DefaultAcsClient> clientMap = new HashMap<>();


        public GetSecretValueResponse getSecretValue(GetSecretValueRequest req) throws ClientException {
            List<Future<GetSecretValueResponse>> futures = new ArrayList<>();
            CountDownLatch count = null;
            AtomicInteger finished = null;
            for (int i = 0; i < regionInfos.size(); i++) {
                if (i == 0) {
                    try {
                        return getSecretValue(regionInfos.get(i), req);
                    } catch (ClientException e) {
                        CommonLogger.getCommonLogger(CacheClientConstant.MODE_NAME).errorf("action:getSecretValue", e);
                        if (!BackoffUtils.judgeNeedRecoveryException(e)) {
                            throw e;
                        }
                        count = new CountDownLatch(1);
                        finished = new AtomicInteger(regionInfos.size());
                    }
                }
                GetSecretValueRequest request = new GetSecretValueRequest();
                request.setSecretName(req.getSecretName());
                request.setVersionStage(req.getVersionStage());
                request.setFetchExtendedConfig(true);
                request.setAcceptFormat(FormatType.XML);
                Future<GetSecretValueResponse> future = pool.submit(new RetryGetSecretValueTask(request, regionInfos.get(i), count, finished));
                futures.add(future);
            }

            GetSecretValueResponse getSecretValueResponse = null;
            try {
                count.await(CacheClientConstant.REQUEST_WAITING_TIME, TimeUnit.MILLISECONDS);
                for (Future<GetSecretValueResponse> future : futures) {
                    try {
                        if (!future.isDone()) {
                            future.cancel(true);
                        } else {
                            getSecretValueResponse = future.get();
                            if (getSecretValueResponse != null) {
                                return getSecretValueResponse;
                            }
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        CommonLogger.getCommonLogger(CacheClientConstant.MODE_NAME).errorf("action:asyncGetSecretValue", e);
                    }
                }
            } catch (InterruptedException e) {
                CommonLogger.getCommonLogger(CacheClientConstant.MODE_NAME).errorf("action:retryGetSecretValueTask", e);
                throw new ClientException(e);
            } finally {
                if (count.getCount() > 0) {
                    count.countDown();
                }
            }
            throw new ClientException(CacheClientConstant.SDK_READ_TIMEOUT, String.format("refreshSecretTask fail with secretName[%s]", req.getSecretName()));
        }

        @Override
        public void close() throws IOException {
            if (pool != null) {
                pool.shutdown();
            }
            if (clientMap != null && clientMap.size() > 0) {
                for (Map.Entry<RegionInfo, DefaultAcsClient> clientEntry : clientMap.entrySet()) {
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
            if (clientMap.get(regionInfo) != null) {
                return clientMap.get(regionInfo);
            }
            synchronized (regionInfo) {
                if (clientMap.containsKey(regionInfo)) {
                    return clientMap.get(regionInfo);
                }
                if (regionInfo.getKmsType() == CacheClientConstant.DKMS_TYPE) {
                    clientMap.put(regionInfo, buildDKMSTransferClient(regionInfo));
                } else {
                    clientMap.put(regionInfo, buildKMSClient(regionInfo));
                }
            }
            return clientMap.get(regionInfo);
        }

        private DefaultAcsClient buildKMSClient(RegionInfo regionInfo) {
            IClientProfile profile = DefaultProfile.getProfile(regionInfo.getRegionId());
            if (!StringUtils.isEmpty(regionInfo.getEndpoint())) {
                DefaultProfile.addEndpoint(regionInfo.getRegionId(), CacheClientConstant.PRODUCT_NAME, regionInfo.getEndpoint());
            } else if (regionInfo.getVpc()) {
                DefaultProfile.addEndpoint(regionInfo.getRegionId(), CacheClientConstant.PRODUCT_NAME, KmsEndpointUtils.getVPCEndpoint(regionInfo.getRegionId()));
            }
            HttpClientConfig clientConfig = HttpClientConfig.getDefault();
            clientConfig.setIgnoreSSLCerts(true);
            profile.setHttpClientConfig(clientConfig);
            DefaultAcsClient acsClient = new DefaultAcsClient(profile, provider);
            acsClient.appendUserAgent(UserAgentManager.getUserAgent(), UserAgentManager.getProjectVersion());
            return acsClient;
        }

        private DefaultAcsClient buildDKMSTransferClient(RegionInfo regionInfo) {
            Config config;
            IClientProfile profile = DefaultProfile.getProfile(regionInfo.getRegionId(), CacheClientConstant.PRETEND_AK, CacheClientConstant.PRETEND_SK);
            DKmsConfig dKmsConfig = DefaultSecretManagerClientBuilder.this.dKmsConfigsMap.get(regionInfo);
            if (dKmsConfig == null) {
                throw new IllegalArgumentException("Unrecognized regionId");
            } else {
                config = dKmsConfig;
                config.setRegionId(regionInfo.getRegionId());
                config.setEndpoint(regionInfo.getEndpoint());
                config.setPassword(dKmsConfig.getPassword());
            }
            HttpClientConfig clientConfig = HttpClientConfig.getDefault();
            clientConfig.setIgnoreSSLCerts(dKmsConfig.getIgnoreSslCerts());
            profile.setHttpClientConfig(clientConfig);
            return new KmsTransferAcsClient(profile, config);
        }

        public void init() throws CacheSecretException {
            initFromConfigFile();
            initFromEnv();
            checkConfigAndEnv();
            UserAgentManager.registerUserAgent(CacheClientConstant.USER_AGENT_OF_SECRETS_MANAGER_JAVA, 0, CacheClientConstant.PROJECT_VERSION);
            if (backoffStrategy == null) {
                backoffStrategy = new FullJitterBackoffStrategy();
            }
            backoffStrategy.init();
            if (regionInfos.size() > 1) {
                regionInfos = sortRegionInfos(regionInfos);
            }
        }

        private void checkConfigAndEnv() {
            if (dKmsConfigsMap.isEmpty() && provider == null) {
                throw new IllegalArgumentException("The configuration of the secrets manager client must be needed, please config the configuration file or related environment variable, or set your credentials provider.");
            }
            if (regionInfos.isEmpty()) {
                throw new IllegalArgumentException("The param[regionInfo] is needed");
            }
        }

        private void initFromEnv() throws CacheSecretException {
            Map<String, String> envMap = System.getenv();
            initCredentialsProviderFromEnv(envMap);
            initDkmsInstancesFromEnv(envMap);
            initKmsRegionsFromEnv(envMap);
        }

        private void initCredentialsProviderFromEnv(Map<String, String> envMap) {
            String credentialsType = envMap.get(CacheClientConstant.ENV_CREDENTIALS_TYPE_KEY);
            if (!StringUtils.isEmpty(credentialsType)) {
                String accessKeyId = envMap.get(CacheClientConstant.ENV_CREDENTIALS_ACCESS_KEY_ID_KEY);
                String accessSecret = envMap.get(CacheClientConstant.ENV_CREDENTIALS_ACCESS_SECRET_KEY);
                AlibabaCloudCredentialsProvider provider = null;
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
                    case "client_key":
                        String password = ClientKeyUtils.getPassword(envMap, CacheClientConstant.ENV_CLIENT_KEY_PASSWORD_FROM_ENV_VARIABLE_NAME, CacheClientConstant.ENV_CLIENT_KEY_PASSWORD_FROM_FILE_PATH_NAME);
                        String privateKeyPath = envMap.get(CacheClientConstant.ENV_CLIENT_KEY_PRIVATE_KEY_PATH_NAME_KEY);
                        checkEnvParamNull(privateKeyPath, CacheClientConstant.ENV_CLIENT_KEY_PRIVATE_KEY_PATH_NAME_KEY);
                        provider = CredentialsProviderUtils.getCredentialsProvider(privateKeyPath, password);
                        break;
                    default:
                        throw new IllegalArgumentException(String.format("env param[%s] is illegal", CacheClientConstant.ENV_CREDENTIALS_TYPE_KEY));
                }
                if (provider != null) {
                    withCredentialsProvider(provider);
                }
            }
        }

        private void initDkmsInstancesFromEnv(Map<String, String> envMap) {
            List<DKmsConfig> dKmsConfigs = new ArrayList<>();
            String configJson = envMap.get(CacheClientConstant.CACHE_CLIENT_DKMS_CONFIG_INFO_KEY);
            if (!StringUtils.isEmpty(configJson)) {
                try {
                    Type configListType = new TypeToken<ArrayList<DKmsConfig>>() {
                    }.getType();
                    dKmsConfigs.addAll(new Gson().fromJson(configJson, configListType));
                } catch (Exception e) {
                    throw new IllegalArgumentException(String.format("env param[%s] is illegal", CacheClientConstant.CACHE_CLIENT_DKMS_CONFIG_INFO_KEY));
                }

            }
            for (DKmsConfig dKmsConfig : dKmsConfigs) {
                RegionInfo regionInfo = new RegionInfo();
                if (StringUtils.isEmpty(dKmsConfig.getRegionId()) || StringUtils.isEmpty(dKmsConfig.getEndpoint())) {
                    throw new IllegalArgumentException("init env fail,cause of cache_client_dkms_config_info param[regionId or endpoint] is null");
                }
                regionInfo.setRegionId(dKmsConfig.getRegionId());
                regionInfo.setEndpoint(dKmsConfig.getEndpoint());
                regionInfo.setKmsType(CacheClientConstant.DKMS_TYPE);
                if (!StringUtils.isEmpty(dKmsConfig.getPasswordFromFilePath())) {
                    dKmsConfig.setPassword(ClientKeyUtils.readPasswordFile(dKmsConfig.getPasswordFromFilePath()));
                } else {
                    dKmsConfig.setPassword(ClientKeyUtils.getPassword(envMap, dKmsConfig.getPasswordFromEnvVariable(), dKmsConfig.getPasswordFromFilePathName()));
                }
                dKmsConfigsMap.put(regionInfo, dKmsConfig);
                regionInfos.add(regionInfo);
            }
        }

        private void initKmsRegionsFromEnv(Map<String, String> envMap) {
            String regionJson = envMap.get(CacheClientConstant.ENV_CACHE_CLIENT_REGION_ID_KEY);
            if (!StringUtils.isEmpty(regionJson)) {
                try {
                    List<Map<String, Object>> configList = new Gson().fromJson(regionJson, List.class);
                    for (Map<String, Object> map : configList) {
                        RegionInfo regionInfo = new RegionInfo();
                        regionInfo.setRegionId(TypeUtils.parseString(map.get(CacheClientConstant.ENV_REGION_REGION_ID_NAME_KEY)));
                        regionInfo.setEndpoint(TypeUtils.parseString(map.get(CacheClientConstant.ENV_REGION_ENDPOINT_NAME_KEY)));
                        regionInfo.setVpc(TypeUtils.parseBoolean(map.get(CacheClientConstant.ENV_REGION_VPC_NAME_KEY)));
                        regionInfos.add(regionInfo);
                    }
                } catch (Exception e) {
                    throw new IllegalArgumentException(String.format("env param[%s] is illegal", CacheClientConstant.ENV_CACHE_CLIENT_REGION_ID_KEY));
                }
            }
        }

        private void initFromConfigFile() {
            CredentialsProperties credentialsProperties = CredentialsPropertiesUtils.loadCredentialsProperties(customConfigFile);
            if (credentialsProperties != null) {
                if (credentialsProperties.getProvider() != null) {
                    AlibabaCloudCredentialsProvider provider = credentialsProperties.getProvider();
                    withCredentialsProvider(provider);
                }
                regionInfos.addAll(credentialsProperties.getRegionInfoList());
                if (credentialsProperties.getDkmsConfigsMap() != null) {
                    dKmsConfigsMap.putAll(credentialsProperties.getDkmsConfigsMap());
                }
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
                    GetSecretValueResponse resp = retryGetSecretValue(req, regionInfo);
                    countDownLatch.countDown();
                    return resp;
                } catch (Exception e) {
                    CommonLogger.getCommonLogger(CacheClientConstant.MODE_NAME).errorf("action:retryGetSecretValueRun", e);
                    return null;
                } finally {
                    if (finished.decrementAndGet() == 0) {
                        countDownLatch.countDown();
                    }
                }
            }

            private GetSecretValueResponse retryGetSecretValue(GetSecretValueRequest req, RegionInfo regionInfo) throws ClientException {
                int retryTimes = 0;
                while (true) {
                    if (countDownLatch.getCount() == 0) {
                        return null;
                    }
                    long waitTimeExponential = backoffStrategy.getWaitTimeExponential(retryTimes);
                    if (waitTimeExponential < 0) {
                        throw new ClientException(CacheClientConstant.SDK_READ_TIMEOUT, "Times limit exceeded");
                    }
                    try {
                        Thread.sleep(waitTimeExponential);
                    } catch (InterruptedException ignore) {
                    }
                    try {
                        return getSecretValue(regionInfo, req);
                    } catch (ClientException e) {
                        CommonLogger.getCommonLogger(CacheClientConstant.MODE_NAME).errorf("action:getSecretValue regionInfo:{}", regionInfo, e);
                        if (!BackoffUtils.judgeNeedRecoveryException(e)) {
                            throw e;
                        }
                    }
                    retryTimes++;
                }
            }
        }
    }

    private List<RegionInfo> sortRegionInfos(List<RegionInfo> regionInfos) {
        List<RegionInfoExtend> regionInfoExtends = new ArrayList<>();
        for (RegionInfo regionInfo : regionInfos) {
            double pingDelay;
            if (!StringUtils.isEmpty(regionInfo.getEndpoint())) {
                pingDelay = PingUtils.ping(regionInfo.getEndpoint());
            } else if (regionInfo.getVpc()) {
                pingDelay = PingUtils.ping(KmsEndpointUtils.getVPCEndpoint(regionInfo.getRegionId()));
            } else {
                pingDelay = PingUtils.ping(KmsEndpointUtils.getEndPoint(regionInfo.getRegionId()));
            }
            RegionInfoExtend regionInfoExtend = new RegionInfoExtend(regionInfo);
            regionInfoExtend.setReachable(pingDelay >= 0);
            regionInfoExtend.setEscaped(pingDelay >= 0 ? pingDelay : Double.MAX_VALUE);
            regionInfoExtends.add(regionInfoExtend);
        }
        return regionInfoExtends.stream().sorted(Comparator.comparing((RegionInfoExtend regionInfoExtend) -> !regionInfoExtend.getReachable())
                .thenComparing(RegionInfoExtend::getEscaped))
                .map(regionInfoExtend -> new RegionInfo(regionInfoExtend.getRegionId(), regionInfoExtend.getVpc(), regionInfoExtend.getEndpoint(), regionInfoExtend.getKmsType()))
                .collect(Collectors.toList());
    }

    class RegionInfoExtend {

        private boolean reachable;
        private double escaped;
        private String regionId;
        private boolean vpc;
        private String endpoint;
        private int kmsType;

        public RegionInfoExtend(RegionInfo regionInfo) {
            this.regionId = regionInfo.getRegionId();
            this.vpc = regionInfo.getVpc();
            this.endpoint = regionInfo.getEndpoint();
            this.kmsType = regionInfo.getKmsType();
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

        public double getEscaped() {
            return this.escaped;
        }

        public void setEscaped(double escaped) {
            this.escaped = escaped;
        }

        public int getKmsType() {
            return kmsType;
        }
    }

}