# Healenium: Self-Healing Locators

This project is integrated with **Healenium**, an AI-powered engine that automatically heals broken or changed Selenium locators during test execution.

## 🚀 How it Works

Healenium works as a proxy between your test code and the WebDriver. It uses a "capture and compare" strategy:

1.  **Baseline Capture:** When a test successfully finds an element for the first time, Healenium takes a "snapshot" of the element and its surrounding DOM structure, storing it in the Healenium backend.
2.  **Detection:** If a locator changes (e.g., an ID changes from `login-btn` to `submit-btn`), Selenium would normally throw a `NoSuchElementException`.
3.  **Healing:** Healenium intercepts this exception. It retrieves the last successful snapshot of the element and compares it with the current page state using machine learning algorithms.
4.  **Recovery:** It identifies the "best match" based on the highest similarity score, performs the action on that element, and allows the test to continue without failing.
5.  **Reporting:** It logs the healing event and generates a report suggesting the updated locator.

## 🛠️ Integration in this Project

### 1. The Wrapper
In `DriverFactory.java`, the standard `WebDriver` is wrapped in a `SelfHealingDriver`:
```java
WebDriver delegate = new ChromeDriver(options);
WebDriver driver = SelfHealingDriver.create(delegate);
```

### 2. Configuration
The behavior of the healing engine is controlled via `src/test/resources/healenium.properties`:
- `heal-enabled`: Globally turn healing on/off.
- `score-cap`: The minimum similarity score (0.0 to 1.0) required to consider a match successful.
- `hlm.server.url`: The URL of the Healenium backend.

## 📋 Setup & Usage

### 1. Start the Backend
Healenium requires a backend service (PostgreSQL + Healing Service) to be running. This project includes a pre-configured Docker Compose file.

Run the following command in your terminal:
```bash
docker-compose -f healenium-docker-compose.yaml up -d
```

### 2. Run Tests
You do **not** need to change any of your Page Objects or test scripts. Simply run your tests as usual:
```bash
mvn clean test
```

### 3. View Healing Reports
After the tests are completed, you can see which locators were healed and get suggestions for your code:
- **URL:** [http://localhost:7878/healenium/report](http://localhost:7878/healenium/report)

## 💡 Best Practices
- **IntelliJ Plugin:** Install the "Healenium" plugin in IntelliJ IDEA to automatically update your `By` locators in the source code based on the healing results.
- **Review Reports:** Always review the healing report after a run to decide if the "healed" locator should be permanently updated in your Page Object.
