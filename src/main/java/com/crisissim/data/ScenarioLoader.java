package com.crisissim.data;

import com.crisissim.model.*;

import java.util.ArrayList;
import java.util.List;

public class ScenarioLoader {

    public static List<CrisisScenario> loadAllScenarios() {
        List<CrisisScenario> scenarios = new ArrayList<>();
        scenarios.add(new FinancialCrisis());
        scenarios.add(new ReputationCrisis());
        scenarios.add(new OperationalCrisis());
        return scenarios;
    }

    public static CrisisScenario getScenarioById(String id) {
        for (CrisisScenario scenario : loadAllScenarios()) {
            if (scenario.getId().equals(id)) {
                return scenario;
            }
        }
        return null;
    }
}