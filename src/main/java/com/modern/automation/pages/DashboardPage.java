package com.modern.automation.pages;

import org.openqa.selenium.By;
import com.modern.automation.utils.ElementUtils;

/**
 * DashboardPage using Fluent POM design.
 */
public class DashboardPage {

    private final By dashboardHeader = By.xpath("//h6[text()='Dashboard']");
    private final By userDropdown = By.className("oxd-userdropdown-name");

    public boolean isDashboardDisplayed() {
        return ElementUtils.isElementDisplayed(dashboardHeader);
    }

    public String getUserProfileName() {
        return ElementUtils.getText(userDropdown);
    }
}
