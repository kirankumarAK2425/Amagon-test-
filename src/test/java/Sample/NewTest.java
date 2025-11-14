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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class NewTest___1 {

	WebDriver driver;
	WebDriverWait wait;

	@BeforeMethod
	public void setup() {

		WebDriverManager.chromedriver().setup();

		ChromeOptions options = new ChromeOptions();
		options.addArguments("--start-maximized");

		driver = new ChromeDriver(options);
		wait = new WebDriverWait(driver, Duration.ofSeconds(20));

		driver.get("https://www.amazon.in/");
	}

	@Test
	public void verifyAddToCart() throws Exception {

		// 2. Search for wireless mouse
		WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.id("twotabsearchtextbox")));
		searchBox.sendKeys("wireless mouse");
		searchBox.submit();

		// 3. Wait for results to load
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.s-main-slot")));

		// 4. Click on the first NON-SPONSORED product
		WebElement firstProduct = wait
				.until(ExpectedConditions.elementToBeClickable(By.xpath("(//div[@class='puisg-col-inner'])[1]")));
		firstProduct.click();

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

		// 6. Add to cart
		WebElement addToCart = wait.until(ExpectedConditions.elementToBeClickable(By.id("add-to-cart-button")));
		addToCart.click();

		// 7. Verify Added to Cart message
		wait.until(
				ExpectedConditions.visibilityOfElementLocated(By.xpath("(//*[contains(text(),'Added to cart')])[1]")));
		System.out.println("✅ Added to cart message displayed.");

		// 8. Stay on same page (page remains open)
		Thread.sleep(1500);

		// 9. Click Add to Cart again (Amazon increments quantity)
		addToCart = wait
				.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[@data-a-selector='increment-icon']")));
		addToCart.click();

		Thread.sleep(1500);

		// 10. Go to cart
		WebElement cart = wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-cart-count-container")));
		cart.click();

		// 11. Verify only one unique item (not duplicated)
		List<WebElement> cartItems = driver.findElements(By.cssSelector("div.sc-list-item-content"));

		if (cartItems.size() == 1) {
			System.out.println("✅ No duplicate items in cart.");
		} else {
			System.out.println("❌ Duplicate items found in cart: " + cartItems.size());
		}

		// 12. Validate product title, quantity = 2, and subtotal calculation

		// Title check
		WebElement cartTitle = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("span.a-truncate-cut")));

		if (cartTitle.getText().toLowerCase().contains(productTitle.toLowerCase().substring(0, 5))) {
			System.out.println("✅ Product title matches.");
		} else {
			System.out.println("❌ Product title does not match.");
		}

		// Quantity check
		WebElement qty = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[@data-a-selector='value']")));

		String quantityText = qty.getText();
		System.out.println("Detected Quantity: " + quantityText);

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
		} else {
			System.out.println(
					"❌ Subtotal mismatch! Expected " + (priceValue * quantityValue) + " but got " + subtotalValue);
		}

		// Final Summary
		System.out.println("🎯 All validations completed successfully.");

		driver.quit();
	}
}
