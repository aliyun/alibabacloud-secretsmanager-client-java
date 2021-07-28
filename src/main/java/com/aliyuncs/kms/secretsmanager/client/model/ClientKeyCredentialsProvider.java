package com.aliyuncs.kms.secretsmanager.client.model;

import com.aliyuncs.auth.AlibabaCloudCredentials;
import com.aliyuncs.auth.AlibabaCloudCredentialsProvider;
import com.aliyuncs.auth.KeyPairCredentials;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;

public class ClientKeyCredentialsProvider implements AlibabaCloudCredentialsProvider {

    private KeyPairCredentials keyPairCredentials;

    public ClientKeyCredentialsProvider() {
    }

    public ClientKeyCredentialsProvider(KeyPairCredentials keyPairCredentials) {
        this.keyPairCredentials = keyPairCredentials;
    }

    @Override
    public AlibabaCloudCredentials getCredentials() throws ClientException, ServerException {
        return this.keyPairCredentials;
    }
}
