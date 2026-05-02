package com.crisissim.model;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private CrisisScenario currentCrisis;
    private List<Metrics> metricsHistory;
    private List<Decision> decisionHistory;
    private Metrics currentMetrics;
    private int currentMonth;
    private boolean isGameOver;
    private String gameOverReason;

    public GameState() {
        this.metricsHistory = new ArrayList<>();
        this.decisionHistory = new ArrayList<>();
        this.currentMonth = 0;
        this.isGameOver = false;
    }

    public void startCrisis(CrisisScenario crisis) {
        this.currentCrisis = crisis;
        this.currentMetrics = crisis.getStartingMetrics().copy();
        this.currentMonth = 0;
        this.isGameOver = false;
        this.metricsHistory.clear();
        this.decisionHistory.clear();
        recordMetrics();
    }

    public void makeDecision(Decision decision) {
        if (isGameOver) return;

        // Apply immediate effects
        applyEffects(decision.getImmediateEffects());

        // Store decision for delayed effects
        decisionHistory.add(decision);
        currentMonth++;
        currentMetrics.setMonth(currentMonth);

        recordMetrics();

        // Check game over conditions
        if (currentMetrics.isBankrupt()) {
            isGameOver = true;
            gameOverReason = "Your business has gone bankrupt! Financial health dropped to zero.";
        } else if (currentCrisis.isComplete(currentMonth, currentMetrics)) {
            isGameOver = true;
            if (currentMetrics.getOverallHealth() >= 70) {
                gameOverReason = "SUCCESS! Your business has weathered the crisis and emerged stronger!";
            } else if (currentMetrics.getOverallHealth() <= 30) {
                gameOverReason = "FAILURE: Your business couldn't recover from the crisis.";
            } else {
                gameOverReason = "The crisis period has ended. Your business survived but with mixed results.";
            }
        }
    }

    public void processDelayedEffects() {
        // Process delayed effects from decisions made 2-3 months ago
        for (int i = 0; i < decisionHistory.size(); i++) {
            Decision decision = decisionHistory.get(i);
            int monthsSinceDecision = currentMonth - i;

            if (monthsSinceDecision == decision.getDelayMonths()) {
                applyEffects(decision.getDelayedEffects());
                recordMetrics();
            }
        }
    }

    private void applyEffects(java.util.Map<String, Double> effects) {
        for (java.util.Map.Entry<String, Double> effect : effects.entrySet()) {
            switch (effect.getKey()) {
                case "financialHealth":
                    currentMetrics.setFinancialHealth(currentMetrics.getFinancialHealth() + effect.getValue());
                    break;
                case "reputation":
                    currentMetrics.setReputation(currentMetrics.getReputation() + effect.getValue());
                    break;
                case "employeeMorale":
                    currentMetrics.setEmployeeMorale(currentMetrics.getEmployeeMorale() + effect.getValue());
                    break;
                case "customerSatisfaction":
                    currentMetrics.setCustomerSatisfaction(currentMetrics.getCustomerSatisfaction() + effect.getValue());
                    break;
            }
        }
    }

    private void recordMetrics() {
        metricsHistory.add(currentMetrics.copy());
    }

    public List<Decision> getCurrentDecisions() {
        if (isGameOver) return new ArrayList<>();
        return currentCrisis.getDecisionsForMonth(currentMonth + 1, currentMetrics);
    }

    public String getNarrativeUpdate() {
        Decision lastDecision = decisionHistory.isEmpty() ? null :
                decisionHistory.get(decisionHistory.size() - 1);
        return currentCrisis.getNarrativeUpdate(currentMonth, lastDecision);
    }

    // Getters
    public CrisisScenario getCurrentCrisis() { return currentCrisis; }
    public List<Metrics> getMetricsHistory() { return metricsHistory; }
    public List<Decision> getDecisionHistory() { return decisionHistory; }
    public Metrics getCurrentMetrics() { return currentMetrics; }
    public int getCurrentMonth() { return currentMonth; }
    public boolean isGameOver() { return isGameOver; }
    public String getGameOverReason() { return gameOverReason; }
}