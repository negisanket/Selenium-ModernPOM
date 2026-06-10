package com.modern.automation.pages;

import org.openqa.selenium.By;
import com.modern.automation.utils.ElementUtils;

/**
 * LoginPage using Fluent POM design.
 */
public class LoginPage {

    // By Locators - No PageFactory
    private final By usernameInput = By.name("username");
    private final By passwordInput = By.name("password");
    private final By loginButton = By.xpath("//button[@type='submit']");
    private final By errorMessage = By.cssSelector(".oxd-alert-content-text");

    public LoginPage typeUsername(String username) {
        ElementUtils.type(usernameInput, username);
        return this;
    }

    public LoginPage typePassword(String password) {
        ElementUtils.type(passwordInput, password);
        return this;
    }

    public DashboardPage clickLogin() {
        ElementUtils.click(loginButton);
        return new DashboardPage();
    }

    /**
     * Chained login method.
     */
    public DashboardPage login(String username, String password) {
        return typeUsername(username)
                .typePassword(password)
                .clickLogin();
    }

    public String getErrorMessage() {
        return ElementUtils.getText(errorMessage);
    }
}
