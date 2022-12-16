# Alibaba Cloud secrets manager client profile settings 

Build the client credentials with the configuration file (secretsmanager.properties) in the directory where the program runs:
1. Use Aliyun AK SK to access Aliyun KMS,you must set the following configuration variables

```properties
# the type of access credentials
credentials_type=ak
# AK
credentials_access_key_id=#access key id#
# SK
credentials_access_secret=#access key secret#
# the region information
cache_client_region_id=[{"regionId":"#regionId#"}]
```
2. Use Aliyun sts to access Aliyun KMS,you must set the following configuration variables

```properties
# the type of access credentials
credentials_type=sts
# role name
credentials_role_session_name=#role name#
# role arn
credentials_role_arn=#role arn#
# AK
credentials_access_key_id=#access key id#
# SK
credentials_access_secret=#access key secret#
# the region information
cache_client_region_id=[{"regionId":"#regionId#"}]
```
3. Use ram role to access Aliyun KMS,you must set the following configuration variables

```properties
# the type of access credentials
credentials_type=ram_role
# role name
credentials_role_session_name=#role name#
# role arn
credentials_role_arn=#role arn#
# AK
credentials_access_key_id=#access key id#
# SK
credentials_access_secret=#access key secret#
# the region information
cache_client_region_id=[{"regionId":"#regionId#"}]
```
4. Use ECS RAM role to access Aliyun KMS,you must set the following configuration variables

```properties
# the type of access credentials
credentials_type=ecs_ram_role
# ECS RAM Role name
credentials_role_name=#credentials_role_name#
# the region information
cache_client_region_id=[{"regionId":"#regionId#"}]
```

5. Use Client Key to access Aliyun KMS,you must set the following configuration variables

```properties
# the type of access credentials
credentials_type=client_key

# you could read the password of client key from environment variable or file
client_key_password_from_env_variable=#your client key private key password environment variable name#


client_key_password_from_file_path=#your client key private key password file path#
# the private key file path of the Client Key
client_key_private_key_path=#your client key private key file path#

# the region related to the kms service
cache_client_region_id=[{"regionId":"#regionId#"}]
```

6. Access aliyun dedicated kms,you must set the following configuration variables

```properties
 cache_client_dkms_config_info=[{"regionId":"<your dkms region>","endpoint":"<your dkms endpoint>","passwordFromFilePath":"< your password file path >","clientKeyFile":"<your client key file path>","ignoreSslCerts":false,"caFilePath":"<your CA certificate file path>"}]
```
```
    The details of the configuration item named cache_client_dkms_config_info:
    1. The configuration item named cache_client_dkms_config_info must be configured as a json array, you can configure multiple region instances
    2. regionId:Region id 
    3. endpoint:Domain address of dkms
    4. passwordFromFilePath and passwordFromEnvVariable
      passwordFromFilePath:The client key password configuration is obtained from the file,choose one of the two with passwordFromEnvVariable.
      e.g. while configuring passwordFromFilePath: < your password file path >, you need to configure a file with password written under the configured path
      passwordFromEnvVariable:The client key password configuration is obtained from the environment variable,choose one of the two with passwordFromFilePath.
      e.g. while configuring passwordFromEnvVariable: "your_password_env_variable",
           You need to add your_password_env_variable=< your client key private key password > in env.
    5. clientKeyFile:The path to the client key json file
    6. ignoreSslCerts:If ignore ssl certs (true: Ignores the ssl certificate, false: Validates the ssl certificate)
    7. caFilePath:The path of the CA certificate of the dkms
```