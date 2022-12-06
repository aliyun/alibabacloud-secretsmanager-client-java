# 阿里云凭据管家客户端系统环境变量设置 

通过以下系统环境变量设置方式使用阿里云凭据管家客户端：

* 通过使用AK访问KMS，你必须要设置如下系统环境变量 (linux):

	- export credentials\_type=ak
	- export credentials\_access\_key\_id=\<your access key id>
	- export credentials\_access\_secret=\<your access key secret>
	- export cache\_client\_region\_id=[{"regionId":"\<your region id>"}]

* 通过使用STS访问KMS，你必须要设置如下系统环境变量 (linux):

	- export credentials\_type=sts
	- export credentials\_role\_session_name=\<your role name>
	- export credentials\_role\_arn=\<your role arn>
	- export credentials\_access\_key\_id=\<your access key id>
	- export credentials\_access\_secret=\<your access key secret>
	- export cache\_client\_region\_id=[{"regionId":"\<your region id>"}]
	
* 通过使用RAM Role访问KMS，你必须要设置如下系统环境变量 (linux):

	- export credentials_type=ram\_role
	- export credentials\_role\_session\_name=\<your role name>
	- export credentials\_role\_arn=\<your role arn>
	- export credentials\_access\_key\_id=\<your access key id>
	- export credentials\_access\_secret=\<your access key secret>
	- export cache\_client\_region\_id=[{"regionId":"\<your region id>"}]

* 通过使用ECS RAM Role访问KMS，你必须要设置如下系统环境变量 (linux):

	- export credentials\_type=ecs\_ram\_role
	- export credentials\_role\_session\_name=\<your role name>
	- export cache\_client\_region\_id=[{"regionId":"\<your region id>"}]

* 通过使用Client Key访问KMS，你必须要设置如下系统环境变量 (linux):
    ```
       提示:client_key_password_from_env_variable与client_key_password_from_file_path为二选一， 
       当配置client_key_password_from_env_variable时，需在指定的环境变量中配置password； 
       当配置client_key_password_from_file_path时，需在指定的文件中配置password。
    ```
	- export credentials\_type=client\_key
	- export client\_key\_password\_from\_env\_variable=\<your client key private key password from environment variable>
	- export client\_key\_password\_from\_file\_path=\<your client key private key password from file>
	- export client\_key\_private\_key\_path=\<your client key private key file path>
	- export cache\_client\_region\_id=[{"regionId":"\<your region id>"}]

* 访问DKMS，你必须要设置如下系统环境变量 (linux):

    - export cache_client_dkms_config_info=[{"ignoreSslCerts":false,"passwordFromFilePath":"\<your password file absolute path>","clientKeyFile":"\<your client key file absolute path>","regionId":"\<your dkms region>","endpoint":"\<your dkms endpoint>","caFilePath":"\<your CA certificate file absolute path>"}]
    ```
        cache_client_dkms_config_info配置项说明:
        1. cache_client_dkms_config_info配置项为json数组，支持配置多个region实例
        2. ignoreSslCerts:是否忽略ssl证书 (true:忽略ssl证书,false:验证ssl证书)
        3. passwordFromFilePath、passwordFromFilePathName和passwordFromEnvVariable
           passwordFromFilePath和passwordFromFilePathName:client key密码配置从文件中获取，与passwordFromEnvVariable三选一
           例:当配置passwordFromFilePath:<你的client key密码文件所在的绝对路径>,需在配置的绝对路径下配置写有password的文件
           例:当配置passwordFromFilePathName:"client_key_password_from_file_path"时，
             需在环境变量中添加client_key_password_from_file_path=<你的client key密码文件所在的绝对路径>，
             以及对应写有password的文件。
           例:当配置passwordFromEnvVariable:"client_key_password_from_env_variable"时，
             需在环境变量中添加client_key_password_from_env_variable=<你的client key密码对应的环境变量名>
             以及对应的环境变量(xxx_env_variable=<your password>)。
        4. clientKeyFile:client key json文件的绝对路径
        5. regionId:地域Id
        6. endpoint:专属kms的域名地址
  		7. caFilePath:专属kms的CA证书绝对路径
    ```
    
   
  