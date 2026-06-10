package com.modern.automation.base;

import com.modern.automation.api.ApiClient;
import com.modern.automation.driver.DriverFactory;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * BaseTest class for setup and teardown.
 * Includes Hybrid API-UI bridge logic.
 */
public abstract class BaseTest {

    protected static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
    
    protected String baseUrl = getEnvOrDefault("BASE_UI_URL", "https://opensource-demo.orangehrmlive.com/");
    protected String baseApiUrl = getEnvOrDefault("BASE_API_URL", "https://opensource-demo.orangehrmlive.com/");
    protected String defaultBrowser = getEnvOrDefault("BROWSER", "chrome");

    @BeforeMethod
    @Parameters("browser")
    public void setUp(@Optional String browser) {
        String targetBrowser = (browser != null) ? browser : defaultBrowser;
        DriverFactory.initDriver(targetBrowser);
        DriverFactory.getDriver().get(baseUrl);
    }

    @AfterMethod
    public void tearDown() {
        DriverFactory.quitDriver();
    }

    /**
     * Hybrid Bridge: Authenticate via API and seed cookies into the browser.
     */
    protected void loginViaApi(String username, String password) {
        logger.info("Performing hybrid API login for user: {}", username);
        ApiClient apiClient = new ApiClient(baseApiUrl);
        Map<String, String> cookies = apiClient.getSessionCookies(username, password);

        WebDriver driver = DriverFactory.getDriver();
        // Browser must be on the domain to set cookies
        driver.get(baseUrl);

        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            Cookie cookie = new Cookie(entry.getKey(), entry.getValue());
            driver.manage().addCookie(cookie);
        }

        // Refresh to apply cookies and enter authenticated state
        driver.navigate().refresh();
        logger.info("Hybrid login completed. Cookies seeded and page refreshed.");
    }

    private String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value != null) ? value : defaultValue;
    }
}
