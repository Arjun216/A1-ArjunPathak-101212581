package org.example;
public class QuestCard extends Card {
    private int stages;

    public QuestCard(int stages) {
        setType("Quest");
        setName("Q" + stages);
        this.stages = stages;
    }

    public int getStages() {
        return stages;
    }
    @Override
    public String toString() {
        return getName() + " (Quest, " + stages + " stages)";
    }
}
