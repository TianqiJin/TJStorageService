package com.tianqj.tjstorageservice.utils;

import software.amazon.awssdk.regions.Region;

import java.time.Duration;

public class Constants {
    public static final Region REGION = Region.US_WEST_2;
    public static final String LAMBDA_ROLE = "arn:aws:iam::286964110163:role/service-role/TJStorageServiceDdbToEsLambda-role-8borj42z";
    public static final String LAMBDA_ROLE_SESSION = "TJStorageServiceDdbToEsLambdaSession";

    public static final String SERVICE_NAME = "es";

    public static final String OPEN_SEARCH_DOMAIN = "search-tjstorageservice-es-qidczhss6ivpsavpdvq3pjpdam.us-west-2.es.amazonaws.com";

    public static final Duration OPEN_SEARCH_SOCKET_TIMEOUT = Duration.ofSeconds(5);
    public static final Duration OPEN_SEARCH_CONNECT_TIMEOUT = Duration.ofSeconds(5);

    public static final String RESOURCE_TABLE_QUALIFIER = "RESOURCE_TABLE_QUALIFIER";
    public static final String RESOURCE_TABLE_ACCESSOR_QUALIFIER = "RESOURCE_TABLE_ACCESSOR_QUALIFIER";

    public static class DynamoDB {
        public static final String PARTITION_KEY = "ResourceId_ResourceType";
    }

}
