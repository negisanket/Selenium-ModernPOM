package com.modern.automation.tests;

import com.modern.automation.base.BaseTest;
import com.modern.automation.driver.DriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import lombok.extern.slf4j.Slf4j;

/**
 * Demo test to verify Healenium integration.
 */
@Slf4j
public class HealeniumDemoTest extends BaseTest {

    @Test(description = "Demonstrate Healenium self-healing")
    public void testLocatorHealing() {
        log.info("Step 1: Interact with the correct locator to 'teach' Healenium the element attributes.");
        By correctLocator = By.name("username");
        WebElement usernameField = DriverFactory.getDriver().findElement(correctLocator);
        usernameField.sendKeys("Learning...");
        usernameField.clear();

        log.info("Step 2: Attempt to find the same element using a BROKEN locator.");
        // This XPath is intentionally incorrect (typo in attribute name)
        By brokenLocator = By.xpath("//input[@name='username_broken_typo']");

        /* 
         * Normally, the line below would throw a NoSuchElementException.
         * With Healenium, it will intercept the failure, compare the current page 
         * with the previously learned 'snapshot', and find the best match.
         */
        WebElement healedElement = DriverFactory.getDriver().findElement(brokenLocator);
        
        log.info("Healenium successfully intercepted the failure and healed the locator!");
        
        healedElement.sendKeys("Admin");
        
        // Verify the value was actually entered into the field
        Assert.assertEquals(healedElement.getAttribute("value"), "Admin", "Healing failed to find the correct field.");
    }
}
