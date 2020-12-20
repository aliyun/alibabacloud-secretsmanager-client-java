package com.aliyuncs.kms.secretsmanager.client.utils;

import com.aliyuncs.utils.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PingUtils {

    private static Pattern unixPattern = Pattern.compile("=(\\d+)(.\\d+)? ms", Pattern.CASE_INSENSITIVE);

    private static Pattern windowsPattern = Pattern.compile("=(\\d+)(.\\d+)?ms", Pattern.CASE_INSENSITIVE);

    private PingUtils() {
        // do nothing
    }

    public static double ping(String addr) {
        if (isWindows()) {
            return systemPing("ping -n 1 ", addr, windowsPattern);
        } else {
            return systemPing("ping -c 1 ", addr, unixPattern);
        }
    }

    private static double systemPing(String pingCommand, String addr, Pattern pattern) {
        BufferedReader in = null;
        try {
            Process process = Runtime.getRuntime().exec(pingCommand + addr);
            if (process == null) {
                return -1;
            }
            in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line = null;
            while ((line = in.readLine()) != null) {
                sb.append(line + "\n");
            }
            boolean finished = process.waitFor(2, TimeUnit.SECONDS);
            if (!finished) {
                return -1;
            } else {
                return getCheckResult(sb.toString(), pattern);
            }
        } catch (InterruptedException | IOException ignore) {
            CommonLogger.getCommonLogger(CacheClientConstant.MODE_NAME).errorf("action:ping", ignore);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignore) {
                }
            }
        }
        return -1;
    }

    private static double getCheckResult(String line, Pattern pattern) {
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            if (matcher.groupCount() == 2) {
                if (StringUtils.isEmpty(matcher.group(2))) {
                    return Double.parseDouble(matcher.group(1));
                }
                return Double.parseDouble(matcher.group(1) + matcher.group(2));
            } else {
                return Double.parseDouble(matcher.group(1));
            }
        }
        return -1;
    }

    private static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.indexOf("win") >= 0;
    }

}