![](https://aliyunsdk-pages.alicdn.com/icons/AlibabaCloud.svg)

# Aliyun Secrets Manager Client for Java

[![GitHub version](https://badge.fury.io/gh/aliyun%2Falibabacloud-secretsmanager-client-java.svg)](https://badge.fury.io/gh/aliyun%2Falibabacloud-secretsmanager-client-java)
[![Build Status](https://travis-ci.org/aliyun/alibabacloud-secretsmanager-client-java.svg?branch=master)](https://travis-ci.org/aliyun/alibabacloud-secretsmanager-client-java)

The Aliyun Secrets Manager Client for Java enables Java developers to easily work with Aliyun KMS Secrets. You can get started in minutes using ***Maven*** .

*Read this in other languages: [English](README.md), [简体中文](README.zh-cn.md)*

- [Aliyun Secrets Manager Client Homepage](https://help.aliyun.com/document_detail/190269.html?spm=a2c4g.11186623.6.621.201623668WpoMj)
- [Issues](https://github.com/aliyun/alibabacloud-secretsmanager-client-java/issues)
- [Release](https://github.com/aliyun/alibabacloud-secretsmanager-client-java/releases)

## License

[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html)

## Features
* Provide quick integration capability to gain secret information
* Provide Alibaba secrets cache ( memory cache or encryption file cache )
* Provide tolerated disaster by the secrets with the same secret name and secret data in different regions
* Provide default backoff strategy and user-defined backoff strategy

## Requirements

- Java 1.8 or later
- Maven

## Install

The recommended way to use the Aliyun Secrets Manager Client for Java in your project is to consume it from Maven. Import as follows:

```
<dependency>
    <groupId>com.aliyun</groupId>
    <artifactId>alibabacloud-secretsmanager-client</artifactId>
    <version>1.3.5</version>
</dependency>
<dependency>
  <groupId>com.aliyun</groupId>
  <artifactId>aliyun-java-sdk-core</artifactId>
  <version>4.5.17</version>
</dependency>
```


## Build

Once you check out the code from GitHub, you can build it using Maven. Use the following command to build:

```
mvn clean install -DskipTests -Dgpg.skip=true
```



## Sample Code
### Ordinary User Sample Code
* Build Secrets Manager Client by system environment variables or configuration file (secretsmanager.properties) ([system environment variables setting for details](README_environment.md),[configure configuration details](README_config.md))   

```Java
import com.aliyuncs.kms.secretsmanager.client.SecretCacheClient;
import com.aliyuncs.kms.secretsmanager.client.SecretCacheClientBuilder;
import com.aliyuncs.kms.secretsmanager.client.exception.CacheSecretException;
import com.aliyuncs.kms.secretsmanager.client.model.SecretInfo;

public class CacheClientEnvironmentSample {

    public static void main(String[] args) {
        try {
            SecretCacheClient client = SecretCacheClientBuilder.newClient();
            SecretInfo secretInfo = client.getSecretInfo("#secretName#");
            System.out.println(secretInfo);
        } catch (CacheSecretException e) {
            e.printStackTrace();
        }
    }
}
```

* Build Secrets Manager Client by a custom configuration file (you can customize the file name or file path name) ([Profile Setting Details](README_config.md))

```Java
import com.aliyuncs.kms.secretsmanager.client.SecretCacheClient;
import com.aliyuncs.kms.secretsmanager.client.SecretCacheClientBuilder;
import com.aliyuncs.kms.secretsmanager.client.exception.CacheSecretException;
import com.aliyuncs.kms.secretsmanager.client.model.SecretInfo;
import com.aliyuncs.kms.secretsmanager.client.service.BaseSecretManagerClientBuilder;

public class CacheClientCustomConfigFileSample {

    public static void main(String[] args) {
        try {
            SecretCacheClient client = SecretCacheClientBuilder.newCacheClientBuilder(
                    BaseSecretManagerClientBuilder.standard().withCustomConfigFile("#customConfigFileName#").build()).build();
            SecretInfo secretInfo = client.getSecretInfo("#secretName#");
            System.out.println(secretInfo);
        } catch (CacheSecretException e) {
            System.out.println("CacheSecretException:" + e.getMessage());
        }
    }
}
```

*  Build Secrets Manager Client by the given parameters(accessKey, accessSecret, regionId, etc)

```Java
import com.aliyuncs.kms.secretsmanager.client.SecretCacheClient;
import com.aliyuncs.kms.secretsmanager.client.SecretCacheClientBuilder;
import com.aliyuncs.kms.secretsmanager.client.exception.CacheSecretException;
import com.aliyuncs.kms.secretsmanager.client.model.SecretInfo;
import com.aliyuncs.kms.secretsmanager.client.service.BaseSecretManagerClientBuilder;
import com.aliyuncs.kms.secretsmanager.client.utils.CredentialsProviderUtils;

public class CacheClientSimpleParametersSample {

    public static void main(String[] args) {
        try {
            SecretCacheClient client = SecretCacheClientBuilder.newCacheClientBuilder(
                    BaseSecretManagerClientBuilder.standard().withCredentialsProvider(CredentialsProviderUtils
                            .withAccessKey(System.getenv("#accessKeyId#"), System.getenv("#accessKeySecret#"))).withRegion("#regionId#").build()).build();
            SecretInfo secretInfo = client.getSecretInfo("#secretName#");
            System.out.println(secretInfo);
        } catch (CacheSecretException e) {
            e.printStackTrace();
        }
    }
}
```
### Particular User Sample Code
* Use custom parameters or customized implementation

```Java
import com.aliyuncs.kms.secretsmanager.client.SecretCacheClient;
import com.aliyuncs.kms.secretsmanager.client.SecretCacheClientBuilder;
import com.aliyuncs.kms.secretsmanager.client.cache.FileCacheSecretStoreStrategy;
import com.aliyuncs.kms.secretsmanager.client.exception.CacheSecretException;
import com.aliyuncs.kms.secretsmanager.client.model.SecretInfo;
import com.aliyuncs.kms.secretsmanager.client.service.BaseSecretManagerClientBuilder;
import com.aliyuncs.kms.secretsmanager.client.service.DefaultRefreshSecretStrategy;
import com.aliyuncs.kms.secretsmanager.client.service.FullJitterBackoffStrategy;
import com.aliyuncs.kms.secretsmanager.client.utils.CredentialsProviderUtils;

public class CacheClientDetailParametersSample {

    public static void main(String[] args) {
        try {
            SecretCacheClient client = SecretCacheClientBuilder.newCacheClientBuilder(BaseSecretManagerClientBuilder.standard()
                    .withCredentialsProvider(CredentialsProviderUtils.withAccessKey(System.getenv("#accessKeyId#"), System.getenv("#accessKeySecret#")))
                    .withRegion("#regionId#")
                    .withBackoffStrategy(new FullJitterBackoffStrategy(3, 2000, 10000)).build())
                    .withCacheSecretStrategy(new FileCacheSecretStoreStrategy("#cacheSecretPath#", true, "#salt#"))
                    .withRefreshSecretStrategy(new DefaultRefreshSecretStrategy("#ttlName#"))
                    .withCacheStage("#stage#")
                    .withSecretTTL("#secretName#", 1 * 60 * 1000l)
                    .withSecretTTL("#secretName1#", 2 * 60 * 1000l).build();
            SecretInfo secretInfo = client.getSecretInfo("#secretName#");
            System.out.println(secretInfo);
        } catch (CacheSecretException e) {
            e.printStackTrace();
        }
    }
}
```

 
