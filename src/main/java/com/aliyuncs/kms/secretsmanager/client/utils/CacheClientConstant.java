package com.aliyuncs.kms.secretsmanager.client.utils;

public interface CacheClientConstant {

    /**
     * 凭据管家产品名称
     */
    String PRODUCT_NAME = "kms";

    /**
     * 随机密钥字节长度
     */
    int RANDOM_KEY_LENGTH = 32;

    /**
     * 随机IV字节长度
     */
    int IV_LENGTH = 16;

    /**
     * 当前stage
     */
    String STAGE_ACS_CURRENT = "ACSCurrent";

    /**
     * 默认最大重试次数
     */
    long DEFAULT_RETRY_MAX_ATTEMPTS = 5L;

    /**
     * 默认重试间隔时间
     */
    long DEFAULT_RETRY_INITIAL_INTERVAL_MILLS = 2000L;

    /**
     * 默认最大等待时间
     */
    long DEFAULT_CAPACITY = 10000L;

    /**
     * 环境变量cache_client_region_id key
     */
    String ENV_CACHE_CLIENT_REGION_ID_KEY = "cache_client_region_id";

    /**
     * 环境变量credentials_type key
     */
    String ENV_CREDENTIALS_TYPE_KEY = "credentials_type";

    /**
     * 环境变量credentials_access_key_id key
     */
    String ENV_CREDENTIALS_ACCESS_KEY_ID_KEY = "credentials_access_key_id";

    /**
     * 环境变量credentials_access_secret key
     */
    String ENV_CREDENTIALS_ACCESS_SECRET_KEY = "credentials_access_secret";

    /**
     * 环境变量credentials_access_token_id key
     */
    String ENV_CREDENTIALS_ACCESS_TOKEN_ID_KEY = "credentials_access_token_id";

    /**
     * 环境变量credentials_access_token key
     */
    String ENV_CREDENTIALS_ACCESS_TOKEN_KEY = "credentials_access_token";

    /**
     * 环境变量credentials_role_session_name key
     */
    String ENV_CREDENTIALS_ROLE_SESSION_NAME_KEY = "credentials_role_session_name";

    /**
     * 环境变量credentials_role_arn key
     */
    String ENV_CREDENTIALS_ROLE_ARN_KEY = "credentials_role_arn";

    /**
     * 环境变量credentials_policy key
     */
    String ENV_CREDENTIALS_POLICY_KEY = "credentials_policy";

    /**
     * 环境变量credentials_role_name key
     */
    String ENV_CREDENTIALS_ROLE_NAME_KEY = "credentials_role_name";

    /**
     * ClientException 欠费errorCode
     */
    String CLIENT_EXCEPTION_ERROR_CODE_FORBIDDEN_IN_DEBT_OVER_DUE = "Forbidden.InDebtOverdue";

    /**
     * ClientException 欠费errorCode
     */
    String CLIENT_EXCEPTION_ERROR_CODE_FORBIDDEN_IN_DEBT = "Forbidden.InDebt";

    /**
     * 模块名称
     */
    String modeName = "CacheClient";

    /**
     * 凭据文本数据类型
     */
    String TEXT_DATA_TYPE = "text";

    /**
     * 凭据二进制数据类型
     */
    String BINARY_DATA_TYPE = "binary";
}
