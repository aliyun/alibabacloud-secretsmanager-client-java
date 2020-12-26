package com.aliyuncs.kms.secretsmanager.client.utils;

import com.google.gson.GsonBuilder;

import java.io.*;

public class JsonIOUtils {

    private JsonIOUtils() {
        // do nothing
    }

    public static <T> T readObject(String filePath, String fileName, Class<T> type) {
        File file = new File(filePath + File.separatorChar + fileName);
        if (!file.exists()) {
            return null;
        }
        try (
                FileReader fileReader = new FileReader(file);
                BufferedReader reader = new BufferedReader(fileReader);
        ) {
            T json = new GsonBuilder().setPrettyPrinting().create().fromJson(reader, type);
            return json;
        } catch (IOException ex) {
            CommonLogger.getCommonLogger(CacheClientConstant.MODE_NAME).errorf("action:readObject", ex);
            throw new RuntimeException(ex);
        }
    }

    public static <T> void writeObject(String filePath, String fileName, T t) {
        String secretJSON = new GsonBuilder().setPrettyPrinting().create().toJson(t);
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(filePath + File.separatorChar + fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                CommonLogger.getCommonLogger(CacheClientConstant.MODE_NAME).errorf("action:createFile", e);
                throw new RuntimeException(e);
            }
        }
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {
            writer.write(secretJSON);
        } catch (IOException e) {
            CommonLogger.getCommonLogger(CacheClientConstant.MODE_NAME).errorf("action:writeObject", e);
            throw new RuntimeException(e);
        }
    }

    public static boolean exists(String filePath, String fileName) {
        File file = new File(filePath + File.separatorChar + fileName);
        return file.exists();
    }

    public static void delete(String filePath, String fileName) {
        File file = new File(filePath + File.separatorChar + fileName);
        file.delete();
    }
}
