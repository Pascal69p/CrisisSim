package com.crisissim.model;

import java.util.HashMap;
import java.util.Map;

public class Decision {
    private String id;
    private String title;
    private String description;
    private Map<String, Double> immediateEffects;
    private Map<String, Double> delayedEffects;
    private int delayMonths;
    private String consequenceMessage;

    public Decision(String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.immediateEffects = new HashMap<>();
        this.delayedEffects = new HashMap<>();
        this.delayMonths = 2;
        this.consequenceMessage = "";
    }

    // Fluent builder pattern methods
    public Decision addImmediateEffect(String metric, double change) {
        immediateEffects.put(metric, change);
        return this;
    }

    public Decision addDelayedEffect(String metric, double change) {
        delayedEffects.put(metric, change);
        return this;
    }

    public Decision setDelayMonths(int months) {
        this.delayMonths = months;
        return this;
    }

    public Decision setConsequenceMessage(String message) {
        this.consequenceMessage = message;
        return this;
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Map<String, Double> getImmediateEffects() { return immediateEffects; }
    public Map<String, Double> getDelayedEffects() { return delayedEffects; }
    public int getDelayMonths() { return delayMonths; }
    public String getConsequenceMessage() { return consequenceMessage; }
}