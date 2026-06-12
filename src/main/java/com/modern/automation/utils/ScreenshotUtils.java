package com.modern.automation.utils;

import com.modern.automation.driver.DriverFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.IOException;

/**
 * Utility for capturing screenshots.
 */
@Slf4j
public class ScreenshotUtils {
    private static final String SCREENSHOT_PATH = "build/screenshots/";

    /**
     * Captures a screenshot and saves it to the specified path.
     * @param fileName Name of the screenshot file.
     * @return Path to the saved screenshot.
     */
    public static String captureScreenshot(String fileName) {
        String timestamp = DateUtils.getCurrentTimestamp();
        String destinationPath = SCREENSHOT_PATH + fileName + "_" + timestamp + ".png";
        
        File srcFile = ((TakesScreenshot) DriverFactory.getDriver()).getScreenshotAs(OutputType.FILE);
        File destFile = new File(destinationPath);

        try {
            FileUtils.copyFile(srcFile, destFile);
            log.info("Screenshot saved successfully at: {}", destinationPath);
        } catch (IOException e) {
            log.error("Failed to capture screenshot: {}", destinationPath, e);
        }

        return destinationPath;
    }
}
