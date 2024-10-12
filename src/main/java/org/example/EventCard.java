package org.example;
public class EventCard extends Card {
    private String eventName;

    public EventCard(String eventName) {
        setType("Event");
        this.eventName = eventName;
    }

    public String getEventName() {
        return eventName;
    }
}
