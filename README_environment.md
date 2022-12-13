# System Environment Variables Setting For Aliyun Secrets Manager Client 

Use Aliyun Secrets Manager client by system environment variables with the below ways:

* Use access key to access aliyun kms, you must set the following system environment variables (for linux):

	- export credentials\_type=ak
	- export credentials\_access\_key\_id=\<your access key id>
	- export credentials\_access\_secret=\<your access key secret>
	- export cache\_client\_region\_id=[{"regionId":"\<your region id>"}]

* Use STS to access aliyun kms, you must set the following system environment variables (for linux):

	- export credentials\_type=sts
	- export credentials\_role\_session_name=\<your role name>
	- export credentials\_role\_arn=\<your role arn>
	- export credentials\_access\_key\_id=\<your access key id>
	- export credentials\_access\_secret=\<your access key secret>
	- export cache\_client\_region\_id=[{"regionId":"\<your region id>"}]

* Use RAM role to access aliyun kms, you must set the following system environment variables (for linux):

	- export credentials_type=ram\_role
	- export credentials\_role\_session\_name=\<your role name>
	- export credentials\_role\_arn=\<your role arn>
	- export credentials\_access\_key\_id=\<your access key id>
	- export credentials\_access\_secret=\<your access key secret>
	- export cache\_client\_region\_id=[{"regionId":"\<your region id>"}]

* Use ECS RAM role to access aliyun kms, you must set the following system environment variables (for linux):

	- export credentials\_type=ecs\_ram\_role
	- export credentials\_role\_session\_name=\<your role name>
	- export cache\_client\_region\_id=[{"regionId":"\<your region id>"}]

* Use client key to access aliyun kms, you must set the following system environment variables (for linux):
    ```
        tips: choose one of the two client_key_password_from_env_variable and client_key_password_from_file_path,
              When configuring client_key_password_from_env_variable, configure the password in the specified environment variable. 
              When configuring client_key_password_from_file_path, you need to configure password in the specified file.
    ```
	- export credentials\_type=client\_key
	- export client\_key\_password\_from\_env\_variable=\<your client key private key password from environment variable>
	- export client\_key\_password\_from\_file\_path=\<your client key private key password from file>
	- export client\_key\_private\_key\_path=\<your client key private key file path>
	- export cache\_client\_region\_id=[{"regionId":"\<your region id>"}]

* Access aliyun dedicated kms, you must set the following system environment variables (for linux):

    - export cache_client_dkms_config_info=[{"regionId":"\<your dkms region>","endpoint":"\<your dkms endpoint>","passwordFromEnvVariable":"your_password_env_variable","clientKeyFile":"\<your client key file absolute path>","ignoreSslCerts":false,"caFilePath":"\<your CA certificate file absolute path>"}]
    - export your_password_env_variable=\<your password>
     ```
        The details of the configuration item named cache_client_dkms_config_info:
        1. The configuration item named cache_client_dkms_config_info must be configured as a json array, you can configure multiple region instances
        2. regionId:Region id 
        3. endpoint:Domain address of dkms
        4. passwordFromFilePath and passwordFromEnvVariable
          passwordFromFilePath:The client key password configuration is obtained from the file,choose one of the two with passwordFromEnvVariable.
          e.g. while configuring passwordFromFilePath: < your password file absolute path >, you need to configure a file with password written under the configured absolute path
          passwordFromEnvVariable:The client key password configuration is obtained from the environment variable,choose one of the two with passwordFromFilePath.
          e.g. while configuring passwordFromEnvVariable: "your_password_env_variable",
               You need to add your_password_env_variable=< your client key private key password > in env.
        5. clientKeyFile:The absolute path to the client key json file
        6. ignoreSslCerts:If ignore ssl certs (true: Ignores the ssl certificate, false: Validates the ssl certificate)
  		7. caFilePath:The absolute path of the CA certificate of the dkms
    ```
    
    
  