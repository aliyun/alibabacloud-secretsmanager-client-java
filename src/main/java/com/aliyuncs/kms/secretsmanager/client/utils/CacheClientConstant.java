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
    String MODE_NAME = "CacheClient";

    /**
     * 凭据文本数据类型
     */
    String TEXT_DATA_TYPE = "text";

    /**
     * 凭据二进制数据类型
     */
    String BINARY_DATA_TYPE = "binary";

    /**
     * 项目版本
     */
    String PROJECT_VERSION = "1.1.8";

    /**
     * the user agent of secrets manager java
     */
    String USER_AGENT_OF_SECRETS_MANAGER_JAVA = "alibabacloud-secretsmanager-java";

    /**
     * the default expiration time duration of ram role
     */
    int DEFAULT_ROLE_SESSION_DURATION_SECONDS = 3600;

    /**
     * 环境变量region中endPoint key
     */
    String ENV_REGION_ENDPOINT_NAME_KEY = "endpoint";

    /**
     * 环境变量region中regionId key
     */
    String ENV_REGION_REGION_ID_NAME_KEY = "regionId";

    /**
     * 环境变量region中regionId key
     */
    String ENV_REGION_VPC_NAME_KEY = "vpc";

    /**
     * KMS服务Socket连接超时错误码
     */
    String SDK_READ_TIMEOUT = "SDK.ReadTimeout";

    /**
     * KMS服务无法连接错误码
     */
    String SDK_SERVER_UNREACHABLE = "SDK.ServerUnreachable";

    /**
     * 请求等待时间
     */
    long REQUEST_WAITING_TIME = 10 * 60 * 1000L;

    /**
     * 监控间隔时间
     */
    long MONITOR_INTERVAL = 5 * 60 * 1000;
    /**
     * 环境变量credentials_client_key_password key
     */
    String DEFAULT_ENV_CLIENT_KEY_PASSWORD_NAME = "client_key_password";
    /**
     * 环境变量credentials_client_key_private_key_path key
     */
    String ENV_CLIENT_KEY_PRIVATE_KEY_PATH_NAME_KEY = "client_key_private_key_path";
    /**
     * credentials配置文件名称
     */
    String CREDENTIALS_PROPERTIES_CONFIG_NAME = "secretsmanager.properties";
    /**
     * credentials配置中属性名称secret_names
     */
    String PROPERTIES_SECRET_NAMES_KEY = "secret_names";
    /**
     * client_key_password_from_env_variable key
     */
    String ENV_CLIENT_KEY_PASSWORD_FROM_ENV_VARIABLE_NAME = "client_key_password_from_env_variable";
    /**
     * client_key_password_from_file_path key
     */
    String ENV_CLIENT_KEY_PASSWORD_FROM_FILE_PATH_NAME = "client_key_password_from_file_path";

    /**
     * 环境变量cache_client_dkms_config_info key
     */
    String CACHE_CLIENT_DKMS_CONFIG_INFO_KEY = "cache_client_dkms_config_info";
    /**
     * 环境变量cache_client_config_info key
     */
    String ENV_IGNORE_SSL_CERTS_KEY = "ignoreSSLCerts";
    /**
     * 虚假的ak
     */
    String PRETEND_AK = "PRETEND_AK";
    /**
     * 虚假的sk
     */
    String PRETEND_SK = "PRETEND_SK";

    int DKMS_TYPE = 1;

    int KMS_TYPE = 0;
}
