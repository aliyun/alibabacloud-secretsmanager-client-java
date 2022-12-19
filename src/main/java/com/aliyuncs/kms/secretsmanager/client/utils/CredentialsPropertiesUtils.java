package com.aliyuncs.kms.secretsmanager.client.utils;

import com.aliyuncs.auth.AlibabaCloudCredentialsProvider;
import com.aliyuncs.kms.secretsmanager.client.model.CredentialsProperties;
import com.aliyuncs.kms.secretsmanager.client.model.DKmsConfig;
import com.aliyuncs.kms.secretsmanager.client.model.RegionInfo;
import com.aliyuncs.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.*;

public class CredentialsPropertiesUtils {

    private static final Gson gson = new Gson();
    private static final Type dkmsInstancesType = new TypeToken<ArrayList<DKmsConfig>>() {
    }.getType();

    private CredentialsPropertiesUtils() {
        // do nothing
    }

    public static CredentialsProperties loadCredentialsProperties(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            fileName = CacheClientConstant.CREDENTIALS_PROPERTIES_CONFIG_NAME;
        }
        Properties properties = ConfigUtils.loadConfig(fileName);
        CredentialsProperties credentialsProperties = new CredentialsProperties();
        credentialsProperties.setSourceProperties(properties);
        if (properties != null && properties.size() > 0) {
            initDefaultConfig(credentialsProperties);
            initSecretsRegions(credentialsProperties);
            initCredentialsProvider(credentialsProperties);
            initSecretNames(properties, credentialsProperties);
            return credentialsProperties;
        } else {
            return null;
        }
    }

    private static void initDefaultConfig(CredentialsProperties credentialsProperties) {
        credentialsProperties.setPrivateKeyPath(credentialsProperties.getSourceProperties().getProperty(CacheClientConstant.ENV_CLIENT_KEY_PRIVATE_KEY_PATH_NAME_KEY));
        try {
            String password = ClientKeyUtils.getPassword(credentialsProperties.getSourceProperties(), CacheClientConstant.ENV_CLIENT_KEY_PASSWORD_FROM_ENV_VARIABLE_NAME, CacheClientConstant.ENV_CLIENT_KEY_PASSWORD_FROM_FILE_PATH_NAME);
            credentialsProperties.setPassword(password);
        } catch (Exception ignore) {
            // do nothing
        }
    }

    public static void checkConfigParamNull(String param, String paramName) {
        if (StringUtils.isEmpty(param)) {
            throw new IllegalArgumentException(String.format("credentials config missing required parameters[%s]", paramName));
        }
    }

    private static void initSecretNames(Properties properties, CredentialsProperties credentialsProperties) {
        List<String> secretNameList = new ArrayList<>();
        String secretNames = properties.getProperty(CacheClientConstant.PROPERTIES_SECRET_NAMES_KEY);
        if (!StringUtils.isEmpty(secretNames)) {
            secretNameList.addAll(Arrays.asList(secretNames.split(",")));
        }
        credentialsProperties.setSecretNameList(secretNameList);
    }

    private static void initSecretsRegions(CredentialsProperties credentialsProperties) {
        List<RegionInfo> regionInfoList = new ArrayList<>();
        initDkmsInstances(regionInfoList, credentialsProperties);
        initKmsRegions(regionInfoList, credentialsProperties);
        credentialsProperties.setRegionInfoList(regionInfoList);
    }

    private static void initKmsRegions(List<RegionInfo> regionInfoList, CredentialsProperties credentialsProperties) {
        String regionIds = credentialsProperties.getSourceProperties().getProperty(CacheClientConstant.ENV_CACHE_CLIENT_REGION_ID_KEY);
        if (!StringUtils.isEmpty(regionIds)) {
            try {
                List<Map<String, Object>> list = new Gson().fromJson(regionIds, new TypeToken<List<Map<String, Object>>>() {
                }.getType());
                for (Map<String, Object> map : list) {
                    RegionInfo regionInfo = new RegionInfo();
                    regionInfo.setEndpoint(TypeUtils.parseString(map.get(CacheClientConstant.ENV_REGION_ENDPOINT_NAME_KEY)));
                    regionInfo.setRegionId(TypeUtils.parseString(map.get(CacheClientConstant.ENV_REGION_REGION_ID_NAME_KEY)));
                    regionInfo.setVpc(TypeUtils.parseBoolean(map.get(CacheClientConstant.ENV_REGION_VPC_NAME_KEY)));
                    regionInfoList.add(regionInfo);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException(String.format("credentials config param[%s] is illegal", CacheClientConstant.ENV_CACHE_CLIENT_REGION_ID_KEY));
            }
        }
    }

    private static void initDkmsInstances(List<RegionInfo> regionInfoList, CredentialsProperties credentialsProperties) {
        List<DKmsConfig> dKmsConfigs = new ArrayList<>();
        String configJson = credentialsProperties.getSourceProperties().getProperty(CacheClientConstant.CACHE_CLIENT_DKMS_CONFIG_INFO_KEY);

        if (!StringUtils.isEmpty(configJson)) {
            try {
                dKmsConfigs = gson.fromJson(configJson, dkmsInstancesType);
            } catch (Exception e) {
                throw new IllegalArgumentException(String.format("credentials config param[%s] is illegal", CacheClientConstant.CACHE_CLIENT_DKMS_CONFIG_INFO_KEY));
            }
        }
        Map<RegionInfo, DKmsConfig> dKmsConfigsMap = new HashMap<>();
        for (DKmsConfig dKmsConfig : dKmsConfigs) {
            RegionInfo regionInfo = new RegionInfo();
            if (StringUtils.isEmpty(dKmsConfig.getRegionId()) || StringUtils.isEmpty(dKmsConfig.getEndpoint())) {
                throw new IllegalArgumentException("init properties fail,cause of cache_client_dkms_config_info param[regionId or endpoint] is null");
            }
            regionInfo.setRegionId(dKmsConfig.getRegionId());
            regionInfo.setEndpoint(dKmsConfig.getEndpoint());
            regionInfo.setKmsType(CacheClientConstant.DKMS_TYPE);
            try {
                if (!StringUtils.isEmpty(dKmsConfig.getPasswordFromFilePath())) {
                    dKmsConfig.setPassword(ClientKeyUtils.readPasswordFile(dKmsConfig.getPasswordFromFilePath()));
                } else {
                    dKmsConfig.setPassword(ClientKeyUtils.getPassword(credentialsProperties.getSourceProperties(), dKmsConfig.getPasswordFromEnvVariable(), dKmsConfig.getPasswordFromFilePathName()));
                }
            } catch (IllegalArgumentException e) {
                if (StringUtils.isEmpty(credentialsProperties.getPassword())) {
                    throw e;
                }
                dKmsConfig.setPassword(credentialsProperties.getPassword());
            }
            if (StringUtils.isEmpty(dKmsConfig.clientKeyFile) && StringUtils.isEmpty(credentialsProperties.getPrivateKeyPath())) {
                throw new IllegalArgumentException("client key file is not provided");
            }
            dKmsConfigsMap.put(regionInfo, dKmsConfig);
            regionInfoList.add(regionInfo);
        }
        credentialsProperties.setDkmsConfigsMap(dKmsConfigsMap);
    }

    private static void initCredentialsProvider(CredentialsProperties credentialsProperties) {
        AlibabaCloudCredentialsProvider credentialsProvider;
        String credentialsType = credentialsProperties.getSourceProperties().getProperty(CacheClientConstant.ENV_CREDENTIALS_TYPE_KEY);
        String accessKeyId = credentialsProperties.getSourceProperties().getProperty(CacheClientConstant.ENV_CREDENTIALS_ACCESS_KEY_ID_KEY);
        String accessSecret = credentialsProperties.getSourceProperties().getProperty(CacheClientConstant.ENV_CREDENTIALS_ACCESS_SECRET_KEY);
        if (!StringUtils.isEmpty(credentialsType)) {
            switch (credentialsType) {
                case "ak":
                    checkConfigParamNull(accessKeyId, CacheClientConstant.ENV_CREDENTIALS_ACCESS_KEY_ID_KEY);
                    checkConfigParamNull(accessSecret, CacheClientConstant.ENV_CREDENTIALS_ACCESS_SECRET_KEY);
                    credentialsProvider = CredentialsProviderUtils.withAccessKey(accessKeyId, accessSecret);
                    break;
                case "token":
                    String credentialsAccessTokenId = credentialsProperties.getSourceProperties().getProperty(CacheClientConstant.ENV_CREDENTIALS_ACCESS_TOKEN_ID_KEY);
                    String credentialsAccessToken = credentialsProperties.getSourceProperties().getProperty(CacheClientConstant.ENV_CREDENTIALS_ACCESS_TOKEN_KEY);
                    checkConfigParamNull(credentialsAccessTokenId, CacheClientConstant.ENV_CREDENTIALS_ACCESS_TOKEN_ID_KEY);
                    checkConfigParamNull(credentialsAccessToken, CacheClientConstant.ENV_CREDENTIALS_ACCESS_TOKEN_KEY);
                    credentialsProvider = CredentialsProviderUtils.withToken(credentialsAccessTokenId, credentialsAccessToken);
                    break;
                case "sts":
                case "ram_role":
                    checkConfigParamNull(accessKeyId, CacheClientConstant.ENV_CREDENTIALS_ACCESS_KEY_ID_KEY);
                    checkConfigParamNull(accessSecret, CacheClientConstant.ENV_CREDENTIALS_ACCESS_SECRET_KEY);
                    String roleSessionName = credentialsProperties.getSourceProperties().getProperty(CacheClientConstant.ENV_CREDENTIALS_ROLE_SESSION_NAME_KEY);
                    String roleArn = credentialsProperties.getSourceProperties().getProperty(CacheClientConstant.ENV_CREDENTIALS_ROLE_ARN_KEY);
                    String policy = credentialsProperties.getSourceProperties().getProperty(CacheClientConstant.ENV_CREDENTIALS_POLICY_KEY);
                    checkConfigParamNull(roleSessionName, CacheClientConstant.ENV_CREDENTIALS_ROLE_SESSION_NAME_KEY);
                    checkConfigParamNull(roleArn, CacheClientConstant.ENV_CREDENTIALS_ROLE_ARN_KEY);
                    credentialsProvider = CredentialsProviderUtils.withRamRoleArnOrSts(accessKeyId, accessSecret, credentialsProperties.getRegionInfoList().get(0).getRegionId(), roleSessionName, roleArn, policy);
                    break;
                case "ecs_ram_role":
                    String roleName = credentialsProperties.getSourceProperties().getProperty(CacheClientConstant.ENV_CREDENTIALS_ROLE_NAME_KEY);
                    checkConfigParamNull(roleName, CacheClientConstant.ENV_CREDENTIALS_ROLE_NAME_KEY);
                    credentialsProvider = CredentialsProviderUtils.withEcsRamRole(roleName);
                    break;
                case "client_key":
                    String password = ClientKeyUtils.getPassword(credentialsProperties.getSourceProperties(), CacheClientConstant.ENV_CLIENT_KEY_PASSWORD_FROM_ENV_VARIABLE_NAME, CacheClientConstant.ENV_CLIENT_KEY_PASSWORD_FROM_FILE_PATH_NAME);
                    String privateKeyPath = credentialsProperties.getSourceProperties().getProperty(CacheClientConstant.ENV_CLIENT_KEY_PRIVATE_KEY_PATH_NAME_KEY);
                    checkConfigParamNull(privateKeyPath, CacheClientConstant.ENV_CLIENT_KEY_PRIVATE_KEY_PATH_NAME_KEY);
                    credentialsProvider = CredentialsProviderUtils.getCredentialsProvider(privateKeyPath, password);
                    break;
                default:
                    throw new IllegalArgumentException(String.format("credentials config param[%s] is illegal", CacheClientConstant.ENV_CREDENTIALS_TYPE_KEY));
            }
            credentialsProperties.setProvider(credentialsProvider);
        }
    }
}
