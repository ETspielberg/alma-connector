package org.unidue.ub.libintel.almaconnector.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@RedisHash(value = "BlockedId", timeToLive = 30)
public class BlockedId {

    @Id
    private String identifier;

    private LocalDateTime timestamp;

    public BlockedId() {}

    public BlockedId(String identifier, LocalDateTime timestamp) {
        this.identifier = identifier;
        this.timestamp = timestamp;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
