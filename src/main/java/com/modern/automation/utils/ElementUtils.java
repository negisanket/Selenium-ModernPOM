package com.modern.automation.utils;

import com.modern.automation.driver.DriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Stateless utility class for Selenium actions.
 * Fetches the active thread's driver dynamically from DriverFactory.
 */
public class ElementUtils {

    private static final Logger logger = LoggerFactory.getLogger(ElementUtils.class);
    private static final int DEFAULT_TIMEOUT = 15;

    private ElementUtils() {}

    private static WebDriverWait getWait() {
        return new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(DEFAULT_TIMEOUT));
    }

    public static WebElement waitForElementVisible(By locator) {
        logger.debug("Waiting for element to be visible: {}", locator);
        return getWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement waitForElementClickable(By locator) {
        logger.debug("Waiting for element to be clickable: {}", locator);
        return getWait().until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static void click(By locator) {
        logger.info("Clicking on element: {}", locator);
        waitForElementClickable(locator).click();
    }

    public static void type(By locator, String text) {
        logger.info("Typing text '{}' into element: {}", text, locator);
        WebElement element = waitForElementVisible(locator);
        element.clear();
        element.sendKeys(text);
    }

    public static String getText(By locator) {
        logger.info("Getting text from element: {}", locator);
        return waitForElementVisible(locator).getText();
    }

    public static boolean isElementDisplayed(By locator) {
        try {
            return waitForElementVisible(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
