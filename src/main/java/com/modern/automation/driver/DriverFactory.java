package com.modern.automation.driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import com.epam.healenium.SelfHealingDriver;
import lombok.extern.slf4j.Slf4j;

/**
 * Singleton + ThreadLocal Driver Factory to ensure thread-safe execution in concurrent pipelines.
 */
@Slf4j
public class DriverFactory {

    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    private DriverFactory() {}

    /**
     * Initializes the driver based on the provided browser name.
     * Uses W3C compliant options.
     */
    public static void initDriver(String browser) {
        log.info("Initializing WebDriver for browser: {}", browser);

        WebDriver delegate = switch (browser.toLowerCase()) {
            case "chrome" -> {
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--remote-allow-origins=*");
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-dev-shm-usage");
                yield new ChromeDriver(options);
            }
            case "firefox" -> new FirefoxDriver(new FirefoxOptions());
            case "edge" -> new EdgeDriver(new EdgeOptions());
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
        };

        /* 
        // Wrap the standard driver with Healenium's SelfHealingDriver
        WebDriver driver = SelfHealingDriver.create(delegate);
        */
        WebDriver driver = delegate;

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
            log.info("Quitting WebDriver for thread: {}", Thread.currentThread().getId());
            driverThreadLocal.get().quit();
            driverThreadLocal.remove();
        }
    }
}
