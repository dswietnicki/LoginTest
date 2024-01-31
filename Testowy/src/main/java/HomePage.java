import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;


public class HomePage {
    private final WebDriver driver;

    private final By BUTTON_LOCATOR = By.xpath("//a[contains(text(),'Panel Klienta')]");
    private final By LOGIN_USER_NAME = By.xpath("//div[@id='login_user_name']");
    private final By LOGOUT_BUTTON = By.xpath("//div[@id='login_box']/a[contains(text(),'Wyloguj')]");

    public HomePage(WebDriver driver) {
        this.driver = driver;
    }

    public HomePage hoverAndReadContent() {
        try {
            Actions actions = new Actions(driver);
            WebElement button = driver.findElement(BUTTON_LOCATOR);
            actions.moveToElement(button).perform();
            Assert.assertEquals(driver.findElement(LOGIN_USER_NAME).getText(), "tester1233323");
        } catch (Exception e) {
            System.out.println(e);
        }
        return this;
    }

    public HomePage logout() {
        driver.findElement(LOGOUT_BUTTON).click();
        return this;
    }
}