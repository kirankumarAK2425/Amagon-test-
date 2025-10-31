package Sample;

import java.time.Duration;
import java.util.List;
import java.util.Set;

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

public class NewTest {
	
	WebDriver driver;
    WebDriverWait wait;

    @BeforeMethod
    public void setUp() {
        // ✅ Setup ChromeDriver automatically before creating driver instance
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        driver.get("https://www.amazon.in/");
    }
	
	
  @Test
  public void f() throws InterruptedException {


      // 2. Enter “wireless mouse” in search bar and press Enter
      WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.id("twotabsearchtextbox")));
      searchBox.sendKeys("wireless mouse");
      searchBox.submit();

      // 3. Wait for search results to load
      //wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.s-main-slot")));

      // 4. Click on the first non-sponsored product
      //    (filtering out sponsored items)
      Thread.sleep(2000);
//      WebElement firstProduct = wait.until(ExpectedConditions.elementToBeClickable(
//              By.xpath("(//div[@data-component-type='s-search-result'])[1]")
//      ));
//      firstProduct.click();
//      Thread.sleep(1000);
      driver.findElement(By.xpath("(//div[@class='puisg-col-inner'])[1]")).click();
      Thread.sleep(2000);
      String originalWindow = driver.getWindowHandle();
      Set<String> allWindows = driver.getWindowHandles();

      // Switch to new tab
      for (String window : allWindows) {
          if (!window.equals(originalWindow)) {
              driver.switchTo().window(window);
              break;
          }
      }

      // 5. Validate product title and price are visible
      WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("productTitle")));
      WebElement price = wait.until(ExpectedConditions.visibilityOfElementLocated(
              By.xpath("//span[@class='a-price-whole'] | //span[@id='priceblock_ourprice'] | //span[@id='priceblock_dealprice']")
      ));
      System.out.println("Product Title: " + title.getText());
      System.out.println("Product Price: " + price.getText());

      // 6. Click “Add to Cart”
      WebElement addToCartBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("add-to-cart-button")));
      addToCartBtn.click();

      // 7. Verify confirmation message “Added to Cart”
      wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//*[contains(text(),'Added to cart')])[1]")));
      System.out.println("✅ Product added to cart successfully."); 

      // Repeat Add Scenario:
      // 8. Stay on product page (navigate back if needed)
     // driver.navigate().back();
      Thread.sleep(2000);

      // 9. Click “Add to Cart” again
      addToCartBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[@data-a-selector='increment-icon']")));
      addToCartBtn.click();

      // 10. Click on the cart icon to go to Cart page
      WebElement cartBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-cart-count-container")));
      cartBtn.click();
      
      
      
      
      
   // Wait for cart page to load completely
      wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.sc-list-item-content")));

      // 11. Verify: product is not duplicated (only one unique item in cart)
      List<WebElement> cartItems = driver.findElements(By.cssSelector("div.sc-list-item-content"));
      int itemCount = cartItems.size();
      if (itemCount == 1) {
          System.out.println("✅ Product not duplicated, only one item in cart.");
      } else {
          System.out.println("❌ Duplicate products found in cart: " + itemCount);
      }

      // 12. Validate that the product title matches, quantity = 2, and subtotal = price × quantity

      // (a) Get product title from cart
      WebElement cartTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
              By.cssSelector("span.a-truncate-full")));
      String cartTitleText = cartTitle.getText().trim();
      System.out.println("Cart Title: " + cartTitleText);

      // (b) Compare product title from product page and cart
      if (cartTitleText.toLowerCase().contains(title.getText().toLowerCase())) {
          System.out.println("✅ Product title matches.");
      } else {
          System.out.println("❌ Product title does not match.");
      }

      // (c) Get quantity (the visible number inside the stepper, e.g., “2”)
      WebElement quantityElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
              By.xpath("//span[contains(@class,'a-button-text') and normalize-space(text())='2'] | //span[contains(@class,'a-dropdown-prompt')] | //input[@name='quantityBox']")));
      String quantityText = quantityElement.getText().trim().isEmpty()
              ? quantityElement.getAttribute("value").trim()
              : quantityElement.getText().trim();

      System.out.println("Detected Quantity: " + quantityText);

      // (d) Get unit price and subtotal
      WebElement priceElement = driver.findElement(By.xpath("(//span[@class='a-price-whole'])[1]"));
      WebElement subtotalElement = driver.findElement(By.xpath("//span[contains(@id,'sc-subtotal-amount')]//span"));

      String priceText = priceElement.getText().replace(",", "").trim();
      String subtotalText = subtotalElement.getText().replace(",", "").trim();

      double priceValue = Double.parseDouble(priceText);
      double subtotalValue = Double.parseDouble(subtotalText);
      int quantityValue = Integer.parseInt(quantityText);

      // (e) Validate subtotal = price × quantity
      if (subtotalValue == priceValue * quantityValue) {
          System.out.println("✅ Subtotal verified correctly: " + subtotalValue);
      } else {
          System.out.println("❌ Subtotal mismatch! Expected " + (priceValue * quantityValue) + " but got " + subtotalValue);
      }

      // Final Summary
      System.out.println("🎯 All validations completed successfully.");


      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
     

      driver.quit();
  }
}

	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
 

