package com.modern.automation.utils;

import com.modern.automation.driver.DriverFactory;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TestNG Listener for automated actions during test lifecycle.
 * Handles screenshot capture on test failure and attaches them to Allure reports.
 */
public class TestListener implements ITestListener {

    private static final Logger logger = LoggerFactory.getLogger(TestListener.class);

    @Override
    public void onTestFailure(ITestResult result) {
        logger.error("Test Failed: {}. Capturing screenshot...", result.getName());
        try {
            // Local file capture
            String path = ScreenshotUtils.captureScreenshot(result.getName());
            logger.info("Failure screenshot captured locally at: {}", path);
            
            // Allure Attachment
            saveScreenshot(result.getName());
        } catch (Exception e) {
            logger.error("Failed to capture screenshot on test failure.", e);
        }
    }

    @Attachment(value = "Failure screenshot - {0}", type = "image/png")
    public byte[] saveScreenshot(String testName) {
        return ((TakesScreenshot) DriverFactory.getDriver()).getScreenshotAs(OutputType.BYTES);
    }

    @Override
    public void onTestStart(ITestResult result) {
        logger.info("Starting Test: {}", result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("Test Passed: {}", result.getName());
    }
}
