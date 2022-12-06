# 阿里云托管凭据客户端配置文件设置 

在程序运行目录下，通过配置文件（secretsmanager.properties）构建客户端：
1. 采用阿里云AK SK作为访问鉴权方式

```properties
# 访问凭据类型
credentials_type=ak
# AK
credentials_access_key_id=#access key id#
# SK
credentials_access_secret=#access key secret#
# 关联的KMS服务地域
cache_client_region_id=[{"regionId":"#regionId#"}]
```
2. 采用STS作为访问鉴权方式

```properties
# 访问凭据类型
credentials_type=sts
# 角色名称
credentials_role_session_name=#role name#
# 资源短名称
credentials_role_arn=#role arn#
# AK
credentials_access_key_id=#access key id#
# SK
credentials_access_secret=#access key secret#
# 关联的KMS服务地域
cache_client_region_id=[{"regionId":"#regionId#"}]
```
3. 采用阿里云ECS Ram Role作为访问鉴权方式

```properties
# 访问凭据类型
credentials_type=ram_role
# 角色名称
credentials_role_session_name=#role name#
# 资源短名称
credentials_role_arn=#role arn#
# AK
credentials_access_key_id=#access key id#
# SK
credentials_access_secret=#access key secret#
# 关联的KMS服务地域
cache_client_region_id=[{"regionId":"#regionId#"}]
```
4. 采用阿里云ECS Ram Role作为访问鉴权方式

```properties
# 访问凭据类型
credentials_type=ecs_ram_role
# ECS RAM Role名称
credentials_role_name=#credentials_role_name#
# 关联的KMS服务地域
cache_client_region_id=[{"regionId":"#regionId#"}]
```

5. 采用阿里云Client Key作为访问鉴权方式

```properties
# 访问凭据类型
credentials_type=client_key

# 读取client key的解密密码：支持从环境变量或者文件读取
client_key_password_from_env_variable=#your client key private key password environment variable name#
client_key_password_from_file_path=#your client key private key password file path#

# Client Key私钥文件路径
client_key_private_key_path=#your client key private key file path#

# 关联的KMS服务地域
cache_client_region_id=[{"regionId":"#regionId#"}]
```

6. 访问专属kms服务:
```properties
cache_client_dkms_config_info=[{"ignoreSslCerts":false,"passwordFromFilePathName":"client_key_password_from_file_path","clientKeyFile":"\<your client key file absolute path>","regionId":"\<your dkms region>","endpoint":"\<your dkms endpoint>"}]
# client_key_password_from_file_path=<你的client key密码文件所在的绝对路径>
```
```
    cache_client_dkms_config_info配置项说明:
    1. cache_client_dkms_config_info配置项为json数组，支持配置多个region实例
    2. ignoreSslCerts:是否忽略ssl证书 (true:忽略ssl证书,false:验证ssl证书)
    3. passwordFromFilePath、passwordFromFilePathName和passwordFromEnvVariable
       passwordFromFilePath和passwordFromFilePathName:client key密码配置从文件中获取，与passwordFromEnvVariable三选一
       例:当配置passwordFromFilePath:<你的client key密码文件所在的绝对路径>,需在配置的绝对路径下配置写有password的文件
       例:当配置passwordFromFilePathName:"client_key_password_from_file_path"时，
         需在配置文件中添加属性client_key_password_from_file_path=<你的client key密码文件所在的绝对路径>，
         以及对应写有password的文件。
       例:当配置passwordFromEnvVariable:"client_key_password_from_env_variable"时，
        需在环境变量中添加client_key_password_from_env_variable=<你的client key密码对应的环境变量名>
        以及对应的环境变量(xxx_env_variable=<your password>)。
    4. clientKeyFile:client key json文件的绝对路径
    5. regionId:地域Id
    6. endpoint:专属kms的域名地址
```