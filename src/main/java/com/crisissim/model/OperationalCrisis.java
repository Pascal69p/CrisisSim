package com.crisissim.model;

import java.util.ArrayList;
import java.util.List;

public class OperationalCrisis extends CrisisScenario {

    public OperationalCrisis() {
        super("OPS-001", "Supply Chain Disruption",
                "Your main supplier has gone bankrupt, cutting off critical components.",
                "You operate an electronics assembly business. Your primary component supplier just filed for bankruptcy. " +
                        "You have 3 weeks of inventory left. Production lines will halt without a solution.");

        this.startingMetrics = new Metrics(55, 60, 58, 65, 0);
        this.availableDecisions = createDecisions();
    }

    private List<Decision> createDecisions() {
        List<Decision> decisions = new ArrayList<>();

        Decision decision1 = new Decision("RUSH-001", "Rush New Supplier",
                "Pay premium rates to rush orders from an untested overseas supplier.")
                .addImmediateEffect("financialHealth", -20.0)
                .addImmediateEffect("customerSatisfaction", -10.0)
                .addDelayedEffect("reputation", -15.0)
                .addDelayedEffect("customerSatisfaction", -15.0)
                .setDelayMonths(2)
                .setConsequenceMessage("The new supplier has quality issues. Returns are mounting!");

        Decision decision2 = new Decision("STOCK-001", "Build Strategic Inventory",
                "Invest heavily in building 6 months of inventory from multiple sources.")
                .addImmediateEffect("financialHealth", -40.0)
                .addDelayedEffect("financialHealth", 10.0)
                .addDelayedEffect("reputation", 5.0)
                .setDelayMonths(3)
                .setConsequenceMessage("You're well-stocked, but cash is tied up in inventory. Storage costs are high.");

        Decision decision3 = new Decision("AUTO-001", "Automate Production",
                "Invest in automation to reduce dependency on external suppliers.")
                .addImmediateEffect("financialHealth", -30.0)
                .addImmediateEffect("employeeMorale", -15.0)
                .addDelayedEffect("financialHealth", 25.0)
                .addDelayedEffect("reputation", 10.0)
                .setDelayMonths(3)
                .setConsequenceMessage("Automation is paying off! Production is more reliable and cheaper.");

        Decision decision4 = new Decision("REDESIGN-001", "Redesign Product",
                "Redesign product to use available components.")
                .addImmediateEffect("financialHealth", -15.0)
                .addDelayedEffect("financialHealth", 15.0)
                .addDelayedEffect("customerSatisfaction", -5.0)
                .setDelayMonths(2)
                .setConsequenceMessage("The redesigned product is working, but some customers miss the old features.");

        decisions.add(decision1);
        decisions.add(decision2);
        decisions.add(decision3);
        decisions.add(decision4);

        return decisions;
    }

    @Override
    public List<Decision> getDecisionsForMonth(int month, Metrics currentMetrics) {
        return availableDecisions;
    }

    @Override
    public String getNarrativeUpdate(int month, Decision lastDecision) {
        if (lastDecision == null) {
            return "Production is at risk. Every day of downtime costs thousands.";
        }

        return "Your supply chain strategy is unfolding. " + lastDecision.getConsequenceMessage();
    }

    @Override
    public boolean isComplete(int month, Metrics currentMetrics) {
        return currentMetrics.isBankrupt() || month >= 6 ||
                (currentMetrics.getFinancialHealth() > 60 && month >= 4);
    }
}