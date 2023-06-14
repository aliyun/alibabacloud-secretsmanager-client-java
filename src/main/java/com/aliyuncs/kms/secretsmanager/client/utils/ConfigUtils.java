package com.aliyuncs.kms.secretsmanager.client.utils;


import java.io.*;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigUtils {

    private ConfigUtils() {
        // do nothing
    }

    public static Properties loadConfig(String configName) {
        File file = getFileByPath(configName);
        Properties properties = new Properties();
        if (file == null) {
            try (InputStream in = ConfigUtils.class.getClassLoader().getResourceAsStream(configName)) {
                if (in == null) {
                    return null;
                }
                properties.load(in);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try (InputStream in = new FileInputStream(file)) {
                properties.load(in);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return properties;
    }

    public static File getFileByPath(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            URL resource = ConfigUtils.class.getClassLoader().getResource("");
            String path = "";
            if(resource != null){
                path = resource.getPath();
            }
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
            try (InputStream in = ConfigUtils.class.getClassLoader().getResourceAsStream(filePath);
                 BufferedReader reader = in == null ? null : new BufferedReader(new InputStreamReader(in))
            ) {
                return readContent(reader);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                return readContent(reader);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private static String readContent(BufferedReader reader) throws IOException {
        if (reader == null) {
            return null;
        }
        String content = "";
        String line;
        while ((line = reader.readLine()) != null) {
            content += line;
        }
        return content;
    }
}
