package com.aliyuncs.kms.secretsmanager.client.utils;

import com.aliyuncs.auth.*;

public class CredentialsProviderUtils {

    private CredentialsProviderUtils() {
        // do nothing
    }

    public static AlibabaCloudCredentialsProvider withToken(String tokenId, String token) {
        return new StaticCredentialsProvider(new BasicCredentials(tokenId, token));
    }

    public static AlibabaCloudCredentialsProvider withAccessKey(String accessKeyId, String accessKeySecret) {
        return new StaticCredentialsProvider(new BasicCredentials(accessKeyId, accessKeySecret));
    }

    public static AlibabaCloudCredentialsProvider withRamRoleArnOrSts(String accessKeyId, String accessKeySecret, String regionId, String roleSessionName, String roleArn, String policy) {
        return new STSAssumeRoleSessionCredentialsProvider(accessKeyId, accessKeySecret, roleSessionName, roleArn, regionId, policy);
    }

    public static AlibabaCloudCredentialsProvider withEcsRamRole(String roleName) {
        return new InstanceProfileCredentialsProvider(roleName);
    }
}
