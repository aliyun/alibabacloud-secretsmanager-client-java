package com.aliyuncs.kms.secretsmanager.client.model;

import com.google.gson.annotations.SerializedName;

public class ClientKeyInfo {

    @SerializedName("KeyId")
    private String keyId;
    @SerializedName("PrivateKeyData")
    private String privateKeyData;

    public String getKeyId() {
        return this.keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getPrivateKeyData() {
        return this.privateKeyData;
    }

    public void setPrivateKeyData(String privateKeyData) {
        this.privateKeyData = privateKeyData;
    }

}
