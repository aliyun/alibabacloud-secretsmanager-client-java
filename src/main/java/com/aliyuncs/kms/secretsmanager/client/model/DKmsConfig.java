package com.aliyuncs.kms.secretsmanager.client.model;


import com.aliyun.dkms.gcs.openapi.models.Config;


public class DKmsConfig extends Config {

    private boolean ignoreSslCerts;
    private String passwordFromEnvVariable;
    private String passwordFromFilePathName;

    public DKmsConfig() {
    }

    public boolean getIgnoreSslCerts() {
        return ignoreSslCerts;
    }

    public void setIgnoreSslCerts(boolean ignoreSslCerts) {
        this.ignoreSslCerts = ignoreSslCerts;
    }

    public String getPasswordFromEnvVariable() {
        return passwordFromEnvVariable;
    }

    public void setPasswordFromEnvVariable(String passwordFromEnvVariable) {
        this.passwordFromEnvVariable = passwordFromEnvVariable;
    }

    public String getPasswordFromFilePathName() {
        return passwordFromFilePathName;
    }

    public void setPasswordFromFilePathName(String passwordFromFilePathName) {
        this.passwordFromFilePathName = passwordFromFilePathName;
    }
}
