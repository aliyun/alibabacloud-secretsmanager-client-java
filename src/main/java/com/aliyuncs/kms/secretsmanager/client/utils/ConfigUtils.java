package com.aliyuncs.kms.secretsmanager.client.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtils {

    private ConfigUtils() {
        // do nothing
    }

    public static Properties loadConfig(String configName) {
        try (InputStream in = ConfigUtils.class.getClassLoader().getResourceAsStream(configName)) {
            if (in == null) {
                return null;
            }
            Properties properties = new Properties();
            properties.load(in);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
