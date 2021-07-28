package com.aliyuncs.kms.secretsmanager.client.utils;

import com.aliyuncs.auth.AlibabaCloudCredentialsProvider;
import com.aliyuncs.kms.secretsmanager.client.model.CredentialsProperties;
import com.aliyuncs.kms.secretsmanager.client.model.RegionInfo;
import com.aliyuncs.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.*;

public class CredentialsPropertiesUtils {

    public CredentialsPropertiesUtils() {
    }

    public static CredentialsProperties loadCredentialsProperties(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            fileName = CacheClientConstant.CREDENTIALS_PROPERTIES_CONFIG_NAME;
        }
        Properties properties = ConfigUtils.loadConfig(fileName);
        AlibabaCloudCredentialsProvider credentialsProvider;
        List<RegionInfo> regionInfoList = new ArrayList<>();
        List<String> secretNameList = new ArrayList<>();
        if (properties != null && properties.size() > 0) {
            String credentialsType = properties.getProperty(CacheClientConstant.ENV_CREDENTIALS_TYPE_KEY);
            String accessKeyId = properties.getProperty(CacheClientConstant.ENV_CREDENTIALS_ACCESS_KEY_ID_KEY);
            String accessSecret = properties.getProperty(CacheClientConstant.ENV_CREDENTIALS_ACCESS_SECRET_KEY);
            String regionIds = properties.getProperty(CacheClientConstant.ENV_CACHE_CLIENT_REGION_ID_KEY);
            checkConfigParamNull(credentialsType, CacheClientConstant.ENV_CREDENTIALS_TYPE_KEY);
            checkConfigParamNull(regionIds, CacheClientConstant.ENV_CACHE_CLIENT_REGION_ID_KEY);
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
            switch (credentialsType) {
                case "ak":
                    checkConfigParamNull(accessKeyId, CacheClientConstant.ENV_CREDENTIALS_ACCESS_KEY_ID_KEY);
                    checkConfigParamNull(accessSecret, CacheClientConstant.ENV_CREDENTIALS_ACCESS_SECRET_KEY);
                    credentialsProvider = CredentialsProviderUtils.withAccessKey(accessKeyId, accessSecret);
                    break;
                case "token":
                    String credentialsAccessTokenId = properties.getProperty(CacheClientConstant.ENV_CREDENTIALS_ACCESS_TOKEN_ID_KEY);
                    String credentialsAccessToken = properties.getProperty(CacheClientConstant.ENV_CREDENTIALS_ACCESS_TOKEN_KEY);
                    checkConfigParamNull(credentialsAccessTokenId, CacheClientConstant.ENV_CREDENTIALS_ACCESS_TOKEN_ID_KEY);
                    checkConfigParamNull(credentialsAccessToken, CacheClientConstant.ENV_CREDENTIALS_ACCESS_TOKEN_KEY);
                    credentialsProvider = CredentialsProviderUtils.withToken(credentialsAccessTokenId, credentialsAccessToken);
                    break;
                case "sts":
                case "ram_role":
                    checkConfigParamNull(accessKeyId, CacheClientConstant.ENV_CREDENTIALS_ACCESS_KEY_ID_KEY);
                    checkConfigParamNull(accessSecret, CacheClientConstant.ENV_CREDENTIALS_ACCESS_SECRET_KEY);
                    String roleSessionName = properties.getProperty(CacheClientConstant.ENV_CREDENTIALS_ROLE_SESSION_NAME_KEY);
                    String roleArn = properties.getProperty(CacheClientConstant.ENV_CREDENTIALS_ROLE_ARN_KEY);
                    String policy = properties.getProperty(CacheClientConstant.ENV_CREDENTIALS_POLICY_KEY);
                    checkConfigParamNull(roleSessionName, CacheClientConstant.ENV_CREDENTIALS_ROLE_SESSION_NAME_KEY);
                    checkConfigParamNull(roleArn, CacheClientConstant.ENV_CREDENTIALS_ROLE_ARN_KEY);
                    credentialsProvider = CredentialsProviderUtils.withRamRoleArnOrSts(accessKeyId, accessSecret, regionInfoList.get(0).getRegionId(), roleSessionName, roleArn, policy);
                    break;
                case "ecs_ram_role":
                    String roleName = properties.getProperty(CacheClientConstant.ENV_CREDENTIALS_ROLE_NAME_KEY);
                    checkConfigParamNull(roleName, CacheClientConstant.ENV_CREDENTIALS_ROLE_NAME_KEY);
                    credentialsProvider = CredentialsProviderUtils.withEcsRamRole(roleName);
                    break;
                case "client_key":
                    String password = properties.getProperty(CacheClientConstant.ENV_CLIENT_KEY_PASSWORD_NAME_KEY);
                    String privateKeyPath = properties.getProperty(CacheClientConstant.ENV_CLIENT_KEY_PRIVATE_KEY_PATH_NAME_KEY);
                    credentialsProvider = CredentialsProviderUtils.getCredentialsProvider(privateKeyPath, password);
                    break;
                default:
                    throw new IllegalArgumentException(String.format("credentials config param[%s] is illegal", CacheClientConstant.ENV_CREDENTIALS_TYPE_KEY));
            }
            String secretNames = properties.getProperty(CacheClientConstant.PROPERTIES_SECRET_NAMES_KEY);
            if (!StringUtils.isEmpty(secretNames)) {
                secretNameList.addAll(Arrays.asList(secretNames.split(",")));
            }
            CredentialsProperties credentialsProperties = new CredentialsProperties();
            credentialsProperties.setProvider(credentialsProvider);
            credentialsProperties.setRegionInfoList(regionInfoList);
            credentialsProperties.setSecretNameList(secretNameList);
            credentialsProperties.setSourceProperties(properties);
            return credentialsProperties;
        } else {
            return null;
        }
    }

    public static void checkConfigParamNull(String param, String paramName) {
        if (StringUtils.isEmpty(param)) {
            throw new IllegalArgumentException(String.format("credentials config missing required parameters[%s]", paramName));
        }
    }
}
