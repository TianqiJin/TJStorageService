package com.tianqj.tjstorageservice.repositories;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.Map;

@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class DynamoDbAccessor {
    private final String tableName;
    private final DynamoDbClient dynamoDbClient;

    public void save(final Map<String, AttributeValue> itemMap) {
        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(tableName)
                .item(itemMap)
                .build();

        dynamoDbClient.putItem(putItemRequest);
    }
}
