package com.tianqj.tjstorageservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceRequest {

    private String id;
    @Getter
    private String resource;

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }
}
