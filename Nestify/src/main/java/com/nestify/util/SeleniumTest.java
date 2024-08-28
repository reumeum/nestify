package com.nestify.util;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public class SeleniumTest {
    public static void main(String[] args) {

        // 1. Auto installs
        WebDriverManager.chromedriver().setup();

        // 2. use the selenium with the driver
        WebDriver driver = new ChromeDriver();	
        driver.get("https://google.com");

        // 3. quit
        driver.quit();  
    }
}
