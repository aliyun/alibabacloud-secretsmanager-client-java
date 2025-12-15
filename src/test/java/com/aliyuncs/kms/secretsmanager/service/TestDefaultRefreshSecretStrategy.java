package com.aliyuncs.kms.secretsmanager.service;

import com.aliyuncs.kms.secretsmanager.client.model.SecretInfo;
import com.aliyuncs.kms.secretsmanager.client.service.DefaultRefreshSecretStrategy;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * DefaultRefreshSecretStrategy 测试类
 */
public class TestDefaultRefreshSecretStrategy {

    private DefaultRefreshSecretStrategy strategyWithTtlProperty;
    private DefaultRefreshSecretStrategy strategyWithoutTtlProperty;

    @Before
    public void setUp() {
        strategyWithTtlProperty = new DefaultRefreshSecretStrategy("ttl");
        strategyWithoutTtlProperty = new DefaultRefreshSecretStrategy();
    }

    /**
     * 测试当 jsonTTLPropertyName 为空时，返回 -1
     */
    @Test
    public void testParseTTLWithEmptyJsonTTLPropertyName() {
        SecretInfo secretInfo = new SecretInfo();
        secretInfo.setSecretValue("{\"ttl\": 3600}");

        long result = strategyWithoutTtlProperty.parseTTL(secretInfo);
        Assert.assertEquals(-1L, result);
    }

    /**
     * 测试当 secretValue 为空字符串时，返回 -1
     */
    @Test
    public void testParseTTLWithEmptySecretValue() {
        SecretInfo secretInfo = new SecretInfo();
        secretInfo.setSecretValue("");

        long result = strategyWithTtlProperty.parseTTL(secretInfo);
        Assert.assertEquals(-1L, result);
    }

    /**
     * 测试当 secretValue 为普通字符串（非JSON）时，返回 -1
     */
    @Test
    public void testParseTTLWithPlainString() {
        SecretInfo secretInfo = new SecretInfo();
        secretInfo.setSecretValue("plain text");

        long result = strategyWithTtlProperty.parseTTL(secretInfo);
        Assert.assertEquals(-1L, result);
    }

    /**
     * 测试当 secretValue 为井号开头的字符串时，返回 -1
     */
    @Test
    public void testParseTTLWithHashPrefixString() {
        SecretInfo secretInfo = new SecretInfo();
        secretInfo.setSecretValue("#some comment");

        long result = strategyWithTtlProperty.parseTTL(secretInfo);
        Assert.assertEquals(-1L, result);
    }

    /**
     * 测试当 secretValue 为有效的JSON且包含 TTL 属性时，正确解析 TTL 值
     */
    @Test
    public void testParseTTLWithValidJsonContainingTTLProperty() {
        SecretInfo secretInfo = new SecretInfo();
        secretInfo.setSecretValue("{\"username\": \"admin\", \"password\": \"123456\", \"ttl\": 3600}");

        long result = strategyWithTtlProperty.parseTTL(secretInfo);
        Assert.assertEquals(3600L, result);
    }

    /**
     * 测试当 secretValue 为有效的JSON但不包含 TTL 属性时，返回 -1
     */
    @Test
    public void testParseTTLWithValidJsonWithoutTTLProperty() {
        SecretInfo secretInfo = new SecretInfo();
        secretInfo.setSecretValue("{\"username\": \"admin\", \"password\": \"123456\"}");

        long result = strategyWithTtlProperty.parseTTL(secretInfo);
        Assert.assertEquals(-1L, result);
    }

    /**
     * 测试当 secretValue 为无效JSON时，返回 -1
     */
    @Test
    public void testParseTTLWithInvalidJson() {
        SecretInfo secretInfo = new SecretInfo();
        secretInfo.setSecretValue("{invalid json}");

        long result = strategyWithTtlProperty.parseTTL(secretInfo);
        Assert.assertEquals(-1L, result);
    }
}