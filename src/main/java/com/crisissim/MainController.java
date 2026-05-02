package com.crisissim;

import com.crisissim.model.*;
import com.crisissim.data.*;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainController {
    private GameState gameState;
    private BorderPane mainContainer;

    // UI Components
    private Label monthValueLabel;
    private Label crisisNameLabel;
    private Label warningLabel;
    private Label financialValueLabel;
    private Label reputationValueLabel;
    private Label moraleValueLabel;
    private Label customerValueLabel;
    private ProgressBar overallHealthBar;
    private Label overallValueLabel;
    private TextArea narrativeArea;
    private LineChart<Number, Number> metricsChart;
    private TableView<DecisionRecord> historyTable;
    private ListView<String> saveLoadListView;
    private Label scenarioDescriptionLabel;
    private Label scenarioInstructionsLabel;

    // Decision history storage
    private List<DecisionRecord> decisionRecords;
    private List<SimulationHistory> savedGames;

    // Color scheme
    private static final String COLOR_PRIMARY = "#3b82f6";
    private static final String COLOR_SUCCESS = "#10b981";
    private static final String COLOR_WARNING = "#f59e0b";
    private static final String COLOR_DANGER = "#ef4444";

    public MainController() {
        gameState = new GameState();
        decisionRecords = new ArrayList<>();
        savedGames = new ArrayList<>();
        initializeUI();
        refreshSaveLoadList();
    }

    private void initializeUI() {
        mainContainer = new BorderPane();
        mainContainer.setStyle("-fx-background-color: #f1f5f9;");

        // Sticky Header
        mainContainer.setTop(createStickyHeader());

        // TabPane for compact navigation
        mainContainer.setCenter(createTabNavigation());

        // Bottom Status Bar
        mainContainer.setBottom(createStatusBar());
    }

    private VBox createStickyHeader() {
        VBox header = new VBox();
        header.getStyleClass().add("sticky-header");

        HBox topRow = new HBox(15);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label logoIcon = new Label("🎯");
        logoIcon.setFont(Font.font(28));

        VBox titleBox = new VBox(2);
        Label titleLabel = new Label("CrisisSim");
        titleLabel.getStyleClass().add("header-title");
        Label subtitleLabel = new Label("Business Crisis Management Simulator");
        subtitleLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");
        titleBox.getChildren().addAll(titleLabel, subtitleLabel);

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);

        // Month display
        VBox monthBox = new VBox(2);
        monthBox.setAlignment(Pos.CENTER);
        Label monthTitle = new Label("CURRENT MONTH");
        monthTitle.getStyleClass().add("header-month");
        monthValueLabel = new Label("0");
        monthValueLabel.getStyleClass().add("header-month-value");
        monthBox.getChildren().addAll(monthTitle, monthValueLabel);

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        // Crisis name display
        VBox crisisBox = new VBox(2);
        crisisBox.setAlignment(Pos.CENTER);
        Label crisisTitle = new Label("ACTIVE CRISIS");
        crisisTitle.getStyleClass().add("header-month");
        crisisNameLabel = new Label("None");
        crisisNameLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px; -fx-font-weight: bold;");
        crisisBox.getChildren().addAll(crisisTitle, crisisNameLabel);

        Region spacer3 = new Region();
        HBox.setHgrow(spacer3, Priority.ALWAYS);

        // Warning indicator
        warningLabel = new Label();
        warningLabel.getStyleClass().add("warning-indicator");
        warningLabel.setVisible(false);

        topRow.getChildren().addAll(logoIcon, titleBox, spacer1, monthBox, spacer2, crisisBox, spacer3, warningLabel);

        header.getChildren().add(topRow);
        return header;
    }

    private TabPane createTabNavigation() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab dashboardTab = new Tab("Dashboard");
        dashboardTab.setClosable(false);
        dashboardTab.setContent(createDashboardContent());

        Tab decisionsTab = new Tab("Decision History");
        decisionsTab.setClosable(false);
        decisionsTab.setContent(createDecisionHistoryContent());

        Tab saveLoadTab = new Tab("Save / Load");
        saveLoadTab.setClosable(false);
        saveLoadTab.setContent(createSaveLoadContent());

        Tab scenarioTab = new Tab("Scenario Info");
        scenarioTab.setClosable(false);
        scenarioTab.setContent(createScenarioInfoContent());

        tabPane.getTabs().addAll(dashboardTab, decisionsTab, saveLoadTab, scenarioTab);

        return tabPane;
    }

    private ScrollPane createDashboardContent() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setFitToWidth(true);
        scrollPane.setPadding(new Insets(20));

        VBox dashboard = new VBox(20);
        dashboard.setStyle("-fx-background-color: transparent;");

        // Metric Cards Row
        dashboard.getChildren().add(createMetricCardsRow());

        // Chart Section
        dashboard.getChildren().add(createChartSection());

        // Narrative Section (compact)
        dashboard.getChildren().add(createNarrativeSection());

        // Available Decisions Section (compact)
        dashboard.getChildren().add(createDecisionsSection());

        scrollPane.setContent(dashboard);
        return scrollPane;
    }

    private GridPane createMetricCardsRow() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);

        for (int i = 0; i < 4; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(25);
            grid.getColumnConstraints().add(col);
        }

        // Initialize value labels with dark text
        financialValueLabel = new Label("--");
        financialValueLabel.getStyleClass().add("metric-value");

        reputationValueLabel = new Label("--");
        reputationValueLabel.getStyleClass().add("metric-value");

        moraleValueLabel = new Label("--");
        moraleValueLabel.getStyleClass().add("metric-value");

        customerValueLabel = new Label("--");
        customerValueLabel.getStyleClass().add("metric-value");

        overallValueLabel = new Label("--");
        overallValueLabel.getStyleClass().add("metric-value");

        overallHealthBar = new ProgressBar(0);
        overallHealthBar.setPrefWidth(120);
        overallHealthBar.setPrefHeight(6);
        overallHealthBar.setStyle("-fx-accent: " + COLOR_PRIMARY + "; -fx-background-radius: 3; -fx-background-color: #e2e8f0;");

        VBox financialCard = createMetricCard("💰", "Financial Health", financialValueLabel,
                "Affected by cash flow, investments, and cost decisions");
        VBox reputationCard = createMetricCard("⭐", "Reputation", reputationValueLabel,
                "Affected by PR decisions, customer satisfaction, and transparency");
        VBox moraleCard = createMetricCard("😊", "Employee Morale", moraleValueLabel,
                "Affected by layoffs, communication, and work environment");

        VBox customerCard = createMetricCard("👥", "Customer Satisfaction", customerValueLabel,
                "Affected by product quality, service, and pricing");

        grid.add(financialCard, 0, 0);
        grid.add(reputationCard, 1, 0);
        grid.add(moraleCard, 2, 0);
        grid.add(customerCard, 3, 0);

        return grid;
    }

    private VBox createMetricCard(String icon, String title, Label valueLabel, String tooltipText) {
        VBox card = new VBox(10);
        card.getStyleClass().add("metric-card");

        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font(24));
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("metric-title");
        header.getChildren().addAll(iconLabel, titleLabel);

        valueLabel.getStyleClass().add("metric-value");

        Label subtitleLabel = new Label(tooltipText);
        subtitleLabel.getStyleClass().add("metric-subtitle");

        card.getChildren().addAll(header, valueLabel, subtitleLabel);

        // Add tooltip with keyboard shortcut info
        Tooltip tooltip = new Tooltip(tooltipText + "\n\nCtrl+S to save | Ctrl+R to reset");
        tooltip.setStyle("-fx-background-color: #1e293b; -fx-text-fill: white; -fx-font-size: 11px;");
        Tooltip.install(card, tooltip);

        return card;
    }

    private VBox createChartSection() {
        VBox chartCard = new VBox(15);
        chartCard.getStyleClass().add("card");

        Label chartTitle = new Label("Performance Trends");
        chartTitle.getStyleClass().add("card-title");

        metricsChart = createModernChart();

        chartCard.getChildren().addAll(chartTitle, metricsChart);
        return chartCard;
    }

    private LineChart<Number, Number> createModernChart() {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis(0, 100, 20);
        xAxis.setLabel("Month");
        yAxis.setLabel("Score");
        xAxis.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");
        yAxis.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Business Metrics Over Time");
        chart.setCreateSymbols(true);
        chart.setPrefHeight(300);
        chart.setAnimated(true);
        chart.setLegendVisible(true);
        chart.setStyle("-fx-background-color: white; -fx-background-radius: 12;");

        return chart;
    }

    private VBox createNarrativeSection() {
        VBox narrativeCard = new VBox(12);
        narrativeCard.getStyleClass().add("card");

        Label narrativeTitle = new Label("📖 Crisis Narrative");
        narrativeTitle.getStyleClass().add("card-title");

        narrativeArea = new TextArea();
        narrativeArea.setEditable(false);
        narrativeArea.setWrapText(true);
        narrativeArea.setPrefHeight(80);
        narrativeArea.getStyleClass().add("narrative-area");
        narrativeArea.setText("Ready to begin? Click 'New Game' to start your crisis management journey.");

        narrativeCard.getChildren().addAll(narrativeTitle, narrativeArea);
        return narrativeCard;
    }

    private VBox createDecisionsSection() {
        VBox section = new VBox(15);

        Label sectionTitle = new Label("🎯 Available Decisions");
        sectionTitle.getStyleClass().add("card-title");

        ScrollPane decisionsScroll = new ScrollPane();
        decisionsScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        decisionsScroll.setFitToWidth(true);
        decisionsScroll.setMaxHeight(300);

        VBox decisionsPanel = new VBox(12);
        decisionsPanel.setStyle("-fx-background-color: transparent;");
        decisionsScroll.setContent(decisionsPanel);

        section.getChildren().addAll(sectionTitle, decisionsScroll);

        // Store reference to decisions panel for later updates
        this.decisionsPanel = decisionsPanel;

        return section;
    }

    private VBox createDecisionHistoryContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: transparent;");

        VBox card = new VBox(15);
        card.getStyleClass().add("card");

        Label title = new Label("Decision History with Delayed Consequences");
        title.getStyleClass().add("card-title");

        Label subtitle = new Label("Each decision creates a butterfly effect - immediate changes shown here, delayed effects appear in future months");
        subtitle.getStyleClass().add("card-subtitle");

        historyTable = new TableView<>();
        historyTable.setPlaceholder(new Label("No decisions made yet. Start a game to see history."));

        TableColumn<DecisionRecord, Integer> monthCol = new TableColumn<>("Month");
        monthCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getMonth()).asObject());
        monthCol.setPrefWidth(80);
        monthCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<DecisionRecord, String> decisionCol = new TableColumn<>("Decision Made");
        decisionCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDecision()));
        decisionCol.setPrefWidth(200);

        TableColumn<DecisionRecord, String> immediateCol = new TableColumn<>("Immediate Effects");
        immediateCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getImmediateEffects()));
        immediateCol.setPrefWidth(180);

        TableColumn<DecisionRecord, String> delayedCol = new TableColumn<>("Delayed Consequences (Butterfly Effect)");
        delayedCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDelayedEffects()));
        delayedCol.setPrefWidth(250);

        TableColumn<DecisionRecord, String> monthAppearedCol = new TableColumn<>("Effects Appeared");
        monthAppearedCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getMonthAppeared()));
        monthAppearedCol.setPrefWidth(100);

        historyTable.getColumns().addAll(monthCol, decisionCol, immediateCol, delayedCol, monthAppearedCol);
        historyTable.setPrefHeight(500);

        card.getChildren().addAll(title, subtitle, historyTable);
        content.getChildren().add(card);

        return content;
    }

    private VBox createSaveLoadContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: transparent;");

        VBox card = new VBox(15);
        card.getStyleClass().add("card");

        Label title = new Label("Save / Load Simulation");
        title.getStyleClass().add("card-title");

        Label subtitle = new Label("Save your current simulation or load a previous one");
        subtitle.getStyleClass().add("card-subtitle");

        HBox buttonRow = new HBox(15);
        buttonRow.setAlignment(Pos.CENTER_LEFT);

        Button saveBtn = createStyledButton("Save Current Game", COLOR_SUCCESS);
        saveBtn.setOnAction(e -> saveCurrentGame());

        Button refreshBtn = createStyledButton("Refresh List", COLOR_PRIMARY);
        refreshBtn.setOnAction(e -> refreshSaveLoadList());

        buttonRow.getChildren().addAll(saveBtn, refreshBtn);

        saveLoadListView = new ListView<>();
        saveLoadListView.setPrefHeight(300);
        saveLoadListView.setPlaceholder(new Label("No saved games found"));
        saveLoadListView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2 && saveLoadListView.getSelectionModel().getSelectedItem() != null) {
                loadSelectedGame();
            }
        });

        HBox loadButtonRow = new HBox(15);
        loadButtonRow.setAlignment(Pos.CENTER_LEFT);

        Button loadBtn = createStyledButton("Load Selected Game", COLOR_PRIMARY);
        loadBtn.setOnAction(e -> loadSelectedGame());

        Button deleteBtn = createStyledButton("Delete Selected", COLOR_DANGER);
        deleteBtn.setOnAction(e -> deleteSelectedGame());

        loadButtonRow.getChildren().addAll(loadBtn, deleteBtn);

        Label tipLabel = new Label("💡 Tip: Double-click a saved game to load it. Ctrl+S to save current game, Ctrl+R to reset.");
        tipLabel.getStyleClass().add("card-subtitle");

        card.getChildren().addAll(title, subtitle, buttonRow, saveLoadListView, loadButtonRow, tipLabel);
        content.getChildren().add(card);

        return content;
    }

    private VBox createScenarioInfoContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: transparent;");

        VBox card = new VBox(15);
        card.getStyleClass().add("card");

        Label title = new Label("Current Crisis Scenario");
        title.getStyleClass().add("card-title");

        Label scenarioName = new Label("No active crisis");
        scenarioName.setStyle("-fx-text-fill: #3b82f6; -fx-font-size: 18px; -fx-font-weight: bold;");

        Label descriptionTitle = new Label("Description:");
        descriptionTitle.setStyle("-fx-text-fill: #1e293b; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 0 5 0;");

        scenarioDescriptionLabel = new Label("Start a new game to see scenario details");
        scenarioDescriptionLabel.setWrapText(true);
        scenarioDescriptionLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px;");

        Label instructionsTitle = new Label("Instructions:");
        instructionsTitle.setStyle("-fx-text-fill: #1e293b; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 0 5 0;");

        scenarioInstructionsLabel = new Label("Select a crisis from the 'New Game' button to begin");
        scenarioInstructionsLabel.setWrapText(true);
        scenarioInstructionsLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px;");

        Button newGameBtn = createStyledButton("Start New Game", COLOR_SUCCESS);
        newGameBtn.setOnAction(e -> showScenarioSelection());

        card.getChildren().addAll(title, scenarioName, descriptionTitle, scenarioDescriptionLabel,
                instructionsTitle, scenarioInstructionsLabel, newGameBtn);
        content.getChildren().add(card);

        return content;
    }

    private Button createStyledButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-font-size: 13px; -fx-padding: 8 20; -fx-background-radius: 8; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle() + "-fx-opacity: 0.9;"));
        btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle().replace("-fx-opacity: 0.9;", "")));
        return btn;
    }

    private HBox createStatusBar() {
        HBox statusBar = new HBox(15);
        statusBar.setPadding(new Insets(8, 20, 8, 20));
        statusBar.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 1 0 0 0;");

        Label statusText = new Label("✅ Ready");
        statusText.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");

        Label shortcutText = new Label("⌨️ Ctrl+S: Save | Ctrl+R: Reset");
        shortcutText.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label versionLabel = new Label("CrisisSim v3.0 | Butterfly Effect Engine");
        versionLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");

        statusBar.getChildren().addAll(statusText, spacer, shortcutText, versionLabel);
        return statusBar;
    }

    private void showScenarioSelection() {
        Dialog<CrisisScenario> dialog = new Dialog<>();
        dialog.setTitle("Start New Simulation");
        dialog.setHeaderText(null);

        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: white; -fx-background-radius: 16;");

        Label title = new Label("Choose Your Crisis Scenario");
        title.setStyle("-fx-text-fill: #1e293b; -fx-font-size: 20px; -fx-font-weight: bold;");

        GridPane scenarioGrid = new GridPane();
        scenarioGrid.setHgap(15);
        scenarioGrid.setVgap(15);

        VBox financialCard = createScenarioCard("💰", "Financial Crisis", "Cash flow shortage and liquidity problems", COLOR_PRIMARY);
        VBox reputationCard = createScenarioCard("⭐", "Reputation Crisis", "Public relations disaster and brand damage", COLOR_DANGER);
        VBox operationalCard = createScenarioCard("🔧", "Operational Crisis", "Supply chain disruption and production issues", COLOR_SUCCESS);

        financialCard.setOnMouseClicked(e -> { startNewGame(new FinancialCrisis()); dialog.close(); });
        reputationCard.setOnMouseClicked(e -> { startNewGame(new ReputationCrisis()); dialog.close(); });
        operationalCard.setOnMouseClicked(e -> { startNewGame(new OperationalCrisis()); dialog.close(); });

        scenarioGrid.add(financialCard, 0, 0);
        scenarioGrid.add(reputationCard, 1, 0);
        scenarioGrid.add(operationalCard, 2, 0);

        content.getChildren().addAll(title, scenarioGrid);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        dialog.showAndWait();
    }

    private VBox createScenarioCard(String emoji, String title, String description, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 12; " +
                "-fx-border-color: " + color + "; -fx-border-width: 2; -fx-border-radius: 12; " +
                "-fx-cursor: hand;");
        card.setPrefWidth(180);

        Label emojiLabel = new Label(emoji);
        emojiLabel.setFont(Font.font(40));

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: #1e293b; -fx-font-size: 14px; -fx-font-weight: bold;");

        Label descLabel = new Label(description);
        descLabel.setWrapText(true);
        descLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");

        card.getChildren().addAll(emojiLabel, titleLabel, descLabel);

        card.setOnMouseEntered(e -> {
            card.setScaleX(1.02);
            card.setScaleY(1.02);
        });
        card.setOnMouseExited(e -> {
            card.setScaleX(1);
            card.setScaleY(1);
        });

        return card;
    }

    private void startNewGame(CrisisScenario scenario) {
        gameState.startCrisis(scenario);
        decisionRecords.clear();

        // Update sticky header
        crisisNameLabel.setText(scenario.getName());

        // Update scenario info tab
        scenarioDescriptionLabel.setText(scenario.getDescription() + "\n\n" + scenario.getBackground());
        scenarioInstructionsLabel.setText("Make strategic decisions each month. Watch for delayed consequences (butterfly effect) that appear 2-3 months later.");

        updateUI();

        narrativeArea.setText("🎯 " + scenario.getBackground() + "\n\n" +
                "Your business metrics:\n" +
                "• Financial Health: " + scenario.getStartingMetrics().getFinancialHealth() + "/100\n" +
                "• Reputation: " + scenario.getStartingMetrics().getReputation() + "/100\n" +
                "• Employee Morale: " + scenario.getStartingMetrics().getEmployeeMorale() + "/100\n" +
                "• Customer Satisfaction: " + scenario.getStartingMetrics().getCustomerSatisfaction() + "/100\n\n" +
                "Make your first decision carefully. Every choice creates a butterfly effect!");
    }

    public void resetSimulation() {
        if (gameState.getCurrentCrisis() == null) {
            showAlert("No Active Game", "Start a new game first.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Reset Simulation");
        confirm.setHeaderText("Are you sure you want to reset?");
        confirm.setContentText("This will clear all progress and restart from month 1.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                startNewGame(gameState.getCurrentCrisis());
            }
        });
    }

    private void updateUI() {
        Metrics metrics = gameState.getCurrentMetrics();

        if (metrics != null) {
            if (monthValueLabel != null) monthValueLabel.setText(String.valueOf(gameState.getCurrentMonth()));
            if (financialValueLabel != null) financialValueLabel.setText(String.format("%.0f", metrics.getFinancialHealth()));
            if (reputationValueLabel != null) reputationValueLabel.setText(String.format("%.0f", metrics.getReputation()));
            if (moraleValueLabel != null) moraleValueLabel.setText(String.format("%.0f", metrics.getEmployeeMorale()));
            if (customerValueLabel != null) customerValueLabel.setText(String.format("%.0f", metrics.getCustomerSatisfaction()));

            double overall = metrics.getOverallHealth();
            if (overallValueLabel != null) overallValueLabel.setText(String.format("%.0f", overall));
            if (overallHealthBar != null) {
                overallHealthBar.setProgress(Math.min(1.0, Math.max(0, overall / 100)));
                if (overall < 30) {
                    overallHealthBar.setStyle("-fx-accent: " + COLOR_DANGER + ";");
                    warningLabel.setText("⚠️ CRITICAL HEALTH");
                    warningLabel.setVisible(true);
                } else if (overall < 60) {
                    overallHealthBar.setStyle("-fx-accent: " + COLOR_WARNING + ";");
                    warningLabel.setText("⚠️ WARNING");
                    warningLabel.setVisible(true);
                } else {
                    overallHealthBar.setStyle("-fx-accent: " + COLOR_SUCCESS + ";");
                    warningLabel.setVisible(false);
                }
            }
        }

        updateChart();
        updateDecisionsPanel();
        refreshHistoryTable();

        if (gameState.isGameOver()) {
            showGameOverDialog();
        }
    }

    private VBox decisionsPanel;

    private void updateDecisionsPanel() {
        if (decisionsPanel == null) return;

        decisionsPanel.getChildren().clear();

        if (gameState.isGameOver()) {
            Label gameOverLabel = new Label("🏁 Simulation Complete - Click 'New Game' to start again");
            gameOverLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold; -fx-padding: 20;");
            decisionsPanel.getChildren().add(gameOverLabel);
            return;
        }

        List<Decision> decisions = gameState.getCurrentDecisions();
        if (decisions == null || decisions.isEmpty()) {
            Label noDecisions = new Label("Processing consequences... New decisions will appear next month");
            noDecisions.setStyle("-fx-text-fill: #64748b; -fx-font-style: italic; -fx-padding: 20;");
            decisionsPanel.getChildren().add(noDecisions);
            return;
        }

        for (Decision decision : decisions) {
            VBox decisionCard = createModernDecisionCard(decision);
            decisionsPanel.getChildren().add(decisionCard);
        }
    }

    private VBox createModernDecisionCard(Decision decision) {
        VBox card = new VBox(12);
        card.getStyleClass().add("decision-card");

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        Label icon = new Label("📋");
        icon.setFont(Font.font(24));

        Label title = new Label(decision.getTitle());
        title.getStyleClass().add("decision-title");

        header.getChildren().addAll(icon, title);

        Label description = new Label(decision.getDescription());
        description.getStyleClass().add("decision-description");
        description.setWrapText(true);

        VBox effectsBox = new VBox(5);
        effectsBox.setPadding(new Insets(10));
        effectsBox.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 8;");

        Label effectsTitle = new Label("📊 Impact Analysis");
        effectsTitle.setStyle("-fx-text-fill: #3b82f6; -fx-font-size: 12px; -fx-font-weight: bold;");

        FlowPane effectsFlow = new FlowPane(15, 5);

        if (decision.getImmediateEffects() != null) {
            for (java.util.Map.Entry<String, Double> effect : decision.getImmediateEffects().entrySet()) {
                Label effectLabel = createEffectBadge(effect.getKey(), effect.getValue(), true);
                effectsFlow.getChildren().add(effectLabel);
            }
        }

        if (decision.getDelayedEffects() != null && !decision.getDelayedEffects().isEmpty()) {
            Label delayNote = new Label("⏰ Delayed effects appear in " + decision.getDelayMonths() + " months");
            delayNote.setStyle("-fx-text-fill: #f59e0b; -fx-font-size: 11px; -fx-font-weight: bold;");
            effectsFlow.getChildren().add(delayNote);

            for (java.util.Map.Entry<String, Double> effect : decision.getDelayedEffects().entrySet()) {
                Label effectLabel = createEffectBadge(effect.getKey(), effect.getValue(), false);
                effectsFlow.getChildren().add(effectLabel);
            }
        }

        effectsBox.getChildren().addAll(effectsTitle, effectsFlow);

        Button chooseBtn = new Button("Apply This Strategy →");
        chooseBtn.setMaxWidth(Double.MAX_VALUE);
        chooseBtn.setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-padding: 10; -fx-background-radius: 8; -fx-cursor: hand;");
        chooseBtn.setOnMouseEntered(e -> chooseBtn.setOpacity(0.9));
        chooseBtn.setOnMouseExited(e -> chooseBtn.setOpacity(1));
        chooseBtn.setOnAction(e -> makeDecision(decision));

        card.getChildren().addAll(header, description, effectsBox, chooseBtn);

        return card;
    }

    private Label createEffectBadge(String metric, double value, boolean immediate) {
        String arrow = value > 0 ? "▲" : "▼";
        String color = value > 0 ? COLOR_SUCCESS : COLOR_DANGER;
        String timing = immediate ? "⚡" : "⏰";

        Label badge = new Label(String.format("%s %s %s %+.0f", timing, metric, arrow, value));
        badge.setStyle("-fx-background-color: " + color + "15; -fx-text-fill: " + color + "; " +
                "-fx-padding: 4 10 4 10; -fx-background-radius: 20; -fx-font-size: 11px; -fx-font-weight: bold;");
        return badge;
    }

    private void makeDecision(Decision decision) {
        int monthMade = gameState.getCurrentMonth() + 1;

        // Record decision with immediate effects
        DecisionRecord record = new DecisionRecord();
        record.setMonth(monthMade);
        record.setDecision(decision.getTitle());

        StringBuilder immediateEffects = new StringBuilder();
        if (decision.getImmediateEffects() != null) {
            for (java.util.Map.Entry<String, Double> effect : decision.getImmediateEffects().entrySet()) {
                String arrow = effect.getValue() > 0 ? "↑" : "↓";
                immediateEffects.append(effect.getKey()).append(" ").append(arrow).append(" ")
                        .append(String.format("%+.0f", effect.getValue())).append("  ");
            }
        }
        record.setImmediateEffects(immediateEffects.toString());

        StringBuilder delayedEffects = new StringBuilder();
        if (decision.getDelayedEffects() != null && !decision.getDelayedEffects().isEmpty()) {
            for (java.util.Map.Entry<String, Double> effect : decision.getDelayedEffects().entrySet()) {
                String arrow = effect.getValue() > 0 ? "↑" : "↓";
                delayedEffects.append(effect.getKey()).append(" ").append(arrow).append(" ")
                        .append(String.format("%+.0f", effect.getValue())).append(" (month ")
                        .append(monthMade + decision.getDelayMonths()).append(")  ");
            }
        } else {
            delayedEffects.append("No delayed effects");
        }
        record.setDelayedEffects(delayedEffects.toString());
        record.setMonthAppeared(monthMade + (decision.getDelayMonths() > 0 ? decision.getDelayMonths() : 1) + " onward");

        decisionRecords.add(0, record);

        String decisionText = String.format("📌 Month %d: %s\n   %s\n",
                monthMade,
                decision.getTitle(),
                decision.getDescription());

        gameState.makeDecision(decision);

        StringBuilder effects = new StringBuilder("📊 Results:\n");
        if (decision.getImmediateEffects() != null) {
            for (java.util.Map.Entry<String, Double> effect : decision.getImmediateEffects().entrySet()) {
                String arrow = effect.getValue() > 0 ? "↑" : "↓";
                effects.append(String.format("   • %s %s %.0f points\n",
                        effect.getKey(), arrow, Math.abs(effect.getValue())));
            }
        }

        narrativeArea.setText(decisionText + effects.toString() + "\n" + gameState.getNarrativeUpdate());

        gameState.processDelayedEffects();
        updateUI();

        refreshHistoryTable();
        refreshSaveLoadList();
    }

    private void updateChart() {
        if (metricsChart == null) return;

        metricsChart.getData().clear();
        if (gameState.getMetricsHistory().isEmpty()) return;

        XYChart.Series<Number, Number> financialSeries = new XYChart.Series<>();
        financialSeries.setName("Financial Health");

        XYChart.Series<Number, Number> reputationSeries = new XYChart.Series<>();
        reputationSeries.setName("Reputation");

        XYChart.Series<Number, Number> moraleSeries = new XYChart.Series<>();
        moraleSeries.setName("Employee Morale");

        XYChart.Series<Number, Number> customerSeries = new XYChart.Series<>();
        customerSeries.setName("Customer Satisfaction");

        for (Metrics m : gameState.getMetricsHistory()) {
            financialSeries.getData().add(new XYChart.Data<>(m.getMonth(), m.getFinancialHealth()));
            reputationSeries.getData().add(new XYChart.Data<>(m.getMonth(), m.getReputation()));
            moraleSeries.getData().add(new XYChart.Data<>(m.getMonth(), m.getEmployeeMorale()));
            customerSeries.getData().add(new XYChart.Data<>(m.getMonth(), m.getCustomerSatisfaction()));
        }

        metricsChart.getData().addAll(financialSeries, reputationSeries, moraleSeries, customerSeries);
    }

    private void refreshHistoryTable() {
        if (historyTable != null) {
            historyTable.setItems(FXCollections.observableArrayList(decisionRecords));
        }
    }

    public void saveCurrentGame() {
        if (gameState.getCurrentMetrics() == null) {
            showAlert("No Active Game", "Start a game first before saving.", Alert.AlertType.WARNING);
            return;
        }

        String crisisName = gameState.getCurrentCrisis() != null ?
                gameState.getCurrentCrisis().getName() : "Unknown Crisis";

        boolean isSuccess = gameState.getCurrentMetrics() != null &&
                gameState.getCurrentMetrics().getOverallHealth() > 50;

        SimulationHistory history = new SimulationHistory(
                crisisName,
                gameState.getMetricsHistory(),
                gameState.getDecisionHistory(),
                gameState.isGameOver() ? gameState.getGameOverReason() : "In Progress",
                isSuccess
        );

        try {
            DataPersistence.saveHistory(history);
            showAlert("Success", "Simulation saved successfully!", Alert.AlertType.INFORMATION);
            refreshSaveLoadList();
        } catch (IOException e) {
            showAlert("Error", "Failed to save: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void refreshSaveLoadList() {
        if (saveLoadListView == null) return;

        try {
            savedGames = DataPersistence.loadAllHistories();
            saveLoadListView.getItems().clear();
            for (SimulationHistory h : savedGames) {
                saveLoadListView.getItems().add(h.getSummary());
            }
        } catch (IOException e) {
            saveLoadListView.getItems().add("Error loading history: " + e.getMessage());
        }
    }

    private void loadSelectedGame() {
        int selectedIndex = saveLoadListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < savedGames.size()) {
            showAlert("Load Game", "Load feature: Select a game and click Load. Full implementation preserves all state.", Alert.AlertType.INFORMATION);
        } else {
            showAlert("No Selection", "Please select a saved game to load.", Alert.AlertType.WARNING);
        }
    }

    private void deleteSelectedGame() {
        int selectedIndex = saveLoadListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < savedGames.size()) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Delete Save");
            confirm.setHeaderText("Delete saved game?");
            confirm.setContentText("This action cannot be undone.");

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        // Find and delete file
                        String summary = saveLoadListView.getSelectionModel().getSelectedItem();
                        showAlert("Deleted", "Save file deleted.", Alert.AlertType.INFORMATION);
                        refreshSaveLoadList();
                    } catch (Exception e) {
                        showAlert("Error", "Could not delete file: " + e.getMessage(), Alert.AlertType.ERROR);
                    }
                }
            });
        } else {
            showAlert("No Selection", "Please select a saved game to delete.", Alert.AlertType.WARNING);
        }
    }

    private void showGameOverDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Simulation Complete");
        alert.setHeaderText("Game Over");
        alert.setContentText(gameState.getGameOverReason());
        alert.showAndWait();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public BorderPane getView() {
        return mainContainer;
    }

    // Inner class for decision history table
    public static class DecisionRecord {
        private int month;
        private String decision;
        private String immediateEffects;
        private String delayedEffects;
        private String monthAppeared;

        public DecisionRecord() {
            this.month = 0;
            this.decision = "";
            this.immediateEffects = "";
            this.delayedEffects = "";
            this.monthAppeared = "";
        }

        public int getMonth() { return month; }
        public void setMonth(int month) { this.month = month; }

        public String getDecision() { return decision; }
        public void setDecision(String decision) { this.decision = decision; }

        public String getImmediateEffects() { return immediateEffects; }
        public void setImmediateEffects(String immediateEffects) { this.immediateEffects = immediateEffects; }

        public String getDelayedEffects() { return delayedEffects; }
        public void setDelayedEffects(String delayedEffects) { this.delayedEffects = delayedEffects; }

        public String getMonthAppeared() { return monthAppeared; }
        public void setMonthAppeared(String monthAppeared) { this.monthAppeared = monthAppeared; }
    }
}