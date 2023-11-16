package com.tianqj.tjstorageservice.configuration;

import com.tianqj.tjstorageservice.interceptors.AwsRequestSigningInterceptor;
import com.tianqj.tjstorageservice.repositories.DynamoDbAccessor;
import com.tianqj.tjstorageservice.utils.Constants;
import com.tianqj.tjstorageservice.utils.JsonHelper;
import com.tianqj.tjstorageservice.utils.jackson.JacksonConverter;
import com.tianqj.tjstorageservice.utils.jackson.JacksonConverterImpl;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.signer.Aws4Signer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleCredentialsProvider;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;

import static com.tianqj.tjstorageservice.utils.Constants.*;

@Configuration
public class AppConfig {

    private static final Region REGION = Region.US_EAST_1;


    @Bean
    public StsClient getStsClient() {
        return StsClient.builder()
                .region(REGION)
                .build();
    }

    @Bean
    public StsAssumeRoleCredentialsProvider getStsAssumeRoleCredentialsProvider(final StsClient stsClient) {
        final String rolaArn = "";
        AssumeRoleRequest assumeRoleRequest = AssumeRoleRequest.builder()
                .roleArn(rolaArn)
                .roleSessionName("TJStorageServiceRoleSession")
                .build();

        return StsAssumeRoleCredentialsProvider.builder()
                .stsClient(stsClient)
                .refreshRequest(() -> assumeRoleRequest)
                .build();
    }

    @Bean
    public DynamoDbClient getDynamodbClient(final StsAssumeRoleCredentialsProvider credentialsProvider) {
        return DynamoDbClient.builder()
                .credentialsProvider(credentialsProvider)
                .region(REGION)
                .build();
    }

    @Bean
    public Aws4Signer getAws4Signer() {
        return Aws4Signer.create();
    }


    @Bean
    public HttpRequestInterceptor getHttpRequestInterceptor(final Aws4Signer signer,
                                                            final AwsCredentialsProvider awsCredentialsProvider) {
        return new AwsRequestSigningInterceptor(
                SERVICE_NAME,
                signer,
                awsCredentialsProvider,
                Constants.REGION
        );
    }

    @Bean
    public RestHighLevelClient getRestHighLevelClient(final HttpRequestInterceptor httpRequestInterceptor) {
        return new RestHighLevelClient(
                RestClient.builder(new HttpHost(Constants.OPEN_SEARCH_DOMAIN, -1, "https"))
                        .setHttpClientConfigCallback(builder -> {
                            builder.useSystemProperties().addInterceptorLast(httpRequestInterceptor);
                            return builder;
                        })
                        .setRequestConfigCallback(requestConfigBuilder -> {
                            requestConfigBuilder
                                    .setConnectTimeout(
                                            Long.valueOf(Constants.OPEN_SEARCH_CONNECT_TIMEOUT.toMillis()).intValue())
                                    .setSocketTimeout(
                                            Long.valueOf(Constants.OPEN_SEARCH_SOCKET_TIMEOUT.toMillis()).intValue())
                                    .build();
                            return requestConfigBuilder;
                        }));
    }

    @Bean
    public JsonHelper getJsonHelper() {
        return new JsonHelper();
    }

    @Bean
    public JacksonConverter getJacksonConverter() {
        return new JacksonConverterImpl();
    }

    @Bean
    @Qualifier(RESOURCE_TABLE_QUALIFIER)
    public String getDynamoDBTableName() {
        return "ResourceTable";
    }

    @Bean
    @Qualifier(RESOURCE_TABLE_ACCESSOR_QUALIFIER)
    public DynamoDbAccessor getDynamoDbAccessor(final String tableName, final DynamoDbClient dynamoDbClient) {
        return new DynamoDbAccessor(tableName, dynamoDbClient);
    }



}
