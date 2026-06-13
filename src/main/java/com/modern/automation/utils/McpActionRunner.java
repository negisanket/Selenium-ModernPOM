package com.modern.automation.utils;

import com.modern.automation.driver.DriverFactory;
import org.openqa.selenium.By;
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.Field;

/**
 * A CLI Runner to bridge MCP actions to the Java Framework.
 * Usage: java McpActionRunner <browser> <action> <target> [value]
 */
@Slf4j
public class McpActionRunner {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: McpActionRunner <browser> <action> <target> [value]");
            System.exit(1);
        }

        String browser = args[0];
        String action = args[1];
        String target = args.length > 2 ? args[2] : "";
        String value = args.length > 3 ? args[3] : "";

        try {
            // 1. Initialize Driver
            DriverFactory.initDriver(browser);

            // 2. Resolve Locator if target looks like "Page.element"
            By locator = resolveLocator(target);

            // 3. Execute Action
            switch (action.toLowerCase()) {
                case "navigate" -> DriverFactory.getDriver().get(target);
                case "click" -> ElementUtils.click(locator);
                case "type" -> ElementUtils.type(locator, value);
                case "gettext" -> System.out.println("RESULT: " + ElementUtils.getText(locator));
                default -> throw new IllegalArgumentException("Unknown action: " + action);
            }

            log.info("Action {} completed successfully", action);

        } catch (Exception e) {
            log.error("Action failed: {}", e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } finally {
            DriverFactory.quitDriver();
        }
    }

    private static By resolveLocator(String target) throws Exception {
        if (!target.contains(".")) {
            return null;
        }

        String[] parts = target.split("\\.");
        String className = parts[0];
        String fieldName = parts[1];

        Class<?> pageClass = Class.forName("com.modern.automation.pages." + className);
        Field field = pageClass.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (By) field.get(null);
    }
}
