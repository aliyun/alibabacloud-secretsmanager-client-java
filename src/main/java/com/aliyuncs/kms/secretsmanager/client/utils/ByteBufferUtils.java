package com.aliyuncs.kms.secretsmanager.client.utils;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class ByteBufferUtils {

    private ByteBufferUtils() {
        // do noting
    }

    /**
     * string convert to byte
     *
     * @param content
     * @return
     */
    public static ByteBuffer convertStringToByte(final String content) {
        try {
            return ByteBuffer.wrap(content.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            CommonLogger.getCommonLogger(CacheClientConstant.MODE_NAME).errorf("action:wrapContent", e);
            throw new RuntimeException(e);
        }
    }
}
