package com.modern.automation.driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton + ThreadLocal Driver Factory to ensure thread-safe execution in concurrent pipelines.
 */
public class DriverFactory {

    private static final Logger logger = LoggerFactory.getLogger(DriverFactory.class);
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    private DriverFactory() {}

    /**
     * Initializes the driver based on the provided browser name.
     * Uses W3C compliant options.
     */
    public static void initDriver(String browser) {
        WebDriver driver;
        logger.info("Initializing WebDriver for browser: {}", browser);

        switch (browser.toLowerCase()) {
            case "chrome":
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--remote-allow-origins=*");
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                driver = new ChromeDriver(chromeOptions);
                break;
            case "firefox":
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                driver = new FirefoxDriver(firefoxOptions);
                break;
            case "edge":
                EdgeOptions edgeOptions = new EdgeOptions();
                driver = new EdgeDriver(edgeOptions);
                break;
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser);
        }

        driver.manage().window().maximize();
        driverThreadLocal.set(driver);
    }

    /**
     * Fetches the active thread's driver instance.
     */
    public static WebDriver getDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver == null) {
            throw new IllegalStateException("WebDriver not initialized. Call initDriver() first.");
        }
        return driver;
    }

    /**
     * Quits the driver and removes it from ThreadLocal.
     */
    public static void quitDriver() {
        if (driverThreadLocal.get() != null) {
            logger.info("Quitting WebDriver for thread: {}", Thread.currentThread().getId());
            driverThreadLocal.get().quit();
            driverThreadLocal.remove();
        }
    }
}
