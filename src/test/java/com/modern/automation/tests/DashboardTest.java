package com.modern.automation.tests;

import com.modern.automation.base.BaseTest;
import com.modern.automation.pages.DashboardPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test class for Dashboard functionality.
 */
public class DashboardTest extends BaseTest {

    @Test(description = "Verify dashboard landing page integrity", groups = {"smokeTest"})
    public void testDashboardHeader() {
        // Log in via API to quickly reach the dashboard
        loginViaApi("Admin", "admin123");
        
        DashboardPage dashboardPage = new DashboardPage();
        Assert.assertTrue(dashboardPage.isDashboardDisplayed(), "Dashboard header should be visible.");
    }

    @Test(description = "Verify user profile name visibility", groups = {"smokeTest"})
    public void testUserProfileVisibility() {
        loginViaApi("Admin", "admin123");
        
        DashboardPage dashboardPage = new DashboardPage();
        String profileName = dashboardPage.getUserProfileName();
        Assert.assertFalse(profileName.isEmpty(), "User profile name should be visible on the dashboard.");
    }
}
