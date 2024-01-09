import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;

public class LoginTest extends GmailServiceActions {

    private WebDriver driver;
    private static final By MODAL_PATH = By.xpath("//div[@class='modal-box']");
    private static final By ACCEPT_MODAL_WINDOW = By.xpath("//div[@class='modal-box__buttons']//button[contains(text(),'Akceptuję wszystkie')]");
    private static final String BASE_URL = "https://www.nazwa.pl/zaloguj-sie";
    private static final By LOGIN_PATH = By.xpath("//input[@class='login']");
    private static final By PASSWORD_PATH = By.xpath("//input[@class='password']");
    private static final By SUBMIT_BUTTON = By.xpath("//div[@class='button-maker']//input[@type='submit']");
    private static final By SEND_EMAIL_BUTTON = By.xpath("//div[@class='authenticationCodeItem']/a[contains(text(),'e-mail')]");
    private static final By VERIFICATION_CODE_PATH = By.xpath("//div[@class='authenticationCodeItem'][1]/input[@class='authenticationCode']");
    private static final By BUTTON_LOCATOR = By.xpath("//a[contains(text(),'Panel Klienta')]");
    private static final By LOGIN_USER_NAME = By.xpath("//div[@id='login_user_name']");
    private static final By LOGOUT_BUTTON = By.xpath("//div[@id='login_box']/a[contains(text(),'Wyloguj')]");

    String Login = "login";
    String Password = "haslo";

    @BeforeClass
    public static void setUp() {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\Dawid\\Downloads\\chromedriver-win32\\chromedriver-win32\\chromedriver.exe");
    }

    @Test
    public void Login() {
        driver = new ChromeDriver();
        driver.get(BASE_URL);
        if (driver.findElement(MODAL_PATH).isDisplayed()) {
            try {
                driver.findElement(ACCEPT_MODAL_WINDOW).click();
            } catch (Exception e) {
                System.out.println("Cannot accept cookie" + e);
            }
            fillData();
            sentCodeViaEmail();
            checkUserAction();
            logout();
            driver.quit();
        }
    }

    private void fillData() {
        try {
            driver.findElement(LOGIN_PATH).sendKeys(Login);
            driver.findElement(PASSWORD_PATH).sendKeys(Password);
            driver.findElement(SUBMIT_BUTTON).click();
        } catch (Exception e) {
            System.out.println("Cannot login" + e);
        }
    }

    private void sentCodeViaEmail() {
        driver.findElement(SEND_EMAIL_BUTTON).click();
        try {
            Thread.sleep(20000);
        } catch (Exception e) {
            System.out.println(e);
        }
        String verificationCode = new GmailServiceActions().EmailsBody();
        driver.findElement(VERIFICATION_CODE_PATH).sendKeys(verificationCode);
        driver.findElement(SUBMIT_BUTTON).click();
    }

    private void checkUserAction() {
        hoverAndReadContent(driver, BUTTON_LOCATOR);
    }

    public void hoverAndReadContent(WebDriver driver, By locator) {
        try {
            Actions actions = new Actions(driver);
            WebElement button = driver.findElement(locator);
            actions.moveToElement(button).perform();
            Assert.assertEquals(driver.findElement(LOGIN_USER_NAME).getText(), Login);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void logout() {
        driver.findElement(LOGOUT_BUTTON).click();
    }
}