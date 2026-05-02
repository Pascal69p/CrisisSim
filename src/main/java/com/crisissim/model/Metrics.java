package com.crisissim.model;

public class Metrics {
    private double financialHealth;    // 0-100
    private double reputation;          // 0-100
    private double employeeMorale;      // 0-100
    private double customerSatisfaction; // 0-100
    private int month;

    public Metrics() {
        this.financialHealth = 70.0;
        this.reputation = 70.0;
        this.employeeMorale = 70.0;
        this.customerSatisfaction = 70.0;
        this.month = 0;
    }

    public Metrics(double financialHealth, double reputation,
                   double employeeMorale, double customerSatisfaction, int month) {
        this.financialHealth = financialHealth;
        this.reputation = reputation;
        this.employeeMorale = employeeMorale;
        this.customerSatisfaction = customerSatisfaction;
        this.month = month;
    }

    // Getters and Setters
    public double getFinancialHealth() { return financialHealth; }
    public void setFinancialHealth(double financialHealth) {
        this.financialHealth = Math.max(0, Math.min(100, financialHealth));
    }

    public double getReputation() { return reputation; }
    public void setReputation(double reputation) {
        this.reputation = Math.max(0, Math.min(100, reputation));
    }

    public double getEmployeeMorale() { return employeeMorale; }
    public void setEmployeeMorale(double employeeMorale) {
        this.employeeMorale = Math.max(0, Math.min(100, employeeMorale));
    }

    public double getCustomerSatisfaction() { return customerSatisfaction; }
    public void setCustomerSatisfaction(double customerSatisfaction) {
        this.customerSatisfaction = Math.max(0, Math.min(100, customerSatisfaction));
    }

    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }

    public double getOverallHealth() {
        return (financialHealth + reputation + employeeMorale + customerSatisfaction) / 4;
    }

    public boolean isBankrupt() {
        return financialHealth <= 0 || getOverallHealth() < 15;
    }

    public Metrics copy() {
        return new Metrics(financialHealth, reputation, employeeMorale, customerSatisfaction, month);
    }

    @Override
    public String toString() {
        return String.format("Month %d - Financial: %.1f, Rep: %.1f, Morale: %.1f, Customer: %.1f",
                month, financialHealth, reputation, employeeMorale, customerSatisfaction);
    }
}