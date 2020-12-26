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

    /**
     * the secret type of the secret
     */
    private String secretType;
    /**
     * the automatic rotation of the secret
     */
    private String automaticRotation;
    /**
     * the extended config of the secret
     */
    private String extendedConfig;
    /**
     * the rotation interval time of the secret
     */
    private String rotationInterval;
    /**
     * the next rotation date of the secret
     */
    private String nextRotationDate;

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

    public SecretInfo(String secretName, String versionId, String secretValue, String secretDataType, String createTime, String secretType, String automaticRotation, String extendedConfig, String rotationInterval, String nextRotationDate) {
        this.secretName = secretName;
        this.versionId = versionId;
        this.secretValue = secretValue;
        this.secretDataType = secretDataType;
        this.createTime = createTime;
        this.secretType = secretType;
        this.automaticRotation = automaticRotation;
        this.extendedConfig = extendedConfig;
        this.rotationInterval = rotationInterval;
        this.nextRotationDate = nextRotationDate;
    }

    public SecretInfo(String secretName, String versionId, String secretValue, ByteBuffer secretValueByteBuffer, String secretDataType, String createTime) {
        this.secretName = secretName;
        this.versionId = versionId;
        this.secretValue = secretValue;
        this.secretValueByteBuffer = secretValueByteBuffer;
        this.secretDataType = secretDataType;
        this.createTime = createTime;
    }

    public SecretInfo(String secretName, String versionId, String secretValue, ByteBuffer secretValueByteBuffer, String secretDataType, String createTime, String secretType, String automaticRotation, String extendedConfig, String rotationInterval, String nextRotationDate) {
        this.secretName = secretName;
        this.versionId = versionId;
        this.secretValue = secretValue;
        this.secretValueByteBuffer = secretValueByteBuffer;
        this.secretDataType = secretDataType;
        this.createTime = createTime;
        this.secretType = secretType;
        this.automaticRotation = automaticRotation;
        this.extendedConfig = extendedConfig;
        this.rotationInterval = rotationInterval;
        this.nextRotationDate = nextRotationDate;
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

    public String getRotationInterval() {
        return this.rotationInterval;
    }
    public void setRotationInterval(String rotationInterval) {
        this.rotationInterval = rotationInterval;
    }
    public String getNextRotationDate() {
        return this.nextRotationDate;
    }
    public void setNextRotationDate(String nextRotationDate) {
        this.nextRotationDate = nextRotationDate;
    }
    public String getSecretType() {
        return this.secretType;
    }
    public void setSecretType(String secretType) {
        this.secretType = secretType;
    }
    public String getAutomaticRotation() {
        return this.automaticRotation;
    }
    public void setAutomaticRotation(String automaticRotation) {
        this.automaticRotation = automaticRotation;
    }
    public String getExtendedConfig() {
        return this.extendedConfig;
    }
    public void setExtendedConfig(String extendedConfig) {
        this.extendedConfig = extendedConfig;
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
                ", secretValue='***\'" +
                ", secretDataType='" + secretDataType + '\'' +
                ", createTime='" + createTime + '\'' +
                ", secretType='" + secretType + '\'' +
                ", automaticRotation='" + automaticRotation + '\'' +
                ", extendedConfig='" + extendedConfig + '\'' +
                ", rotationInterval='" + rotationInterval + '\'' +
                ", nextRotationDate='" + nextRotationDate + '\'' +
                '}';
    }
}
