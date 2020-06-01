package sjtu.testing.webui;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.TessAPI;
import net.sourceforge.tess4j.Tesseract;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import sjtu.testing.webui.common.CommonOperation;
import sjtu.testing.webui.common.Utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Hello world!
 *
 */
@Slf4j
public class App {
    private final static String UPLOAD_FILE = "upload.zip";
    private enum TIMEZONE {
        TOKYO,
        PEKING
    }
    private enum LOCALE {
        ENGLISH,
        CHINESE
    }
    private static final Map<TIMEZONE, String> timezoneOptions = new HashMap<>();
    private static final Map<LOCALE, String> localeOptions = new HashMap<>();
    private static final String username = "G245078728";
    private static final String password = "SHOUXING?CRAZY";

    public static void main( String[] args ) {
        WebDriver driver;

        setup();

        driver = new ChromeDriver();
        testCommitAssignment(driver);

        driver = new ChromeDriver();
        testProfileSetting(driver, localeOptions.get(LOCALE.CHINESE),
                timezoneOptions.get(TIMEZONE.PEKING), false);
    }

    private static void setup() {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\ALIENWARE\\webdriver\\chromedriver.exe");
        System.setProperty("TESSDATA_PREFIX", ".");
        System.setProperty("SCREEN_ZOOM_RATIO", String.valueOf(1.25));
        CommonOperation.debug = true;

        timezoneOptions.put(TIMEZONE.TOKYO, "Tokyo");
        timezoneOptions.put(TIMEZONE.PEKING, "Beijing");

        localeOptions.put(LOCALE.CHINESE, "zh-Hans");
        localeOptions.put(LOCALE.ENGLISH, "en");
    }

    private static void testCommitAssignment(WebDriver driver) {
        WebDriverWait waitElement = new WebDriverWait(driver, 10);
        WebElement courseSvgElement, caLinkElement, assignmentListLinkElement,
                assignmentLinkElement, commitButtonElement, uploadElement, submitButton;
        File uploadFile;

        log.info("Running commit assignment module ...");
        CommonOperation.login(driver, username, password);

        courseSvgElement = waitElement.until(
                (ExpectedCondition<WebElement>) d -> d.findElement(By.id("global_nav_courses_link")));
        courseSvgElement.click();

        caLinkElement = waitElement.until(
                (ExpectedCondition<WebElement>) d -> d.findElement(By.cssSelector("span[data-reactid*=\"$17679\"]")));
        Utils.wait(500);
        caLinkElement.click();

        assignmentListLinkElement = waitElement.until(
                (ExpectedCondition<WebElement>) d -> d.findElement(By.cssSelector("a[href=\"/courses/17679/assignments\"]")));
        Utils.wait(500);
        assignmentListLinkElement.click();

        assignmentLinkElement = waitElement.until(
                (ExpectedCondition<WebElement>) d -> d.findElement(By.cssSelector("a[href=\"https://oc.sjtu.edu.cn/courses/17679/assignments/36947\"]")));
        assignmentLinkElement.click();

        commitButtonElement = waitElement.until(
                (ExpectedCondition<WebElement>) d -> d.findElement(By.cssSelector("div.assignment-buttons")));
        commitButtonElement.click();

        uploadElement = waitElement.until(
                (ExpectedCondition<WebElement>) d -> d.findElement(By.cssSelector("input.input-file")));

        uploadFile = new File(UPLOAD_FILE);
        uploadElement.sendKeys(uploadFile.getAbsolutePath());
        log.info("Success uploaded file {}", UPLOAD_FILE);

        submitButton = driver.findElement(By.id("submit_file_button"));
        submitButton.click();
        log.info("Success submitted file {}", UPLOAD_FILE);

        Utils.wait(10000);
        driver.quit();
    }

    private static void testProfileSetting(WebDriver driver, String locale, String timezone, boolean subscribeEnabled) {
        WebDriverWait waitElement = new WebDriverWait(driver, 10);
        WebElement profileSvgElement, settingLinkElement, editButtonElement,
                subscribeCheckbox, submitButton;
        Select selectLanguageElement, selectTimeZoneElement;

        log.info("Running settings module ...");
        CommonOperation.login(driver, username, password);

        profileSvgElement = waitElement.until(
                (ExpectedCondition<WebElement>) d -> d.findElement(By.id("global_nav_profile_link")));
        profileSvgElement.click();

        settingLinkElement = waitElement.until(
                (ExpectedCondition<WebElement>) d -> d.findElement(By.cssSelector("li[data-reactid*=\"$settings\"] span")));
        Utils.wait(500);
        settingLinkElement.click();

        editButtonElement = waitElement.until(
                (ExpectedCondition<WebElement>) d -> d.findElement(By.cssSelector(".edit_settings_link")));
        Utils.wait(500);
        editButtonElement.click();

        selectLanguageElement = new Select(waitElement.until(
                (ExpectedCondition<WebElement>) d -> d.findElement(By.id("user_locale"))));
        Utils.wait(500);
        selectLanguageElement.selectByValue(locale);
        log.info("User locale has been set to {}", locale);

        selectTimeZoneElement = new Select(waitElement.until(
                (ExpectedCondition<WebElement>) d -> d.findElement(By.id("user_time_zone"))));
        selectTimeZoneElement.selectByValue(timezone);
        log.info("Timezone has been set to {}", timezone);

        subscribeCheckbox = waitElement.until(
                (ExpectedCondition<WebElement>) d -> d.findElement(By.id("user_subscribe_to_emails")));
        if (subscribeCheckbox.isSelected() && !subscribeEnabled ||
            !subscribeCheckbox.isSelected() && subscribeEnabled) {
            subscribeCheckbox.click();
        }
        log.info("Subscribe checkbox has been set to {}", subscribeEnabled);

        submitButton = driver.findElement(By.cssSelector("button[type=\"submit\"]"));
        submitButton.click();
        log.info("Clicked submit button, success configured user settings");

        Utils.wait(10000);
        driver.quit();
    }
}
