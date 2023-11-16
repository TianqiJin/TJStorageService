package com.tianqj.tjstorageservice.utils.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tianqj.tjstorageservice.models.exceptions.JacksonConverterException;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.*;
import java.util.Map.Entry;

/**
 * Implementation of the {@link JacksonConverter}.
 */
public class JacksonConverterImpl implements JacksonConverter {
    /**
     * Maximum JSON depth.
     */
    private static final int MAX_DEPTH = 50;

    /**
     * Constructs a {@link JacksonConverterImpl}.
     */
    public JacksonConverterImpl() {}

    /**
     * Asserts the depth is not greater than {@link #MAX_DEPTH}.
     * @param depth Current JSON depth
     * @throws JacksonConverterException Depth is greater than {@link #MAX_DEPTH}
     */
    private void assertDepth(final int depth) throws JacksonConverterException {
        if (depth > MAX_DEPTH) {
            throw new JacksonConverterException("Max depth reached. The object/array has too much depth.");
        }
    }

    /**
     * Gets an DynamoDB representation of a JsonNode.
     * @param node The JSON to convert
     * @param depth Current JSON depth
     * @return DynamoDB's representation of the JsonNode
     * @throws JacksonConverterException Unknown JsonNode type or JSON is too deep
     */
    private AttributeValue getAttributeValue(final JsonNode node, final int depth) throws JacksonConverterException {
        assertDepth(depth);
        switch (node.asToken()) {
            case VALUE_STRING:
                return AttributeValue.builder().s(node.textValue()).build();
            case VALUE_NUMBER_INT:
            case VALUE_NUMBER_FLOAT:
                return AttributeValue.builder().n(node.numberValue().toString()).build();
            case VALUE_TRUE:
            case VALUE_FALSE:
                return AttributeValue.builder().bool(node.booleanValue()).build();
            case VALUE_NULL:
                return AttributeValue.builder().nul(true).build();
            case START_OBJECT:
                return AttributeValue.builder().m(jsonObjectToMap(node, depth)).build();
            case START_ARRAY:
                return AttributeValue.builder().l(jsonArrayToList(node, depth)).build();
            default:
                throw new JacksonConverterException("Unknown node type: " + node);
        }
    }

    /**
     * Converts a DynamoDB attribute to a JSON representation.
     * @param av DynamoDB attribute * @param depth Current JSON depth
     * @return JSON representation of the DynamoDB attribute
     * @throws JacksonConverterException Unknown DynamoDB type or JSON is too deep
     */
    private JsonNode getJsonNode(final AttributeValue av, final int depth) {
        assertDepth(depth);
        if (av.s() != null) {
            return JsonNodeFactory.instance.textNode(av.s());
        } else if (av.n() != null) {
            try {
                return JsonNodeFactory.instance.numberNode(Integer.parseInt(av.n()));
            } catch (final NumberFormatException e) {
                try {
                    return JsonNodeFactory.instance.numberNode(Float.parseFloat(av.n()));
                } catch (final NumberFormatException e2) {
                    // Not a number
                    throw new RuntimeException(e.getMessage());
                }
            }
        } else if (av.bool() != null) {
            return JsonNodeFactory.instance.booleanNode(av.bool());
        } else if (av.nul() != null) {
            return JsonNodeFactory.instance.nullNode();
        } else if (av.l() != null) {
            return listToJsonArray(av.l(), depth);
        } else if (av.m() != null) {
            return mapToJsonObject(av.m(), depth);
        } else {
            throw new JacksonConverterException("Unknown type value " + av);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonNode itemListToJsonArray(final List<Map<String, AttributeValue>> items) throws RuntimeException {
        if (items != null) {
            final ArrayNode array = JsonNodeFactory.instance.arrayNode();
            for (final Map<String, AttributeValue> item : items) {
                array.add(mapToJsonObject(item, 0));
            }
            return array;
        }
        throw new JacksonConverterException("Items cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AttributeValue> jsonArrayToList(final JsonNode node) throws JacksonConverterException {
        return jsonArrayToList(node, 0);
    }

    /**
     * Helper method to convert a JsonArrayNode to a DynamoDB list.
     * @param node Array node to convert * @param depth Current JSON depth
     * @return DynamoDB list representation of the array node
     * @throws JacksonConverterException JsonNode is not an array or depth is too * great
     */
    private List<AttributeValue> jsonArrayToList(final JsonNode node, final int depth) throws JacksonConverterException {
        assertDepth(depth);
        if (node != null && node.isArray()) {
            final List<AttributeValue> result = new ArrayList();
            final Iterator<JsonNode> children = node.elements();
            while (children.hasNext()) {
                final JsonNode child = children.next();
                result.add(getAttributeValue(child, depth));
            }
            return result;
        }
        throw new JacksonConverterException("Expected JSON array, but received " + node);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, AttributeValue> jsonObjectToMap(final JsonNode node) throws JacksonConverterException {
        return jsonObjectToMap(node, 0);
    }

    /**
     * Transforms a JSON object to a DynamoDB object.
     * @param node JSON object * @param depth Current JSON depth
     * @return DynamoDB object representation of JSON
     * @throws JacksonConverterException JSON is not an object or depth is too great
     */
    private Map<String, AttributeValue> jsonObjectToMap(final JsonNode node, final int depth) throws JacksonConverterException {
        assertDepth(depth);
        if (node != null && node.isObject()) {
            final Map<String, AttributeValue> result = new HashMap<>();
            final Iterator<String> keys = node.fieldNames();
            while (keys.hasNext()) {
                final String key = keys.next();
                result.put(key, getAttributeValue(node.get(key), depth + 1));
            }
            return result;
        }
        throw new JacksonConverterException("Expected JSON Object, but received " + node);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonNode listToJsonArray(final List<AttributeValue> item) throws JacksonConverterException {
        return listToJsonArray(item, 0);
    }

    /**
     * Converts a DynamoDB list to a JSON list.
     * @param item DynamoDB list
     * @param depth Current JSON depth
     * @return JSON array node representation of DynamoDB list
     * @throws JacksonConverterException Null DynamoDB list or JSON too deep
     */
    private JsonNode listToJsonArray(final List<AttributeValue> item, final int depth) throws JacksonConverterException {
        assertDepth(depth);
        if (item != null) {
            final ArrayNode node = JsonNodeFactory.instance.arrayNode();
            for (final AttributeValue value : item) {
                node.add(getJsonNode(value, depth + 1));
            }
            return node;
        }
        throw new JacksonConverterException("Item cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonNode mapToJsonObject(final Map<String, AttributeValue> item) throws JacksonConverterException {
        return mapToJsonObject(item, 0);
    }

    /**
     * Converts a DynamoDB object to a JSON map.
     * @param item DynamoDB object
     * @param depth Current JSON depth
     * @return JSON map representation of the DynamoDB object
     * @throws JacksonConverterException Null DynamoDB object or JSON too deep
     */
    private JsonNode mapToJsonObject(final Map<String, AttributeValue> item, final int depth) throws JacksonConverterException {
        assertDepth(depth);
        if (item != null) {
            final ObjectNode node = JsonNodeFactory.instance.objectNode();
            for (final Entry<String, AttributeValue> entry : item.entrySet()) {
                node.put(entry.getKey(), getJsonNode(entry.getValue(), depth + 1));
            }
            return node;
        }
        throw new JacksonConverterException("Item cannot be null");
    }
}
