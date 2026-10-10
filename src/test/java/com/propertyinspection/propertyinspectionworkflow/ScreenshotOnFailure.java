package com.propertyinspection.propertyinspectionworkflow;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

class ScreenshotOnFailure implements TestExecutionExceptionHandler {

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        Object test = context.getRequiredTestInstance();
        if (test instanceof BaseSeleniumTest base && base.driver instanceof TakesScreenshot camera) {
            try {
                Path dir = Path.of("target", "screenshots");
                Files.createDirectories(dir);
                String name = context.getRequiredTestClass().getSimpleName() + "_"
                        + context.getRequiredTestMethod().getName() + ".png";
                Files.copy(camera.getScreenshotAs(OutputType.FILE).toPath(), dir.resolve(name),
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                System.err.println("Could not save screenshot: " + e.getMessage());
            }
        }
        throw throwable;
    }
}