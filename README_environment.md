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

	- export credentials\_type=client\_key
	- export client\_key\_password\_from\_env\_variable=\<your client key private key password from environment variable>
	- export client\_key\_password\_from\_file\_path=\<your client key private key password from file>
	- export client\_key\_private\_key\_path=\<your client key private key file path>
	- export cache\_client\_region\_id=[{"regionId":"\<your region id>"}]

	