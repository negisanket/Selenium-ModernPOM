package com.modern.automation.utils;

/**
 * Utility to generate test data using centralized Excel utilities.
 */
public class TestDataGenerator {

    public static void main(String[] args) {
        String filePath = "src/test/resources/testdata/login_data.xlsx";
        generateLoginData(filePath);
    }

    /**
     * Generates login test data by leveraging ExcelUtils.
     */
    public static void generateLoginData(String filePath) {
        String[][] data = {
            {"username", "password", "Expected"}, // Header
            {"Admin", "admin123", "success"},
            {"InvalidUser", "wrongPass", "failure"},
            {"Admin", "wrongPassword", "failure"}
        };

        ExcelUtils.writeToExcel(filePath, "Sheet1", data);
    }
}
