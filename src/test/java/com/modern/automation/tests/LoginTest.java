package com.modern.automation.tests;

import com.modern.automation.base.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.modern.automation.pages.DashboardPage;
import com.modern.automation.pages.LoginPage;
import com.modern.automation.utils.ExcelUtils;
import com.modern.automation.utils.RetryAnalyzer;
import com.modern.automation.utils.TestDataGenerator;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;

/**
 * Test class for Login functionality.
 */
@Slf4j
public class LoginTest extends BaseTest {

    private static int flakyCounter = 0;
    private final String excelPath = "src/test/resources/testdata/login_data.xlsx";

    @BeforeClass
    public void prepareTestData() {
        log.info("Preparing Excel test data...");
        TestDataGenerator.generateLoginData(excelPath);
    }

    @DataProvider(name = "loginData")
    public Object[][] getLoginData() {
        log.info("Reading test data from Excel: {}", excelPath);
        return ExcelUtils.getTestData(excelPath, "Sheet1");
    }

    @Test(description = "Verify login with multiple data sets using DataProvider", dataProvider = "loginData")
    public void testLoginWithDataProvider(String username, String password, String expectedResult) {
        LoginPage loginPage = new LoginPage();
        DashboardPage dashboardPage = loginPage.login(username, password);

        if (expectedResult.equalsIgnoreCase("success")) {
            Assert.assertTrue(dashboardPage.isDashboardDisplayed(), "Dashboard should be displayed.");
        } else {
            Assert.assertEquals(loginPage.getErrorMessage(), "Invalid credentials", "Error message mismatch.");
        }
    }

    @Test(description = "Verify RetryAnalyzer with a flaky test", retryAnalyzer = RetryAnalyzer.class)
    public void testFlakyLogin() {
        flakyCounter++;
        log.info("Flaky test execution count: {}", flakyCounter);

        if (flakyCounter == 1) {
            Assert.fail("Simulated first-time failure for RetryAnalyzer validation.");
        }
        
        // Pass on second attempt
        Assert.assertTrue(true, "Passed on retry.");
    }

    @Test(description = "Verify successful login with valid credentials via UI", groups = {"smokeTest"})
    public void testSuccessfulLoginViaUI() {
        LoginPage loginPage = new LoginPage();
        DashboardPage dashboardPage = loginPage.login("Admin", "admin123");

        Assert.assertTrue(dashboardPage.isDashboardDisplayed(), "Dashboard should be displayed after login.");
        Assert.assertFalse(dashboardPage.getUserProfileName().isEmpty(), "User profile name should not be empty.");
    }

    @Test(description = "Verify successful login using Hybrid API-UI Bridge", groups = {"smokeTest"})
    public void testHybridLogin() {
        // Use the API bridge to skip manual UI login
        loginViaApi("Admin", "admin123");

        DashboardPage dashboardPage = new DashboardPage();
        Assert.assertTrue(dashboardPage.isDashboardDisplayed(), "Dashboard should be displayed after API-UI hybrid login.");
    }

    @Test(description = "Verify login failure with invalid credentials")
    public void testInvalidLogin() {
        LoginPage loginPage = new LoginPage();
        loginPage.login("Invalid", "WrongPass");

        Assert.assertEquals(loginPage.getErrorMessage(), "Invalid credentials", "Error message should match.");
    }
}
