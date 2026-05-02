# CrisisSim — Business Crisis Management Simulator

> *Small decisions. Cascading consequences. Can your business survive?*

CrisisSim is an interactive **JavaFX desktop application** that puts you in the role of a small business owner navigating a major crisis — month by month, decision by decision. Built around **butterfly effect mechanics**, it teaches that the choices you make today can save or destroy your business weeks down the road.

---

## Overview

Most people think crisis management is about quick reactions. CrisisSim shows the opposite: a small decision in Month 1 — like choosing a cheap supplier to cut costs — might look smart at first. But two months later, quality problems surface, customers complain, and your reputation craters. That is the butterfly effect in action.

---

## Features

- **3 Crisis Scenarios** — Financial, Reputation, and Operational crises with unique decision trees
- **Butterfly Effect Engine** — Decisions trigger delayed consequences 2–3 months later
- **Real-Time Metrics Dashboard** — Track Financial Health, Reputation, Employee Morale, and Customer Satisfaction on a 0–100 scale
- **Interactive Line Chart** — Visualize how all four metrics trend over time
- **Decision History Table** — Full log of every decision, its immediate effects, and when delayed consequences appeared
- **Save / Load System** — Persist simulations to human-readable `.txt` files
- **Responsive UI** — Tab-based, Google Analytics-style layout that reflows at any window size
- **Keyboard Shortcuts** — `Ctrl+S` to save, `Ctrl+R` to reset

---

## The Three Crisis Scenarios

**Financial Crisis**
Your cash flow has dried up. Major clients have delayed payments, payroll is due in two weeks, and suppliers want payment upfront. Choose between aggressive cost-cutting, seeking investors, negotiating with creditors, or offering deep discounts for immediate cash. Every path has a price.

**Reputation Crisis**
A viral social media post accuses your company of unethical practices. Activists are calling for a boycott and local news wants a statement. Issue a public apology, deny and defend, invest in community initiatives, or wait it out. Your response shapes whether your brand recovers or permanently collapses.

**Operational Crisis**
Your main supplier has gone bankrupt. You have three weeks of inventory left. Rush an untested overseas order, build a buffer from multiple sources, invest in automation, or redesign around available components. Each path carries different costs, risks, and reliability outcomes.

---

## The Butterfly Effect Mechanic

Unlike standard business simulators, CrisisSim uses delayed consequence mechanics. Decisions made in Month 1 affect outcomes in Months 3–4. Small choices compound over time, and users learn that crisis management requires thinking ahead — not just reacting.

| Month | Decision | Immediate Effect | Delayed Effect (Month 3) |
|-------|----------|-----------------|--------------------------|
| 1 | Lay off 15% of staff | +15 Financial Health | -15 Reputation, -10 Customer Satisfaction |
| 1 | Negotiate payment plans | +5 Financial Health, +10 Reputation | None |

---

## User Interface

The dashboard uses a Google Analytics-style layout:

- **Header** — Dark blue gradient showing the app title, current month, active crisis name, and a warning indicator when any metric drops below critical levels
- **Metric Cards** — Large bold numbers with colored left borders: blue for Financial Health, red for Reputation, yellow for Employee Morale, green for Customer Satisfaction
- **Decision Cards** — Hover-highlighted cards with immediate effect badges and delayed effect badges; green for positive changes, red for negative
- **Line Chart** — Four colored lines across months, auto-updated after each decision
- **Tabs** — Dashboard, Decision History, Save/Load, Scenario Info

---

## Technical Stack

| Component | Technology |
|-----------|------------|
| Language | Java 17 |
| UI Framework | JavaFX 21 |
| Build Tool | Maven 3.6+ |
| Data Persistence | Plain-text files with custom format |
| Testing | JUnit 5.10.1 |
| Serialization | Gson 2.10.1 |
| Styling | Custom CSS (Material Design-inspired) |

---

## Object-Oriented Design

The application follows the Model-View-Controller (MVC) pattern:

- **Model** — `GameState`, `Metrics`, `Decision`, and `CrisisScenario` subclasses handle all business logic
- **View** — Programmatic JavaFX UI built without FXML for full layout control
- **Controller** — `MainController` coordinates user input and display updates

**Key design patterns:**

- **Abstract Class** — `CrisisScenario` defines shared behavior; `FinancialCrisis`, `ReputationCrisis`, and `OperationalCrisis` extend it with unique decision sets
- **Builder Pattern** — `Decision` objects are constructed fluently: `.addImmediateEffect().addDelayedEffect().setDelayMonths()`
- **Encapsulation** — `Metrics` validates all values and enforces 0–100 bounds before assignment
- **Polymorphism** — `GameState` works with any `CrisisScenario` subclass without knowing its specific type
- **Composition** — `GameState` contains a `CrisisScenario` reference, a list of `Metrics`, and a list of `Decision` objects

---

## Installation and Setup

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Internet connection for initial dependency download

### Clone and Run

```bash
git clone https://github.com/Pascal69p/CrisisSim.git
cd CrisisSim
mvn clean compile
mvn javafx:run
```

The main class is `com.crisissim.CrisisSimApp`. No additional configuration or environment variables are required beyond Java 17 and Maven.

---

## How to Play

1. Click **New Game** and select a crisis scenario
2. Review your current business metrics and the crisis narrative
3. Choose a decision strategy from the available cards
4. Watch immediate effects apply to your metrics
5. Continue through months — delayed consequences trigger automatically
6. Press `Ctrl+S` to save your progress at any time
7. Review the Decision History tab to understand the butterfly effect across your playthrough

---

## Keyboard Shortcuts

| Shortcut | Action |
|----------|--------|
| `Ctrl + S` | Save current simulation |
| `Ctrl + R` | Reset simulation (confirmation dialog appears) |

---

## Save File Format

Simulations are saved as `.txt` files in the `./saves/` directory. Each file includes:

- Simulation version and unique ID
- Crisis type and timestamp
- Outcome text and success status
- Complete monthly metrics history
- Full decision history with titles, effects, and activation months

The format uses delimited plain text, making save files human-readable and easy to inspect or debug.

---

## Testing

Unit tests cover:

- `Metrics` class bounds enforcement (0–100 clamping)
- `GameState` decision recording and month advancement
- Delayed effect activation on correct future months
- Game-over conditions at critical metric thresholds

---

## Technical Specifications

| Spec | Value |
|------|-------|
| Minimum Window Size | 1000 x 700 px |
| Default Window Size | 85% of screen |
| Tracked Metrics | 4 (Financial Health, Reputation, Employee Morale, Customer Satisfaction) |
| Metric Scale | 0 – 100 |
| Decision Delay Range | 2 – 3 months |
| Crisis Scenarios | 3 |
| Save Format | .txt with delimited fields |
| Save Location | ./saves/ |

---

## Known Issues and Future Improvements

- Load functionality currently shows a placeholder dialog — full state restoration is pending implementation
- Additional crisis scenarios can be added by extending the `CrisisScenario` abstract class
- Multi-player and classroom modes are planned for future development

---

## Contributors

- Pascal69p
- Saar0
- Course: COMP 306 — Real-World Java Application with AI Paired Programming

---

## License

This project was created for educational purposes as part of COMP 306 coursework.

---

## Acknowledgments

- JavaFX community for documentation and support
- Material Design guidelines for UI inspiration
- Google Analytics for dashboard layout patterns
