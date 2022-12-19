package com.aliyuncs.kms.secretsmanager.client.utils;


import java.io.*;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigUtils {

    private ConfigUtils() {
        // do nothing
    }

    public static Properties loadConfig(String configName) {
        File file = getFileByPath(configName);
        try (InputStream in = new FileInputStream(file)) {
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

    public static File getFileByPath(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            String path = ConfigUtils.class.getClassLoader().getResource("").getPath();
            if (!(file = new File(path + filePath)).exists()) {
                path = Paths.get(filePath).toAbsolutePath().toString();
                if (!(file = new File(path)).exists()) {
                    return null;
                }
            }
        }
        return file;
    }

    public static String readFileContent(String filePath) {
        File file = getFileByPath(filePath);
        if (file == null || !file.exists()) {
            return null;
        }
        try (
                FileReader fileReader = new FileReader(file);
                BufferedReader reader = new BufferedReader(fileReader);
        ) {
            String content = "";
            String line;
            while ((line = reader.readLine()) != null) {
                content += line;
            }
            return content;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
