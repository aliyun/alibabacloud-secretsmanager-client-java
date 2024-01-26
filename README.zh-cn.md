![](https://aliyunsdk-pages.alicdn.com/icons/AlibabaCloud.svg)

# 阿里云凭据管家Java客户端
[![GitHub version](https://badge.fury.io/gh/aliyun%2Falibabacloud-secretsmanager-client-java.svg)](https://badge.fury.io/gh/aliyun%2Falibabacloud-secretsmanager-client-java)
[![Build Status](https://travis-ci.org/aliyun/alibabacloud-secretsmanager-client-java.svg?branch=master)](https://travis-ci.org/aliyun/alibabacloud-secretsmanager-client-java)

阿里云凭据管家Java客户端可以使Java开发者快速使用阿里云凭据。你可以通过***Maven***快速使用。

*其他语言版本: [English](README.md), [简体中文](README.zh-cn.md)*

- [阿里云凭据管家Java客户端主页](https://help.aliyun.com/document_detail/190269.html?spm=a2c4g.11186623.6.621.201623668WpoMj)
- [Issues](https://github.com/aliyun/alibabacloud-secretsmanager-client-java/issues)
- [Release](https://github.com/aliyun/alibabacloud-secretsmanager-client-java/releases)

## 许可证

[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html)


## 优势
* 支持用户快速集成获取凭据信息
* 支持阿里云凭据管家内存和文件两种缓存凭据机制
* 支持凭据名称相同场景下的跨地域容灾
* 支持默认规避策略和用户自定义规避策略

## 软件要求

- Java 1.8 或以上版本
- Maven

## 安装

可以通过Maven的方式在项目中使用凭据管家Java客户端。导入方式如下:

```
<dependency>
    <groupId>com.aliyun</groupId>
    <artifactId>alibabacloud-secretsmanager-client</artifactId>
    <version>1.3.7</version>
</dependency>
<dependency>
  <groupId>com.aliyun</groupId>
  <artifactId>aliyun-java-sdk-core</artifactId>
  <version>4.5.17</version>
</dependency>
```


## 构建

你可以从Github检出代码通过下面的maven命令进行构建。

```
mvn clean install -DskipTests -Dgpg.skip=true
```

## 示例代码
### 一般用户代码
* 通过系统环境变量或配置文件(secretsmanager.properties)构建客户端([系统环境变量设置详情](README_environment.zh-cn.md)、[配置文件设置详情](README_config.zh-cn.md))

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

* 通过自定义配置文件(可自定义文件名称或文件路径名称)构建客户端([配置文件设置详情](README_config.zh-cn.md))

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

* 通过指定参数(accessKey、accessSecret、regionId等)构建客户端

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

### 定制化用户代码
* 使用自定义参数或用户自己实现

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

 
