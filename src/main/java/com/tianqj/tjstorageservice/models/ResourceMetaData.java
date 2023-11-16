package com.tianqj.tjstorageservice.models;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ResourceMetaData {
    private final long lastUpdatedTime;
    private final long lastCreatedTime;
    private final long version;
    private final String resourceId;
    private final String resourceType;
}
