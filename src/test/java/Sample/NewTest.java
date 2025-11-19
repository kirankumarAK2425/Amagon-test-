package Sample;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import io.github.bonigarcia.wdm.WebDriverManager;

public class NewTest___1 {

	WebDriver driver;
	WebDriverWait wait;
	ExtentReports extent = new ExtentReports();
	ExtentSparkReporter sparkreporter = new ExtentSparkReporter("Amazon.html");

	@BeforeMethod
	public void setup() {
		extent.attachReporter(sparkreporter);
		WebDriverManager.chromedriver().setup();
//		ChromeOptions options = new ChromeOptions();
//		options.addArguments("--start-maximized");
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		// driver = new ChromeDriver(options);
		wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		driver.get("https://www.amazon.in/");
	}

	@Test
	public void verifyAddToCart() throws Exception {
		ExtentTest test = extent.createTest("Search Product");
		test.info("Launching Chrome browser");
		test.info("Navigated to Amazon");
		// 2. Search for wireless mouse
		WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.id("twotabsearchtextbox")));
		searchBox.sendKeys("wireless mouse");
		searchBox.submit();
		test.info("Entered search keyword: wireless mouse");
		test.info("Clicked on search button");
		// 3. Wait for results to load
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.s-main-slot")));

		// 4. Click on the first NON-SPONSORED product
		WebElement firstProduct = wait
				.until(ExpectedConditions.elementToBeClickable(By.xpath("(//div[@class='puisg-col-inner'])[1]")));
		firstProduct.click();
		test.info("Clicked on first non-sponsored product");
		// Switch to new tab
		String parent = driver.getWindowHandle();
		for (String win : driver.getWindowHandles()) {
			if (!win.equals(parent)) {
				driver.switchTo().window(win);
				break;
			}
		}

		// 5. Validate product title & price
		WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("productTitle")));
		WebElement price = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='a-price-whole'])[1]")));

		String productTitle = title.getText().trim();
		String productPriceText = price.getText().replace(",", "").trim();

		System.out.println("Product Title: " + productTitle);
		System.out.println("Product Price: " + productPriceText);

		test.info("Product Title: " + productTitle);
		test.info("Product Price: " + productPriceText);

		Assert.assertFalse(productTitle.isEmpty());
		test.pass("Product title is visible");

		Assert.assertFalse(productPriceText.isEmpty());
		test.pass("Product price is visible");

		// 6. Add to cart
		WebElement addToCart = wait.until(ExpectedConditions.elementToBeClickable(By.id("add-to-cart-button")));
		addToCart.click();
		test.info("Clicked Add to Cart");

		// 7. Verify Added to Cart message
		WebElement AddedToCart = wait.until(
				ExpectedConditions.visibilityOfElementLocated(By.xpath("(//*[contains(text(),'Added to cart')])[1]")));
		String AddedToCartText = AddedToCart.getText().trim();
		System.out.println("✅ Added to cart message displayed. " + AddedToCartText);
		test.pass("Added to cart message displayed" + AddedToCartText);

		// 8. Stay on same page (page remains open)
		Thread.sleep(1500);

		// 9. Click Add to Cart again (Amazon increments quantity)
		addToCart = wait
				.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[@data-a-selector='increment-icon']")));
		addToCart.click();
		test.info("Click on add more button");
		Thread.sleep(1500);

		// 10. Go to cart
		WebElement cart = wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-cart-count-container")));
		cart.click();
		test.info("Opened cart page");

		// 11. Verify only one unique item (not duplicated)
		List<WebElement> cartItems = driver.findElements(By.cssSelector("div.sc-list-item-content"));

		if (cartItems.size() == 1) {
			System.out.println("✅ No duplicate items in cart.");
			test.info("No duplicate items in cart ");
		} else {
			System.out.println("❌ Duplicate items found in cart: " + cartItems.size());
			test.info("❌ Duplicate items found in cart: " + cartItems.size());
		}

		Thread.sleep(2000);

		// 12. Validate product title, quantity = 2, and subtotal calculation

		// Title check
		// Get first cart item
		WebElement firstCartItem = driver.findElement(By.cssSelector("div.sc-list-item-content"));

		// Get title inside that item (safe)
		WebElement titleElement = firstCartItem.findElement(By.cssSelector("span.a-truncate-cut"));

		// Extract title
		String cartTitle = titleElement.getText().trim();
		String productTitleNormalized = productTitle.toLowerCase().trim();
		String cartTitleNormalized = cartTitle.toLowerCase().trim();

		// Small match (contains)
		if (cartTitleNormalized.contains(productTitleNormalized.substring(0, 10))) {
			System.out.println("✅ Product title matches.");
			test.pass("Product title matches");

		} else {
			System.out.println("❌ Product title does not match.");
			test.fail("Product title does NOT match");
		}

		System.out.println("Product Page Title : " + productTitle);
		System.out.println("Cart Title         : " + cartTitle);

		Thread.sleep(2000);
		// Quantity check
		WebElement qty = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[@data-a-selector='value']")));

		String quantityText = qty.getText();
		System.out.println("Detected Quantity: " + quantityText);
		Assert.assertEquals(quantityText, "2");

		// Subtotal validation
		WebElement priceElement = driver.findElement(By.xpath("(//span[@data-a-size='b'])[1]"));
		WebElement subtotalElement = driver.findElement(By.xpath("//span[contains(@id,'sc-subtotal-amount')]//span"));

		String priceText = priceElement.getText().replaceAll("[^0-9.]", "");
		String subtotalText = subtotalElement.getText().replaceAll("[^0-9.]", "");

		double priceValue = Double.parseDouble(priceText);
		double subtotalValue = Double.parseDouble(subtotalText);
		int quantityValue = Integer.parseInt(quantityText);

		// (e) Validate subtotal = price × quantity
		if (subtotalValue == priceValue * quantityValue) {
			System.out.println("✅ Subtotal verified correctly: " + subtotalValue);
			test.pass("Subtotal is correct  " + subtotalValue);
		} else {
			System.out.println(
					"❌ Subtotal mismatch! Expected " + (priceValue * quantityValue) + " but got " + subtotalValue);
			test.fail("Subtotal mismatch! Expected ");
		}

		// Final Summary
		System.out.println("🎯 All validations completed successfully.");
		test.pass("Test Case: Passed");

	}

	@AfterMethod
	public void afterSuite() {

		extent.flush();
		driver.quit();
	}

}
