package com.crisissim.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SimulationHistory {
    private String id;
    private String crisisType;
    private LocalDateTime timestamp;
    private List<Metrics> metricsHistory;
    private List<String> decisionTitles;
    private String outcome;
    private boolean success;

    // Default constructor
    public SimulationHistory() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
        this.metricsHistory = new ArrayList<>();
        this.decisionTitles = new ArrayList<>();
    }

    // Constructor with parameters
    public SimulationHistory(String crisisType, List<Metrics> metricsHistory,
                             List<Decision> decisions, String outcome, boolean success) {
        this();
        this.crisisType = crisisType;
        this.metricsHistory = new ArrayList<>(metricsHistory);
        for (Decision d : decisions) {
            this.decisionTitles.add(d.getTitle());
        }
        this.outcome = outcome;
        this.success = success;
    }

    // Alternative constructor for loading from file
    public SimulationHistory(String id, String crisisType, LocalDateTime timestamp,
                             List<Metrics> metricsHistory, List<String> decisionTitles,
                             String outcome, boolean success) {
        this.id = id;
        this.crisisType = crisisType;
        this.timestamp = timestamp;
        this.metricsHistory = metricsHistory;
        this.decisionTitles = decisionTitles;
        this.outcome = outcome;
        this.success = success;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCrisisType() { return crisisType; }
    public void setCrisisType(String crisisType) { this.crisisType = crisisType; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public List<Metrics> getMetricsHistory() { return metricsHistory; }
    public void setMetricsHistory(List<Metrics> metricsHistory) { this.metricsHistory = metricsHistory; }

    public List<String> getDecisionTitles() { return decisionTitles; }
    public void setDecisionTitles(List<String> decisionTitles) { this.decisionTitles = decisionTitles; }

    public String getOutcome() { return outcome; }
    public void setOutcome(String outcome) { this.outcome = outcome; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getSummary() {
        int finalMonth = metricsHistory.isEmpty() ? 0 : metricsHistory.size();
        double finalHealth = metricsHistory.isEmpty() ? 0 :
                metricsHistory.get(metricsHistory.size() - 1).getOverallHealth();
        return String.format("%s - %s - %d months - Overall Health: %.1f%%",
                timestamp.toString().substring(0, 19), crisisType, finalMonth, finalHealth);
    }
}