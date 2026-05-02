package com.crisissim.model;

import java.util.ArrayList;
import java.util.List;

public class FinancialCrisis extends CrisisScenario {

    public FinancialCrisis() {
        super("FIN-001", "Cash Flow Crisis",
                "Your business is facing a severe cash flow shortage due to unexpected client payment delays.",
                "You run a small manufacturing company. Three major clients have delayed payments, " +
                        "and your payroll is due in two weeks. Suppliers are demanding upfront payment for raw materials.");

        this.startingMetrics = new Metrics(45, 65, 60, 62, 0);
        this.availableDecisions = createDecisions();
    }

    private List<Decision> createDecisions() {
        List<Decision> decisions = new ArrayList<>();

        // Decision 1: Aggressive cost cutting
        Decision decision1 = new Decision("CUT-001", "Aggressive Cost Cutting",
                "Lay off 15% of staff and reduce marketing to zero. Short-term savings but morale impact.")
                .addImmediateEffect("financialHealth", 15.0)
                .addImmediateEffect("employeeMorale", -20.0)
                .addDelayedEffect("reputation", -15.0)
                .addDelayedEffect("customerSatisfaction", -10.0)
                .setDelayMonths(2)
                .setConsequenceMessage("Your cost cutting worked short-term, but now quality is suffering and customers are noticing!");

        // Decision 2: Seek investors
        Decision decision2 = new Decision("INV-001", "Seek Investors",
                "Reach out to angel investors for a cash injection. Dilute ownership but buy time.")
                .addImmediateEffect("financialHealth", 30.0)
                .addImmediateEffect("reputation", 5.0)
                .addDelayedEffect("financialHealth", -10.0)
                .setDelayMonths(3)
                .setConsequenceMessage("Investors provided cash, but now they want more control over your decisions.");

        // Decision 3: Negotiate with creditors
        Decision decision3 = new Decision("NEG-001", "Negotiate Payment Plans",
                "Work with suppliers and creditors to extend payment terms. Preserves relationships.")
                .addImmediateEffect("financialHealth", 5.0)
                .addImmediateEffect("reputation", 10.0)
                .addImmediateEffect("employeeMorale", 5.0)
                .setConsequenceMessage("Creditors appreciate your transparency. You've bought valuable time!");

        // Decision 4: Rush sales with discounts
        Decision decision4 = new Decision("SAL-001", "Deep Discount Sales",
                "Offer 40% discounts to generate immediate cash flow. Short-term boost but devalues brand.")
                .addImmediateEffect("financialHealth", 25.0)
                .addImmediateEffect("customerSatisfaction", 15.0)
                .addDelayedEffect("reputation", -20.0)
                .addDelayedEffect("financialHealth", -15.0)
                .setDelayMonths(2)
                .setConsequenceMessage("Your discount strategy worked too well - now customers expect discounts forever!");

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
            return "Cash reserves are critically low. Time to make a decision about your company's future.";
        }

        switch (month) {
            case 1:
                return "Initial results are showing. " + lastDecision.getConsequenceMessage() +
                        " What's your next move?";
            case 2:
                return "The situation is evolving. Some consequences of your earlier decisions are becoming apparent.";
            default:
                return "Month " + month + " - The financial landscape continues to shift.";
        }
    }

    @Override
    public boolean isComplete(int month, Metrics currentMetrics) {
        return currentMetrics.isBankrupt() || month >= 6 || currentMetrics.getFinancialHealth() > 70;
    }
}