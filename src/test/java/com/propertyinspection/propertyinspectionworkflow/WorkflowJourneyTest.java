package com.propertyinspection.propertyinspectionworkflow;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorkflowJourneyTest extends BaseSeleniumTest {

    @Test
    void newUserRegistersAdminApprovesAndUserCanLogIn() {
        String username = "inspector_" + uniqueSuffix();

        driver.get(baseUrl());
        driver.findElement(By.id("registerTabBtn")).click();
        driver.findElement(By.id("regUsername")).sendKeys(username);
        driver.findElement(By.id("regPassword")).sendKeys("pass123");
        new Select(driver.findElement(By.id("regRole"))).selectByValue("INSPECTOR");
        driver.findElement(By.cssSelector("#registerTab button.primary")).click();
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("authMsg"), "Registered"));

        // not approved yet -> cannot log in
        login(username, "pass123");
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("authMsg"), "not yet approved"));

        // admin approves
        login("demo", "demo123");
        By approveButton = By.xpath("//table[@id='pendingTbl']//tr[td[normalize-space()='" + username + "']]//button");
        wait.until(ExpectedConditions.elementToBeClickable(approveButton)).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(approveButton));

        // approved -> can log in
        login(username, "pass123");
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("roleLabel"), "INSPECTOR"));
        assertEquals(username, driver.findElement(By.id("whoami")).getText());
    }

    @Test
    void inspectorSubmitsRequestAndSeesItAsSubmitted() throws Exception {
        String inspector = "insp_" + uniqueSuffix();
        createApprovedUser(inspector, "pass123", "INSPECTOR");
        String address = "12 Test Road " + uniqueSuffix();

        login(inspector, "pass123");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("address")));
        driver.findElement(By.id("address")).sendKeys(address);
        new Select(driver.findElement(By.id("propertyType"))).selectByVisibleText("Commercial");
        driver.findElement(By.id("contactNumber")).sendKeys("9876543210");
        driver.findElement(By.id("notes")).sendKeys("Gate code 4321");
        driver.findElement(By.cssSelector("#submitCard button.primary")).click();

        By badge = By.xpath(row("tbl", address) + "//span[contains(@class,'badge')]");
        wait.until(ExpectedConditions.textToBePresentInElementLocated(badge, "SUBMITTED"));
        assertTrue(driver.findElement(By.xpath(row("tbl", address))).getText().contains("Commercial"));
    }

    @Test
    void reviewerStartsReviewThenApprovesWithRemark() throws Exception {
        String suffix = uniqueSuffix();
        String reviewer = "rev_" + suffix;
        String address = "45 Review Lane " + suffix;
        createApprovedUser(reviewer, "pass123", "REVIEWER");
        createRequest(address, "some_inspector");

        login(reviewer, "pass123");
        By badge = By.xpath(row("tbl", address) + "//span[contains(@class,'badge')]");
        wait.until(ExpectedConditions.textToBePresentInElementLocated(badge, "SUBMITTED"));

        driver.findElement(By.xpath(row("tbl", address) + "//button[normalize-space()='Start Review']")).click();
        wait.until(ExpectedConditions.textToBePresentInElementLocated(badge, "UNDER REVIEW"));

        driver.findElement(By.xpath(row("tbl", address) + "//button[normalize-space()='Approve']")).click();
        Alert prompt = wait.until(ExpectedConditions.alertIsPresent());
        prompt.sendKeys("Looks good");
        prompt.accept();

        wait.until(ExpectedConditions.textToBePresentInElementLocated(badge, "APPROVED"));
        assertTrue(driver.findElement(By.xpath(row("tbl", address))).getText().contains("Looks good"));
    }

    @Test
    void reviewerCannotSubmitRequestsAndInspectorCannotReview() throws Exception {
        String suffix = uniqueSuffix();
        String reviewer = "rev_" + suffix;
        String inspector = "insp_" + suffix;
        String address = "99 Access Street " + suffix;
        createApprovedUser(reviewer, "pass123", "REVIEWER");
        createApprovedUser(inspector, "pass123", "INSPECTOR");
        createRequest(address, inspector);

        login(reviewer, "pass123");
        waitForDashboard();
        assertFalse(driver.findElement(By.id("submitCard")).isDisplayed());

        login(inspector, "pass123");
        waitForDashboard();
        assertTrue(driver.findElement(By.id("submitCard")).isDisplayed());
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(row("tbl", address))));
        assertTrue(driver.findElements(By.xpath("//table[@id='tbl']//button")).isEmpty());
    }
}