package org.unidue.ub.libintel.almaconnector.model.media.elasticsearch;

import org.unidue.ub.libintel.almaconnector.model.EventType;

public class GetterAlert {

    private EventType type;

    private String message;

    private String affectedItem;

    private String affectedEvent;

    public GetterAlert withMessage(String message) {
        this.message = message;
        return this;
    }

    public GetterAlert withAffectedItem(String itemId) {
        this.affectedItem = itemId;
        return this;
    }

    public GetterAlert withAffectedEvent(String eventId) {
        this.affectedEvent = eventId;
        return this;
    }

    public GetterAlert(EventType type) {
        this.type = type;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAffectedItem() {
        return affectedItem;
    }

    public void setAffectedItem(String affectedItem) {
        this.affectedItem = affectedItem;
    }

    public String getAffectedEvent() {
        return affectedEvent;
    }

    public void setAffectedEvent(String affectedEvent) {
        this.affectedEvent = affectedEvent;
    }
}
