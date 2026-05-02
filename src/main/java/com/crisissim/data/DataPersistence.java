package com.crisissim.data;

import com.crisissim.model.Metrics;
import com.crisissim.model.SimulationHistory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DataPersistence {
    private static final String SAVE_DIR = "saves";

    static {
        try {
            Files.createDirectories(Paths.get(SAVE_DIR));
        } catch (IOException e) {
            System.err.println("Could not create save directory: " + e.getMessage());
        }
    }

    public static void saveHistory(SimulationHistory history) throws IOException {
        String filename = SAVE_DIR + "/sim_" + history.getId() + ".txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("CRISISSIM_V1");
            writer.println(history.getId());
            writer.println(history.getCrisisType());
            writer.println(history.getTimestamp());
            writer.println(history.getOutcome());
            writer.println(history.isSuccess());

            // Save metrics history
            writer.println(history.getMetricsHistory().size());
            for (Metrics m : history.getMetricsHistory()) {
                writer.println(m.getMonth() + "," + m.getFinancialHealth() + "," +
                        m.getReputation() + "," + m.getEmployeeMorale() + "," +
                        m.getCustomerSatisfaction());
            }

            // Save decisions
            writer.println(history.getDecisionTitles().size());
            for (String d : history.getDecisionTitles()) {
                writer.println(d);
            }
        }
    }

    public static SimulationHistory loadHistory(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String version = reader.readLine();
            if (!"CRISISSIM_V1".equals(version)) {
                throw new IOException("Unknown file format");
            }

            String id = reader.readLine();
            String crisisType = reader.readLine();
            String timestampStr = reader.readLine();
            LocalDateTime timestamp = LocalDateTime.parse(timestampStr);
            String outcome = reader.readLine();
            boolean success = Boolean.parseBoolean(reader.readLine());

            int metricsCount = Integer.parseInt(reader.readLine());
            List<Metrics> metricsHistory = new ArrayList<>();
            for (int i = 0; i < metricsCount; i++) {
                String[] parts = reader.readLine().split(",");
                Metrics m = new Metrics(
                        Double.parseDouble(parts[1]),
                        Double.parseDouble(parts[2]),
                        Double.parseDouble(parts[3]),
                        Double.parseDouble(parts[4]),
                        Integer.parseInt(parts[0])
                );
                metricsHistory.add(m);
            }

            int decisionsCount = Integer.parseInt(reader.readLine());
            List<String> decisions = new ArrayList<>();
            for (int i = 0; i < decisionsCount; i++) {
                decisions.add(reader.readLine());
            }

            return new SimulationHistory(id, crisisType, timestamp, metricsHistory, decisions, outcome, success);
        }
    }

    public static List<SimulationHistory> loadAllHistories() throws IOException {
        List<SimulationHistory> histories = new ArrayList<>();
        Path saveDir = Paths.get(SAVE_DIR);

        if (Files.exists(saveDir)) {
            Files.list(saveDir)
                    .filter(path -> path.toString().endsWith(".txt"))
                    .forEach(path -> {
                        try {
                            histories.add(loadHistory(path.toString()));
                        } catch (IOException e) {
                            System.err.println("Error loading " + path + ": " + e.getMessage());
                        }
                    });
        }

        return histories;
    }

    public static void deleteHistory(String filename) throws IOException {
        Files.deleteIfExists(Paths.get(filename));
    }
}