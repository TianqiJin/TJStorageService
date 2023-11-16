package com.tianqj.tjstorageservice.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tianqj.tjstorageservice.models.ResourceMetaData;
import lombok.experimental.UtilityClass;

public class JsonHelper {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public JsonNode deserializeJson(final String jsonInStr) throws JsonProcessingException {
        return OBJECT_MAPPER.readTree(jsonInStr);
    }

    public JsonNode decorateJsonNode(final JsonNode jsonNode, final ResourceMetaData resourceMetaData) {
        if (!jsonNode.isObject()) {
            throw new IllegalArgumentException("Expect jsonNode object");
        }
        final ObjectNode item = (ObjectNode) jsonNode;
        item.put("ResourceId", resourceMetaData.getResourceId());
        item.put("ResourceType", resourceMetaData.getResourceType());
        item.put("Version", resourceMetaData.getVersion());
        item.put("LastUpdatedTime", resourceMetaData.getLastUpdatedTime());

        return OBJECT_MAPPER.convertValue(item, JsonNode.class);
    }
}
