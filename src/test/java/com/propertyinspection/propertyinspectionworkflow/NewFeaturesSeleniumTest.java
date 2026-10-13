package com.propertyinspection.propertyinspectionworkflow;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import static org.junit.jupiter.api.Assertions.assertTrue;

class NewFeaturesSeleniumTest extends BaseSeleniumTest {

    @Test
    void inspectorCanSubmitWithOptionalFieldsAndViewDetails() throws Exception {
        String inspector = "insp_opt_" + uniqueSuffix();
        createApprovedUser(inspector, "pass123", "INSPECTOR");
        String address = "789 Skyline Tower " + uniqueSuffix();

        login(inspector, "pass123");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("address")));

        driver.findElement(By.id("address")).sendKeys(address);
        new Select(driver.findElement(By.id("propertyType"))).selectByVisibleText("Commercial");
        driver.findElement(By.id("contactNumber")).sendKeys("9123456780");
        driver.findElement(By.id("ownerName")).sendKeys("Acme Properties");
        new Select(driver.findElement(By.id("urgency"))).selectByValue("HIGH");
        driver.findElement(By.id("numRooms")).sendKeys("12");
        driver.findElement(By.id("numFloors")).sendKeys("3");
        driver.findElement(By.id("areaSqFt")).sendKeys("4500");
        driver.findElement(By.id("checklistSummary")).sendKeys("Structural: OK; Electrical: Verified");
        driver.findElement(By.id("notes")).sendKeys("Keycard available at front security desk.");

        driver.findElement(By.cssSelector("#submitCard button.primary")).click();

        By badge = By.xpath(row("tbl", address) + "//span[contains(@class,'badge')]");
        wait.until(ExpectedConditions.textToBePresentInElementLocated(badge, "SUBMITTED"));

        // Inspector table #tbl must still contain NO buttons
        assertTrue(driver.findElements(By.xpath("//table[@id='tbl']//button")).isEmpty());

        // Inspector can click Details link and view the modal
        driver.findElement(By.xpath(row("tbl", address) + "//a[contains(@class,'view-details-link')]")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("detailsModal")));
        assertTrue(driver.findElement(By.id("modalContent")).getText().contains("Acme Properties"));
        assertTrue(driver.findElement(By.id("modalContent")).getText().contains("4500 sq ft"));
    }

    @Test
    void adminViewsAnalyticsAndSvgCharts() throws Exception {
        createRequest("100 Alpha St " + uniqueSuffix(), "demo_insp");
        createRequest("200 Beta St " + uniqueSuffix(), "demo_insp");

        login("demo", "demo123");
        waitForDashboard();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("statsRow")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("analyticsSection")));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#statusChartSvg svg")));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#propertyTypeChartSvg svg")));
    }
}
