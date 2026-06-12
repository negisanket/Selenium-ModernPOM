package com.modern.automation.utils;

import com.modern.automation.driver.DriverFactory;
import io.qameta.allure.Attachment;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNG Listener for automated actions during test lifecycle.
 * Handles screenshot capture on test failure and attaches them to Allure reports.
 */
@Slf4j
public class TestListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        log.error("Test Failed: {}. Capturing screenshot...", result.getName());
        try {
            // Local file capture
            String path = ScreenshotUtils.captureScreenshot(result.getName());
            log.info("Failure screenshot captured locally at: {}", path);
            
            // Allure Attachment
            saveScreenshot(result.getName());
        } catch (Exception e) {
            log.error("Failed to capture screenshot on test failure.", e);
        }
    }

    @Attachment(value = "Failure screenshot - {0}", type = "image/png")
    public byte[] saveScreenshot(String testName) {
        return ((TakesScreenshot) DriverFactory.getDriver()).getScreenshotAs(OutputType.BYTES);
    }

    @Override
    public void onTestStart(ITestResult result) {
        log.info("Starting Test: {}", result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log.info("Test Passed: {}", result.getName());
    }
}
