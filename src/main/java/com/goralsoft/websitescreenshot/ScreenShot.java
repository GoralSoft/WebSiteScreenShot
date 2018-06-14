/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goralsoft.websitescreenshot;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.apache.commons.io.FileUtils;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.safari.SafariDriver;

/**
 *
 * @author vasu
 */
public class ScreenShot {

    private static WebDriver driver = null;
    private static String browser,weburl,outputpath;
    public void getScreenShot() {
        ArrayList<String> targets = new ArrayList<String>();
        String homePage = "http://www.goralsoft.com";
        String url = "";
        HttpURLConnection huc = null;
        int respCode = 200;

        Properties prop = new Properties();
	InputStream input = null;

	try {
		String filename = "config.properties";
    		input = App.class.getClassLoader().getResourceAsStream(filename);
    		if(input==null){
    	            System.out.println("Sorry, unable to find " + filename);
    		    return;
    		}

    		//load a properties file from class path, inside static method
    		prop.load(input);
                browser=prop.getProperty("browser");
                weburl=prop.getProperty("url");
                outputpath=prop.getProperty("outputpath");
		System.out.println(browser);
		System.out.println(weburl);
		System.out.println(outputpath);

	} catch (IOException ex) {
            ex.printStackTrace();
	} finally {
		if (input != null) {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
        
        if(browser.equalsIgnoreCase("ie")){
            System.setProperty ("webdriver.ie.driver", "IEDriverServer.exe");
            driver = new InternetExplorerDriver();
        }
        if(browser.equalsIgnoreCase("edge")){
            System.setProperty ("webdriver.edge.driver", "MicrosoftWebDriver.exe");
            driver = new EdgeDriver();
        }
        else if(browser.equalsIgnoreCase("firefox")){
            System.setProperty("webdriver.gecko.driver", "geckodriver.exe");
            driver = new FirefoxDriver();
        }
        else if(browser.equalsIgnoreCase("safari")){
            System.setProperty("webdriver.safari.noinstall", "true");
            driver = new SafariDriver();
        }
        else if(browser.equalsIgnoreCase("opera")){
            System.setProperty("webdriver.opera.driver", "true");
            driver = new OperaDriver();
        }
        else{
            System.setProperty ("webdriver.chrome.driver", "chromedriver.exe");
            driver = new ChromeDriver();
        }
        
        driver.manage().window().maximize();

        driver.get(weburl);

        List<WebElement> links = driver.findElements(By.tagName("a"));

        Iterator<WebElement> it = links.iterator();

        while (it.hasNext()) {

            url = it.next().getAttribute("href");

            System.out.println(url);

            if (url == null || url.isEmpty()) {
                System.out.println("URL is either not configured for anchor tag or it is empty");
                continue;
            }

            if (!url.startsWith(weburl)) {
                System.out.println("URL belongs to another domain, skipping it.");
                continue;
            }

            try {
                huc = (HttpURLConnection) (new URL(url).openConnection());

                huc.setRequestMethod("HEAD");

                huc.connect();

                respCode = huc.getResponseCode();

                if (respCode >= 400) {
                    System.out.println(url + " is a broken link");
                } else {
                    targets.add(url);   //adding valid urls into arraylist for capturing the page                
                    System.out.println(url + " is a valid link");

                }

            } catch (MalformedURLException e) {                
                e.printStackTrace();
            } catch (IOException e) {                
                e.printStackTrace();
            }
        }

        //to capture the valid url(s)
        for (String target : targets) {
            System.out.println("capturing screen shot...." + url);
            driver.get(target);
            ScreenShot.captureScreenShot(driver);
        }

        driver.quit();
    }

    public static void captureScreenShot(WebDriver ldriver) {
        // Take screenshot and store as a file format             
        File src = ((TakesScreenshot) ldriver).getScreenshotAs(OutputType.FILE);
        try {
            // now copy the  screenshot to desired location using copyFile method
            FileUtils.copyFile(src, new File( outputpath+ System.currentTimeMillis() + ".png"));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
