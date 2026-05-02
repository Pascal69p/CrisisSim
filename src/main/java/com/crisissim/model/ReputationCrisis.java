package com.crisissim.model;

import java.util.ArrayList;
import java.util.List;

public class ReputationCrisis extends CrisisScenario {

    public ReputationCrisis() {
        super("REP-001", "Public Relations Crisis",
                "A viral social media post is accusing your company of unethical practices.",
                "You own a coffee shop chain. A video showing an employee being rude to a customer has gone viral. " +
                        "Activists are calling for a boycott, and local news wants a statement.");

        this.startingMetrics = new Metrics(65, 30, 55, 45, 0);
        this.availableDecisions = createDecisions();
    }

    private List<Decision> createDecisions() {
        List<Decision> decisions = new ArrayList<>();

        Decision decision1 = new Decision("APO-001", "Full Public Apology",
                "CEO issues video apology, announces new training programs, offers refunds to affected customers.")
                .addImmediateEffect("reputation", 20.0)
                .addImmediateEffect("customerSatisfaction", 15.0)
                .addDelayedEffect("financialHealth", -15.0)
                .setDelayMonths(2)
                .setConsequenceMessage("The public appreciated your transparency, but the training and refunds cost significant money.");

        Decision decision2 = new Decision("DEN-001", "Deny and Defend",
                "Claim the video is edited and take legal action against the accuser.")
                .addImmediateEffect("reputation", -20.0)
                .addImmediateEffect("customerSatisfaction", -25.0)
                .addDelayedEffect("reputation", -15.0)
                .setDelayMonths(1)
                .setConsequenceMessage("The backlash intensified. Sales are dropping and employees are facing harassment.");

        Decision decision3 = new Decision("COMM-001", "Community Investment",
                "Launch a major community initiative and donate to local causes.")
                .addImmediateEffect("reputation", 15.0)
                .addImmediateEffect("employeeMorale", 10.0)
                .addDelayedEffect("reputation", 10.0)
                .setDelayMonths(1)
                .setConsequenceMessage("Community support is rebuilding slowly. The donations helped shift the conversation.");

        Decision decision4 = new Decision("WAIT-001", "Wait and See",
                "Release a brief statement and let the news cycle move on.")
                .addImmediateEffect("reputation", -5.0)
                .addDelayedEffect("reputation", -10.0)
                .addDelayedEffect("employeeMorale", -10.0)
                .setDelayMonths(2)
                .setConsequenceMessage("The story didn't die. Sales are down and employees are losing confidence.");

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
            return "The crisis is escalating. Your response will determine your company's future.";
        }

        switch (month) {
            case 1:
                return "Initial reaction is in. " + lastDecision.getConsequenceMessage();
            case 2:
                return "The PR situation continues to develop. Some effects of your strategy are showing.";
            default:
                return "Your reputation continues to evolve based on past decisions.";
        }
    }

    @Override
    public boolean isComplete(int month, Metrics currentMetrics) {
        return currentMetrics.isBankrupt() || month >= 5 || currentMetrics.getReputation() > 75;
    }
}