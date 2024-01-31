import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage {
    private final WebDriver driver;

    private final By MODAL_PATH = By.xpath("//div[@class='modal-box']");
    private final By ACCEPT_MODAL_WINDOW = By.xpath("//div[@class='modal-box__buttons']//button[contains(text(),'Akceptuję wszystkie')]");
    private final By LOGIN_PATH = By.xpath("//input[@class='login']");
    private final By PASSWORD_PATH = By.xpath("//input[@class='password']");
    private final By SUBMIT_BUTTON = By.xpath("//div[@class='button-maker']//input[@type='submit']");
    private final By SEND_EMAIL_BUTTON = By.xpath("//div[@class='authenticationCodeItem']/a[contains(text(),'e-mail')]");
    private final By VERIFICATION_CODE_PATH = By.xpath("//div[@class='authenticationCodeItem'][1]/input[@class='authenticationCode']");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    public LoginPage acceptCookieModal() {
        if (driver.findElement(MODAL_PATH).isDisplayed()) {
            try {
                driver.findElement(ACCEPT_MODAL_WINDOW).click();
            } catch (Exception e) {
                System.out.println("Cannot accept cookie" + e);
            }
        } return this;
    }

    public LoginPage login(String username, String password) {
        try {
            driver.findElement(LOGIN_PATH).sendKeys(username);
            driver.findElement(PASSWORD_PATH).sendKeys(password);
            driver.findElement(SUBMIT_BUTTON).click();
        } catch (Exception e) {
            System.out.println("Cannot login" + e);
        }
        return this;
    }

    public LoginPage sendCodeViaEmail() {
        driver.findElement(SEND_EMAIL_BUTTON).click();
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            System.out.println(e);
        }
        String verificationCode = new GmailServiceActions().EmailsBody();
        driver.findElement(VERIFICATION_CODE_PATH).sendKeys(verificationCode);
        driver.findElement(SUBMIT_BUTTON).click();
        return  this;
    }

}