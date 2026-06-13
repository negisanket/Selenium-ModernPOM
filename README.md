# Selenium ModernPOM Framework

## Overview
An enterprise-grade, high-concurrency hybrid automation framework designed for modern web applications. It integrates **Selenium 4**, **RestAssured**, and **TestNG** using a highly modular and thread-safe architecture.

## Architectural Pillars

### 1. Thread-Safe Driver Management (`DriverFactory`)
- Utilizes `ThreadLocal<WebDriver>` to ensure that each test thread has its own isolated browser instance.
- Supports dynamic browser selection (Chrome, Firefox, Edge) via TestNG parameters or environment variables.
- Uses W3C-compliant `Options` classes for browser configuration.

### 2. Stateless Element Utilities (`ElementUtils`)
- A completely stateless wrapper for Selenium actions.
- Dynamically fetches the active thread's driver from `DriverFactory` on every call, preventing `NoSuchSessionException` or `NullPointerException` during parallel execution.
- Implements robust explicit waits (`WebDriverWait`) for all interactions.

### 3. Fluent Page Object Model (POM)
- Page Objects utilize raw `By` locators instead of `@FindBy` to ensure thread safety and explicit control over element initialization.
- Sequential actions are chained (Fluent interface), making test scripts highly readable.

### 4. Hybrid API-UI Bridge
- Combines REST API capabilities with UI automation.
- **Optimization:** Authenticates via API (`ApiClient`) to retrieve session tokens/cookies and injects them directly into the browser. This allows tests to skip the slow UI login flow and jump directly to protected pages.

### 5. Data-Driven Testing (Excel + DataProvider)
- Integrated **Apache POI** for reading test data from Excel (`.xlsx`) files.
- `ExcelUtils` parses sheets into a 2D object array for TestNG `@DataProvider`.
- Includes `TestDataGenerator` to programmatically create dummy data files.

### 6. Self-Healing Locators (Healenium)
- Integrated **Healenium** to automatically recover from broken or changed locators.
- Wraps the standard `WebDriver` in a `SelfHealingDriver` within the `DriverFactory`.
- Eliminates test fragility caused by minor UI changes without requiring code updates.
- **Report:** Generates AI-driven healing suggestions available at `http://localhost:7878/healenium/report`.

### 7. Resilience & Retry Mechanism
- Implements `IRetryAnalyzer` to automatically re-run flaky tests.
- Configurable max retry count to ensure stability in CI/CD pipelines.

### 8. Observability & Utilities
- **Cleanup Strategy:** Automatically archives artifacts from the last 3 runs in `test-archives/`.
- **Lombok @Slf4j:** Centralized, boilerplate-free logging across all framework layers.
- **Screenshot Utilities:** `ScreenshotUtils` captures browser state during execution.
- **Automated Failure Capture:** `TestListener` (TestNG `ITestListener`) automatically captures and saves a screenshot to `build/screenshots/` whenever a test fails.
- **Allure Reporting:** Integrated Allure for rich, interactive test reports.

## Project Structure
```text
src/
├── main/java/com/modern/automation/
│   ├── api/             # RestAssured API clients
│   ├── driver/          # ThreadLocal WebDriver + Healenium management
│   ├── pages/           # Fluent Page Objects (By locators)
│   └── utils/           # Stateless Element, Cleanup, Screenshot, Date utils & Listeners
└── test/java/com/modern/automation/
    ├── base/            # BaseTest for setup/teardown & Hybrid Bridge
    └── tests/           # TestNG test suites
```

## How to Run

### Prerequisites
- JDK 17+
- Maven 3.8+
- Docker (for Healenium Backend)
- Browsers installed (Chrome/Firefox)

### 1. Start Healenium Backend
```bash
docker-compose -f healenium-docker-compose.yaml up -d
```

### 2. Execute via Maven
```bash
# Run all tests
mvn clean test

# Run only smoke tests
mvn clean test -DsuiteXmlFile=src/test/resources/testng-smoke.xml
```

### 3. Generate Allure Report
```bash
# Generate and open report
mvn allure:serve
```

### View Healing Results
Visit [http://localhost:7878/healenium/report](http://localhost:7878/healenium/report) after your test run to see AI-healed locators.

### Execute via TestNG XML
Right-click `src/test/resources/testng.xml` (Full Suite) or `src/test/resources/testng-smoke.xml` (Smoke Suite) and select **Run**.

## Key Features Demonstrated
- **Parallel Execution:** Configured in `testng.xml` to run Chrome and Firefox tests concurrently.
- **Test Categorization:** Uses TestNG groups (`smokeTest`) for prioritized execution.
- **Flaky Test Handling:** `testFlakyLogin` demonstrates the `RetryAnalyzer` logic.
- **Excel Integration:** `testLoginWithDataProvider` reads credentials directly from `login_data.xlsx`.
- **Hybrid Auth:** `testHybridLogin` demonstrates seeding cookies via API.
- **Visual Analytics:** Allure reports provide a detailed breakdown of test results with embedded failure screenshots.

## 🤖 AI-Agentic Readiness (MCP)
This framework is fully integrated with the **Model Context Protocol (MCP)**, allowing it to interface with AI Agents (like Claude or Gemini).
- **Autonomous Testing:** The AI can execute tests, analyze screenshots, and self-heal locators using the included `mcp-bridge`.
- **Framework-Aware Tools:** Exposes `DriverFactory` and `ElementUtils` capabilities directly to AI agents.
- **Detailed Guide:** See [MCPIntegration.md](./MCPIntegration.md) for the full architectural roadmap.

## 🚀 CI/CD Integration (Jenkins)
The framework includes a production-ready **Jenkinsfile** (Declarative Pipeline) for continuous integration.
- **Parameterized Execution:** Select `BROWSER`, `ENVIRONMENT`, and specific test suites (`RUN_SMOKE`, `RUN_REGRESSION`) at runtime.
- **Fail-Safe Reporting:** Integrated Allure reporting and JUnit backup. The pipeline is designed to capture results even if individual test stages fail.
- **Windows Optimized:** Configured to run seamlessly on Windows-based Jenkins nodes using Batch commands.
- **Artifact Management:** Automatically archives failure screenshots for rapid debugging.
