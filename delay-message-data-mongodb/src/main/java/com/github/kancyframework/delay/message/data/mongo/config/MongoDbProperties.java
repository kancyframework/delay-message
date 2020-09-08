package com.github.kancyframework.delay.message.data.mongo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * MongoDbProperties
 *
 * @author kancy
 * @date 2020/7/25 16:22
 */
@ConfigurationProperties(prefix = "delay.message.mongo")
public class MongoDbProperties {
    /**
     * Mongo database URI. Cannot be set with host, port and credentials.
     */
    private String uri;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
