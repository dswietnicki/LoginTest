import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;


public class LoginTest {
    private WebDriver driver;
    private final String BASE_URL = "https://www.nazwa.pl/zaloguj-sie";

    @BeforeClass
    public static void setUp() {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\Dawid\\Downloads\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe");
    }

    @Test
    public void loginTest() {
        driver = new ChromeDriver();
        driver.get(BASE_URL);

        new LoginPage(driver)
                .acceptCookieModal()
                .login("tester1233323", "Testowe321!")
                .sendCodeViaEmail();

        new HomePage(driver)
                .hoverAndReadContent()
                .logout();

        driver.quit();
    }
}