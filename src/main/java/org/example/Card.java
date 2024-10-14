package org.example;

public abstract class Card {
    private String type;
    private String name;

    public String getType() {
        return type;
    }

    protected void setType(String type) {
        this.type = type;
    }
    public String getName(){
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

}
