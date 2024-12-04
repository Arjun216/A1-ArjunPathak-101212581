package org.example;
public class EventCard extends Card {
    private final String eventName;

    public EventCard(String eventName) {
        setType("Event");
        setName(eventName);
        this.eventName = eventName;
    }

    public String getEventName() {
        return getName();
    }

    @Override
    public String toString() {
        return eventName + " (Event)";
    }
}
