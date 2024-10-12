package org.example;
public class QuestCard extends Card {
    private int stages;

    public QuestCard(int stages) {
        setType("Quest");
        this.stages = stages;
    }

    public int getStages() {
        return stages;
    }
}
