package com.aliyuncs.kms.secretsmanager.client.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class PingUtils {
    /**
     * ping默认超时时间，
     */
    private final static int DEFAULT_PING_TIME_OUT = 2000;//2s

    private PingUtils() {
        // do nothing
    }

    public static boolean ping(String addr) {
        boolean reachable = false;
        try {
            InetAddress address = InetAddress.getByName(addr);
            reachable = address.isReachable(DEFAULT_PING_TIME_OUT);
        } catch (UnknownHostException ignore) {
            CommonLogger.getCommonLogger(CacheClientConstant.modeName).errorf("ping error", ignore);
        } catch (IOException ignore) {
            CommonLogger.getCommonLogger(CacheClientConstant.modeName).errorf("ping error", ignore);
        }
        return reachable;
    }
}