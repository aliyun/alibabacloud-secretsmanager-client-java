# 阿里云凭据管家客户端系统环境变量设置 

通过以下系统环境变量设置方式使用阿里云凭据管家客户端：

* 通过使用AK访问KMS, 你必须要设置如下系统环境变量 (linux):

	- export credentials\_type=ak
	- export credentials\_access\_key\_id=\<your access key id>
	- export credentials\_access\_secret=\<your access key secret>
	- export cache\_client\_region\_id=[{"regionId":"\<your region id>"}]

* 通过使用STS访问KMS, 你必须要设置如下系统环境变量 (linux):

	- export credentials\_type=sts
	- export credentials\_role\_session_name=\<your role name>
	- export credentials\_role\_arn=\<your role arn>
	- export credentials\_access\_key\_id=\<your access key id>
	- export credentials\_access\_secret=\<your access key secret>
	- export cache\_client\_region\_id=[{"regionId":"\<your region id>"}]
	
* 通过使用RAM Role访问KMS, 你必须要设置如下系统环境变量 (linux):

	- export credentials_type=ram\_role
	- export credentials\_role\_session\_name=\<your role name>
	- export credentials\_role\_arn=\<your role arn>
	- export credentials\_access\_key\_id=\<your access key id>
	- export credentials\_access\_secret=\<your access key secret>
	- export cache\_client\_region\_id=[{"regionId":"\<your region id>"}]

* 通过使用ECS RAM Role访问KMS, 你必须要设置如下系统环境变量 (linux):

	- export credentials\_type=ecs\_ram\_role
	- export credentials\_role\_session\_name=\<your role name>
	- export cache\_client\_region\_id=[{"regionId":"\<your region id>"}]

* 通过使用Client Key访问KMS, 你必须要设置如下系统环境变量 (linux):

	- export credentials\_type=client\_key
	- export client\_key\_password\_from\_env\_variable=\<your client key private key password from environment variable>
	- export client\_key\_password\_from\_file\_path=\<your client key private key password from file>
	- export client\_key\_private\_key\_path=\<your client key private key file path>
	- export cache\_client\_region\_id=[{"regionId":"\<your region id>"}]