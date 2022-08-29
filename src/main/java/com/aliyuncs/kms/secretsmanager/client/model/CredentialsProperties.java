package com.aliyuncs.kms.secretsmanager.client.model;

import com.aliyuncs.auth.AlibabaCloudCredentialsProvider;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public class CredentialsProperties {

    private AlibabaCloudCredentialsProvider provider;

    private List<String> secretNameList;

    private List<RegionInfo> regionInfoList;

    private boolean ignoreSSLCerts;

    private Map<RegionInfo, DKmsConfig> dkmsConfigsMap;

    private Properties sourceProperties;

    private String privateKeyPath;

    private String password;

    public AlibabaCloudCredentialsProvider getProvider() {
        return this.provider;
    }

    public void setProvider(AlibabaCloudCredentialsProvider provider) {
        this.provider = provider;
    }

    public List<String> getSecretNameList() {
        return this.secretNameList;
    }

    public void setSecretNameList(List<String> secretNameList) {
        this.secretNameList = secretNameList;
    }

    public List<RegionInfo> getRegionInfoList() {
        return this.regionInfoList;
    }

    public void setRegionInfoList(List<RegionInfo> regionInfoList) {
        this.regionInfoList = regionInfoList;
    }

    public Properties getSourceProperties() {
        return this.sourceProperties;
    }

    public void setSourceProperties(Properties sourceProperties) {
        this.sourceProperties = sourceProperties;
    }

    public boolean getIgnoreSSLCerts() {
        return ignoreSSLCerts;
    }

    public void setIgnoreSSLCerts(boolean ignoreSSLCerts) {
        this.ignoreSSLCerts = ignoreSSLCerts;
    }

    public Map<RegionInfo, DKmsConfig> getDkmsConfigsMap() {
        return dkmsConfigsMap;
    }

    public void setDkmsConfigsMap(Map<RegionInfo, DKmsConfig> dkmsConfigsMap) {
        this.dkmsConfigsMap = dkmsConfigsMap;
    }

    public String getPrivateKeyPath() {
        return privateKeyPath;
    }

    public void setPrivateKeyPath(String privateKeyPath) {
        this.privateKeyPath = privateKeyPath;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
