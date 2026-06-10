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

### 6. Resilience & Retry Mechanism
- Implements `IRetryAnalyzer` to automatically re-run flaky tests.
- Configurable max retry count to ensure stability in CI/CD pipelines.

### 7. Observability & Utilities
- **Screenshot Utilities:** `ScreenshotUtils` captures browser state during execution.
- **Date & Time Utilities:** `DateUtils` provides formatted timestamps for logs and filenames.
- **Automated Failure Capture:** `TestListener` (TestNG `ITestListener`) automatically captures and saves a screenshot to `build/screenshots/` whenever a test fails.
- **Allure Reporting:** Integrated Allure for rich, interactive test reports. Screenshots of failed tests are automatically embedded directly into the report.

## Project Structure
```text
src/
├── main/java/com/modern/automation/
│   ├── api/             # RestAssured API clients
│   ├── driver/          # ThreadLocal WebDriver management
│   ├── pages/           # Fluent Page Objects (By locators)
│   └── utils/           # Stateless Element, Excel, Screenshot, Date utils & Listeners
└── test/java/com/modern/automation/
    ├── base/            # BaseTest for setup/teardown & Hybrid Bridge
    └── tests/           # TestNG test suites
```

## How to Run

### Prerequisites
- JDK 17+
- Maven 3.8+
- Browsers installed (Chrome/Firefox)

### Execute via Maven
```bash
# Run all tests
mvn clean test

# Run only smoke tests
mvn clean test -DsuiteXmlFile=src/test/resources/testng-smoke.xml
```

### Generate Allure Report
After running the tests, use the following commands to generate and view the report:
```bash
# Generate report
mvn allure:report

# Open report in browser
mvn allure:serve
```

### Execute via TestNG XML
Right-click `src/test/resources/testng.xml` (Full Suite) or `src/test/resources/testng-smoke.xml` (Smoke Suite) and select **Run**.

## Key Features Demonstrated
- **Parallel Execution:** Configured in `testng.xml` to run Chrome and Firefox tests concurrently.
- **Test Categorization:** Uses TestNG groups (`smokeTest`) for prioritized execution.
- **Flaky Test Handling:** `testFlakyLogin` demonstrates the `RetryAnalyzer` logic.
- **Excel Integration:** `testLoginWithDataProvider` reads credentials directly from `login_data.xlsx`.
- **Hybrid Auth:** `testHybridLogin` demonstrates seeding cookies via API.
- **Visual Analytics:** Allure reports provide a detailed breakdown of test results with embedded failure screenshots.
