# Aliyun Secrets Manager Client for Java

[![GitHub version](https://badge.fury.io/gh/aliyun%2Falibabacloud-secretsmanager-client-java.svg)](https://badge.fury.io/gh/aliyun%2Falibabacloud-secretsmanager-client-java)

The Aliyun Secrets Manager Client for Java enables Java developers to easily work with Aliyun KMS Secrets. You can get started in minutes using ***Maven*** .

*Read this in other languages: [English](README.md), [简体中文](README.zh-cn.md)*


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
    <version>1.0.0</version>
</dependency>
```


## Build

Once you check out the code from GitHub, you can build it using Maven. Use the following command to build:

```
mvn clean install -DskipTests
```



## Sample Code
### Ordinary User Sample Code
* Build Secrets Manager Client by system environment variables    

```
    SecretCacheClient client = SecretCacheClientBuilder.newClient();  
    SecretInfo secretInfo = client.getSecretInfo("#secretName#");
```

*  Build Secrets Manager Client by the given parameters(accessKey, accessSecret, regionId, etc)

```
    SecretCacheClient client = SecretCacheClientBuilder.newCacheClientBuilder(
                 BaseSecretManagerClientBuilder.standard().withCredentialsProvider(CredentialsProviderUtils  
                 .withAccessKey("#accessKeyId#", "#accessKeySecret#")).withRegion("#regionId#").build()).build();  
    SecretInfo secretInfo = client.getSecretInfo("#secretName#");
```
### Particular User Sample Code
* Use custom parameters or customized implementation

```
   SecretCacheClient client = SecretCacheClientBuilder.newCacheClientBuilder(BaseSecretManagerClientBuilder.standard()  
                          .withCredentialsProvider(CredentialsProviderUtils.withAccessKey("#accessKeyId#", "#accessKeySecret#"))   
                          .withRegion("#regionId#").withBackoffStrategy(new FullJitterBackoffStrategy(3, 2000, 10000)).build())  
                          .withCacheSecretStrategy(new FileCacheSecretStoreStrategy("#cacheSecretPath#", true,"#salt#")).withRefreshSecretStrategy(new DefaultRefreshSecretStrategy("#ttlName#"))  
                           .withCacheStage("#stage#").withSecretTTL("#secretName#", 1 * 60 * 1000l).withSecretTTL("#secretName1#", 2 * 60 * 1000l).build();  
   SecretInfo secretInfo = client.getSecretInfo("#secretName#");
```

 