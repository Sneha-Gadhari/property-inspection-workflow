package com.propertyinspection.propertyinspectionworkflow;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = "server.port=8095")
@ExtendWith(ScreenshotOnFailure.class)
abstract class BaseSeleniumTest {

    static final String BASE_URL = "http://localhost:8095/";
    private static final HttpClient HTTP = HttpClient.newHttpClient();

    WebDriver driver;
    WebDriverWait wait;

    @BeforeEach
    void setUpDriver() {
        ChromeOptions options = new ChromeOptions();
        if (!"false".equals(System.getProperty("headless"))) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1280,900");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDownDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    // ---------- UI helpers ----------

    void login(String username, String password) {
        driver.get(BASE_URL);
        driver.findElement(By.id("loginUsername")).sendKeys(username);
        driver.findElement(By.id("loginPassword")).sendKeys(password);
        driver.findElement(By.cssSelector("#loginTab button.primary")).click();
    }

    void waitForDashboard() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("whoami")));
    }

    /** XPath for the table row whose text contains the given value. */
    static String row(String tableId, String text) {
        return "//table[@id='" + tableId + "']//tr[td[contains(normalize-space(.), '" + text + "')]]";
    }

    static String uniqueSuffix() {
        return Long.toString(System.currentTimeMillis(), 36);
    }

    // ---------- test-data helpers (REST, so UI tests stay focused on the journey) ----------

    static void createApprovedUser(String username, String password, String role) throws Exception {
        String body = post("api/users/register",
                "{\"username\":\"" + username + "\",\"password\":\"" + password + "\",\"role\":\"" + role + "\"}");
        Matcher m = Pattern.compile("\"id\"\\s*:\\s*(\\d+)").matcher(body);
        if (!m.find()) {
            throw new IllegalStateException("Could not create test user: " + body);
        }
        put("api/users/" + m.group(1) + "/approve");
    }

    static void createRequest(String address, String inspectorName) throws Exception {
        post("api/inspections", "{\"propertyAddress\":\"" + address + "\",\"inspectorName\":\""
                + inspectorName + "\",\"propertyType\":\"Residential\",\"contactNumber\":\"9876543210\"}");
    }

    private static String post(String path, String json) throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return HTTP.send(req, HttpResponse.BodyHandlers.ofString()).body();
    }

    private static void put(String path) throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(BASE_URL + path))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();
        HTTP.send(req, HttpResponse.BodyHandlers.ofString());
    }
}