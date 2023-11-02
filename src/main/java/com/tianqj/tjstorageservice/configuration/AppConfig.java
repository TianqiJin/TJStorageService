package com.tianqj.tjstorageservice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleCredentialsProvider;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;

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
    public DynamoDbEnhancedClient getDynamodbEnhancedClient(final DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient).build();
    }
}
