package com.aliyuncs.kms.secretsmanager.client.utils;

import java.util.Arrays;

public class ArrayUtils {

    private ArrayUtils() {
        // do nothing
    }

    public static <T> byte[] concatAll(byte[] first, byte[]... rest) {
        int totalLength = first.length;
        for (byte[] array : rest) {
            totalLength += array.length;
        }
        byte[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (byte[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

}
