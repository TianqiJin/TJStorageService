package com.tianqj.tjstorageservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.tianqj.tjstorageservice.models.ResourceMetaData;
import com.tianqj.tjstorageservice.repositories.DynamoDbAccessor;
import com.tianqj.tjstorageservice.utils.JsonHelper;
import com.tianqj.tjstorageservice.utils.jackson.JacksonConverter;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static com.tianqj.tjstorageservice.utils.Constants.DynamoDB.PARTITION_KEY;
import static com.tianqj.tjstorageservice.utils.Constants.RESOURCE_TABLE_ACCESSOR_QUALIFIER;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CreateResourceService {

    private final JsonHelper jsonHelper;
    private final JacksonConverter jacksonConverter;
    @Qualifier(RESOURCE_TABLE_ACCESSOR_QUALIFIER)
    private final DynamoDbAccessor dynamoDbAccessor;


    public JsonNode handle(final String resourceString, final String resourceType) throws JsonProcessingException {
        JsonNode resourceJson = jsonHelper.deserializeJson(resourceString);
        final String resourceId = UUID.randomUUID().toString();
        final long currentTime = Instant.now().toEpochMilli();

        final ResourceMetaData resourceMetaData = ResourceMetaData.builder()
                .resourceId(resourceId)
                .lastCreatedTime(currentTime)
                .lastUpdatedTime(currentTime)
                .resourceType(resourceType)
                .version(1)
                .build();
        resourceJson = jsonHelper.decorateJsonNode(resourceJson, resourceMetaData);
        Map<String, AttributeValue> itemMap = jacksonConverter.jsonObjectToMap(resourceJson);
        final AttributeValue partitionKeyValue = AttributeValue.builder()
                .s(generatePartitionKey(resourceId, resourceType))
                .build();
        itemMap.put(PARTITION_KEY, partitionKeyValue);
        dynamoDbAccessor.save(itemMap);

        return resourceJson;
    }

    private String generatePartitionKey(final String resourceId, final String resourceType) {
        return String.format("%s-%s", resourceId, resourceType);
    }
}
