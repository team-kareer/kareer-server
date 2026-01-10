package org.sopt.kareer.global.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WebDriverFactory {

    @Value("${selenium.chrome.driver-path:}")
    private String chromeDriverPath;

    public WebDriver create() {
        ChromeOptions options = new ChromeOptions();

        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--lang=en-US");

        if (chromeDriverPath != null && !chromeDriverPath.isBlank()) {
            System.setProperty(
                    "webdriver.chrome.driver",
                    chromeDriverPath
            );
        }

        return new ChromeDriver(options);
    }
}
