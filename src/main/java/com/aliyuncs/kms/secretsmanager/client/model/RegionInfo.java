package com.aliyuncs.kms.secretsmanager.client.model;

import com.aliyuncs.utils.StringUtils;

import java.io.Serializable;
import java.util.Objects;

/**
 * kms凭据管家调用地域信息
 */
public class RegionInfo implements Serializable {

    /**
     * region id
     */
    private String regionId;

    /**
     * 表示程序运行的网络是否为VPC网络
     */
    private boolean vpc;

    /**
     * 终端地址信息
     */
    private String endpoint;

    /**
     * kms类型
     * 0:kms
     * 1:专属KMS
     */
    private int kmsType;

    public RegionInfo() {
        // do nothing
    }

    public RegionInfo(String regionId) {
        this.regionId = regionId;
    }

    /**
     * 构建一个VPC region信息
     *
     * @param regionId region id
     * @param vpc      是否使用vpc域名
     */
    public RegionInfo(String regionId, boolean vpc) {
        this.regionId = regionId;
        this.vpc = vpc;
    }

    /**
     * 构建一个指定endpoint region信息
     *
     * @param regionId region id
     * @param endpoint 指定region endpoint
     */
    public RegionInfo(String regionId, String endpoint) {
        this.regionId = regionId;
        this.endpoint = endpoint;
    }

    public RegionInfo(String regionId, boolean vpc, String endpoint) {
        this.regionId = regionId;
        this.vpc = vpc;
        this.endpoint = endpoint;
    }

    public RegionInfo(String regionId, boolean vpc, String endpoint, int kmsType) {
        this.regionId = regionId;
        this.vpc = vpc;
        this.endpoint = endpoint;
        this.kmsType = kmsType;
    }

    public String getRegionId() {
        return regionId;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }


    public boolean getVpc() {
        return this.vpc;
    }

    public void setVpc(boolean vpc) {
        this.vpc = vpc;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public int getKmsType() {
        return kmsType;
    }

    public void setKmsType(int kmsType) {
        this.kmsType = kmsType;
    }

    @Override
    public String toString() {
        return "RegionInfo{" +
                "regionId='" + regionId + '\'' +
                ", vpc=" + vpc +
                ", endpoint='" + endpoint + '\'' +
                ", kmsType='" + kmsType + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegionInfo that = (RegionInfo) o;
        if (endpoint != null && that.endpoint != null) {
            if (endpoint.equals(that.endpoint)){
                return true;
            }
        }
        return vpc == that.vpc &&
                regionId.equals(that.regionId);
    }

    @Override
    public int hashCode() {
        if (!StringUtils.isEmpty(endpoint)) {
            return Objects.hash(endpoint);
        }
        return Objects.hash(regionId, vpc);
    }

}
