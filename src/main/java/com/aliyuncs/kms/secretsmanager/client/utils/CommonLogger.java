package com.aliyuncs.kms.secretsmanager.client.utils;

import com.aliyuncs.exceptions.ClientException;
import org.slf4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonLogger implements Closeable {

    private static Map<String, CommonLogger> commonLoggerMap = new HashMap<>();
    /**
     * 有效模块
     */
    private static List<String> allowModes = new ArrayList<String>() {{
        add("CacheClient");
    }};
    private Logger logger;
    private String modeName;

    private CommonLogger(String modeName, Logger logger) {
        this.logger = logger;
        this.modeName = modeName;
    }

    /**
     * 注册logger
     *
     * @param modeName
     * @param logger
     */
    public static void registerLogger(String modeName, Logger logger) {
        if (!allowModes.contains(modeName)) {
            throw new IllegalArgumentException(String.format("the modeName [%s] is invalid", modeName));
        }
        commonLoggerMap.put(modeName, new CommonLogger(modeName, logger));
    }

    /**
     * 获取commonLogger
     *
     * @param modeName
     * @return
     */
    public static CommonLogger getCommonLogger(String modeName) {
        if (!commonLoggerMap.containsKey(modeName)) {
            throw new IllegalArgumentException(String.format("the modeName [%s] need register", modeName));
        }
        return commonLoggerMap.get(modeName);
    }

    /**
     * 是否注册commonLogger
     *
     * @param modeName
     * @return
     */
    public static boolean isRegistered(String modeName) {
        return commonLoggerMap.containsKey(modeName);
    }

    public void flush() {

    }

    public void tracef(String format, Object... parameters) {
        logger.trace(parseExceptionErrorMsg(format, parameters), parameters);
    }

    public void infof(String format, Object... parameters) {
        logger.info(parseExceptionErrorMsg(format, parameters), parameters);
    }

    public void debugf(String format, Object... parameters) {
        logger.debug(parseExceptionErrorMsg(format, parameters), parameters);
    }

    public void warnf(String format, Object... parameters) {
        logger.warn(parseExceptionErrorMsg(format, parameters), parameters);
    }

    public void errorf(String format, Object... parameters) {
        logger.error(parseExceptionErrorMsg(format, parameters), parameters);
    }

    public String parseExceptionErrorMsg(String format, Object[] parameters) {
        if (parameters != null && parameters.length > 0) {
            Object parameter = parameters[parameters.length - 1];
            if (parameter instanceof ClientException) {
                ClientException ce = (ClientException) parameter;
                format = format + String.format("\tmodeName:%s\terrorCode:%s\terrMsg:%s\terrorType:%s\terrorDescription:%s\trequestId:%s", modeName, ce.getErrCode(), ce.getErrMsg(), ce.getErrorType(), ce.getErrorDescription(), ce.getRequestId());
            } else if (parameter instanceof Throwable) {
                Throwable e = (Throwable) parameter;
                if (e.getCause() != null && e.getCause() instanceof ClientException) {
                    ClientException ce = (ClientException) e.getCause();
                    format = format + String.format("\tmodeName:%s\terrorCode:%s\terrMsg:%s\terrorType:%s\terrorDescription:%s\trequestId:%s", modeName, ce.getErrCode(), ce.getErrMsg(), ce.getErrorType(), ce.getErrorDescription(), ce.getRequestId());
                }
            }
        }
        return format;
    }

    public void close() throws IOException {

    }
}
