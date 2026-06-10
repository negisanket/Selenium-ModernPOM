package com.modern.automation.utils;

import com.modern.automation.driver.DriverFactory;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Utility for capturing screenshots.
 */
public class ScreenshotUtils {

    private static final Logger logger = LoggerFactory.getLogger(ScreenshotUtils.class);
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
            logger.info("Screenshot saved successfully at: {}", destinationPath);
        } catch (IOException e) {
            logger.error("Failed to capture screenshot: {}", destinationPath, e);
        }

        return destinationPath;
    }
}
