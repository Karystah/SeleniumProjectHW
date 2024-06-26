package selenide.sitepage;

import com.codeborne.selenide.AssertionMode;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.testng.SoftAsserts;
//import com.epam.reportportal.service.ReportPortal;
import com.codeborne.selenide.webdriver.EdgeDriverFactory;
import io.qameta.allure.*;
import org.openqa.selenium.*;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static com.codeborne.selenide.Browsers.CHROME;
import static com.codeborne.selenide.Browsers.EDGE;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;

@Epic("Selenium training")
@Feature("Rubber ducks")
@Listeners({ SoftAsserts.class})

public class ObjectRubberDucksTest {
    public static final String LIGHT_PINK = "rgba(255, 204, 204, 1)";
    public static final String LIGHT_GREEN = "rgba(214, 236, 166, 1)";



    @BeforeMethod
    public void setup() {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setBrowserName(CHROME);
        caps.setPlatform(Platform.WINDOWS);

        Configuration.remote = "http://192.168.0.100:4444/wd/hub";
        Configuration.browserCapabilities = caps;

        Configuration.pageLoadTimeout = 5000;
        Configuration.browserSize = "1920x1080";
        Configuration.baseUrl = "https://litecart.stqa.ru/en/";
        Configuration.assertionMode = AssertionMode.SOFT;

        open(Configuration.baseUrl);
    }

    @AfterMethod
    public void teardown(ITestResult testResult) {
        if(testResult.getStatus() == ITestResult.FAILURE) {
            File screenshot = Selenide.screenshot(OutputType.FILE);
            try {
//                ReportPortal.emitLog(testResult.getMethod().getMethodName(), "ERROR", Calendar.getInstance().getTime(), screenshot);
                Allure.addAttachment(testResult.getMethod().getMethodName(), new FileInputStream(screenshot));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        closeWebDriver();
    }

    @Description("Quantity of ducks in cart changes when duck is added to cart. Not logged in user")
    @Story("Add to cart")
    @Test(priority = 7, groups = "addToCart")
    public void addToCartNotLoggingUserTest() {
        $(By.cssSelector("#box-most-popular [alt='Blue Duck']")).click();
        $(By.name("add_cart_product")).click();

        $(By.cssSelector("span.quantity")).shouldHave(text("1"));
    }

    @Description("When user logged in into the system success message is shown")
    @Story("Authorization")
    @Test(priority = 1, groups = "authorization")
    public void ducksCorrectLoginTest() {
        String expectedLoginMessage = "You are now logged in as Test Auto.";

        LoginPage.attemptLogin("xidepo1971@rartg.com", "123456789");
        HomePage.validateSuccessMessage(expectedLoginMessage, LIGHT_GREEN);
    }

    @Description("When user failed to login into the system error message is shown")
    @Story("Authorization")
    @Test(priority = 2, groups = "authorization")
    public void ducksIncorrectLoginTest_2() {
        String expectedErrorMessage = "Wrong password or the account is disabled, or does not exist";

        LoginPage.attemptLogin("stepantsowa.karina@gvdvd.com", "gsaljgxx7");
        HomePage.validateErrorMessage(expectedErrorMessage, LIGHT_PINK);
    }

    @Description("The logging form is displayed when user is not authorized")
    @Story("Authorization")
    @Test(priority = 3, groups = "authorization")
    public void b_isLoginFormDisplayedTest_3() {
        LoginPage.loginFormIsDisplayed();
    }

    @Description("The logging form is not displayed when user is authorized")
    @Story("Authorization")
    @Test(priority = 4, groups = "authorization")
    public void isLoginFormVisibleTest_4(){

        LoginPage.attemptLogin("xidepo1971@rartg.com", "123456789");

        try {
            LoginPage.loginFormIsDisplayed();
        } catch (org.openqa.selenium.NoSuchElementException e) {
            LoginPage.loginFormIsNotDisplayed();
        }
    }

    @Description("Not authorized user can open product from 'Most popular' category")
    @Story("Product categories")
    @Test(priority = 8, groups = "productCategories")
    public void openProductNotLoggedInUserTest() {
        ProductsPage.openProductFromMostPopularBox();
        ProductsPage.openedMostPopularProductIsDisplayed();
    }

    @Description("User cannot add to cart product when quantity field is empty")
    @Story("Product categories")
    @Test(priority = 9, groups = "product")
    public void emptyQuantityProductAlertTest() {
        ProductsPage.getQuantityProductAlert();
        Alert alert = switchTo().alert();
        Assert.assertTrue(alert.getText().contains("Cannot add product to cart. Invalid quantity"));
        alert.accept();
   }

   @Description("Quantity of ducks in cart changes when duck is added to cart. Logged in user")
   @Story("Add to cart")
   @Test(priority = 10, groups = "addToCart")
    public void addToCartLoggingUserTest() {
       LoginPage.attemptLogin("xidepo1971@rartg.com", "123456789");

       ProductsPage.openProductFromMostPopularBox();
       $(By.name("add_cart_product")).click();
       $(By.cssSelector("span.quantity")).shouldHave(text("1"));
    }

    @Description("There is an empty cart message if the product is not added to cart")
    @Story("Add to cart")
    @Test(priority = 5, groups = "addToCart")
    public void openShoppingCartByContentButtonTest_5() {
        String expectedEmptyCartMessage = "There are no items in your cart.";
        ShoppingCartPage.clickOnContentButton();
        ShoppingCartPage.validateEmptyCartMessageText(expectedEmptyCartMessage);
    }

    @Description("User can open shopping cart by clicking on 'Checkout' button")
    @Story("Add to cart")
    @Test(priority = 6, groups = "addToCart")
    public void openShoppingCartByCheckoutButtonTest_6() {
        String expectedEmptyCartMessage = "There are no items in your cart.";

        ShoppingCartPage.clickOnCheckoutButton();
        ShoppingCartPage.validateEmptyCartMessageText(expectedEmptyCartMessage);
    }

    @Description("User is redirected to 'Rubber Ducks' page when he clicks 'Category one' menu option")
    @Story("Menu")
    @Test(priority = 11, groups = "menu")
    public void clickOnRubberDucksMenuButton() {
        String categoryOneTitle = "Rubber Ducks";
        SiteMenuPage.checkCategoryOnePageTitle(categoryOneTitle);
    }

}
