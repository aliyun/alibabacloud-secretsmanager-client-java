package com.aliyuncs.kms.secretsmanager.client.utils;

import com.aliyuncs.auth.*;
import com.aliyuncs.kms.secretsmanager.client.model.ClientKeyCredentialsProvider;
import com.aliyuncs.kms.secretsmanager.client.model.ClientKeyInfo;

import java.util.Base64;

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

    public static AlibabaCloudCredentialsProvider getCredentialsProvider(String clientKeyPath, String password) {
        ClientKeyInfo clientKeyInfo = JsonIOUtils.readObject(clientKeyPath, "", ClientKeyInfo.class);
        if (clientKeyInfo != null) {
            byte[] pk12 = Base64.getDecoder().decode(clientKeyInfo.getPrivateKeyData());
            try {
                String privateKey = ClientKeyUtils.getPrivateKeyPemFromPk12(pk12, password);
                return new ClientKeyCredentialsProvider(new KeyPairCredentials(clientKeyInfo.getKeyId(), privateKey));
            } catch (Exception e) {
                throw new RuntimeException("getCredentialsProvider fail", e);
            }
        } else {
            throw new RuntimeException("ClientKey is invalid");
        }
    }
}
