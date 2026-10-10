package com.propertyinspection.propertyinspectionworkflow;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoginTest extends BaseSeleniumTest {

    @Test
    void adminCanLogInAndSeesAdminDashboard() {
        login("demo", "demo123");
        waitForDashboard();

        assertEquals("demo", driver.findElement(By.id("whoami")).getText());
        assertEquals("ADMIN", driver.findElement(By.id("roleLabel")).getText());
    }

    @Test
    void wrongPasswordIsRejected() {
        login("demo", "wrong-password");

        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.id("authMsg"), "Incorrect username or password"));
    }
}