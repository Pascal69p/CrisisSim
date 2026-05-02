package com.crisissim.model;

import java.util.List;

public abstract class CrisisScenario {
    protected String id;
    protected String name;
    protected String description;
    protected String background;
    protected List<Decision> availableDecisions;
    protected Metrics startingMetrics;

    public CrisisScenario(String id, String name, String description, String background) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.background = background;
    }

    public abstract List<Decision> getDecisionsForMonth(int month, Metrics currentMetrics);
    public abstract String getNarrativeUpdate(int month, Decision lastDecision);
    public abstract boolean isComplete(int month, Metrics currentMetrics);

    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getBackground() { return background; }
    public Metrics getStartingMetrics() { return startingMetrics; }
    public void setStartingMetrics(Metrics metrics) { this.startingMetrics = metrics; }
    public List<Decision> getAvailableDecisions() { return availableDecisions; }
    public void setAvailableDecisions(List<Decision> decisions) { this.availableDecisions = decisions; }
}