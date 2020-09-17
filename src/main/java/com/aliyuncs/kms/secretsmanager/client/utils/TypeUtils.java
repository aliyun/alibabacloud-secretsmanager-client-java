package com.aliyuncs.kms.secretsmanager.client.utils;

public class TypeUtils {

    private TypeUtils() {
        // do nothing
    }

    public static String parseString(Object obj) {
        if (obj != null) {
            return String.valueOf(obj);
        }
        return null;
    }

    public static boolean parseBoolean(Object obj) {
        if (obj == null) {
            return false;
        } else if (obj instanceof Boolean) {
            return (boolean) obj;
        } else if (obj instanceof String) {
            return Boolean.valueOf((String)obj);
        } else {
            throw new IllegalArgumentException("unknown obj type");
        }
    }

}
