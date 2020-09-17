package com.aliyuncs.kms.secretsmanager.client.model;

import com.aliyuncs.kms.secretsmanager.client.utils.ByteBufferUtils;
import com.aliyuncs.kms.secretsmanager.client.utils.CacheClientConstant;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class SecretInfo implements Serializable, Cloneable {

    /**
     * the name of the secret
     */
    private String secretName;

    /**
     * the versionId of the secret
     */
    private String versionId;

    /**
     * the value of the secret
     */
    private String secretValue;

    /**
     * the byte buffer value of the secret
     */
    private ByteBuffer secretValueByteBuffer;

    /**
     * the data type of the secret
     */
    private String secretDataType;

    /**
     * the create time of the secret
     */
    private String createTime;

    public SecretInfo() {
        // do nothing
    }

    public SecretInfo(String secretName, String versionId, String secretValue, String secretDataType, String createTime) {
        this.secretName = secretName;
        this.versionId = versionId;
        this.secretValue = secretValue;
        this.secretDataType = secretDataType;
        this.createTime = createTime;
    }

    public SecretInfo(String secretName, String versionId, String secretValue, ByteBuffer secretValueByteBuffer, String secretDataType, String createTime) {
        this.secretName = secretName;
        this.versionId = versionId;
        this.secretValue = secretValue;
        this.secretValueByteBuffer = secretValueByteBuffer;
        this.secretDataType = secretDataType;
        this.createTime = createTime;
    }

    public String getSecretName() {
        return secretName;
    }

    public String getVersionId() {
        return versionId;
    }

    public String getSecretValue() {
        return secretValue;
    }

    public String getSecretDataType() {
        return secretDataType;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setSecretName(String secretName) {
        this.secretName = secretName;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public void setSecretValue(String secretValue) {
        this.secretValue = secretValue;
    }

    public void setSecretDataType(String secretDataType) {
        this.secretDataType = secretDataType;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public ByteBuffer getSecretValueByteBuffer() {
        if (CacheClientConstant.BINARY_DATA_TYPE.equals(secretDataType)) {
            return ByteBufferUtils.convertStringToByte(secretValue);
        }
        return this.secretValueByteBuffer;
    }

    @Override
    public SecretInfo clone() {
        try {
            return (SecretInfo) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "SecretInfo{" +
                "secretName='" + secretName + '\'' +
                ", versionId='" + versionId + '\'' +
                ", secretValue='" + "***" + '\'' +
                ", secretDataType='" + secretDataType + '\'' +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}
