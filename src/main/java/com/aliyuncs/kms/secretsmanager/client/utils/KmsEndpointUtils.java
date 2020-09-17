package com.aliyuncs.kms.secretsmanager.client.utils;

/**
 * KMS endpoint 工具类
 */
public class KmsEndpointUtils {

    private KmsEndpointUtils() {
        // do nothing
    }

    public static String getVPCEndpoint(String regionId) {
        return "kms-vpc." + regionId + ".aliyuncs.com";
    }

    public static String getEndPoint(String regionId) {
        return "kms." + regionId + ".aliyuncs.com";
    }
}
