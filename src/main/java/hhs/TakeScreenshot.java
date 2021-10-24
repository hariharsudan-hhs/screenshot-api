package hhs;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Base64;
import java.util.concurrent.TimeUnit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.imageio.ImageIO;

import static spark.Spark.*;

public class TakeScreenshot {

	public static void main(String[] args) throws IOException {

		/*
		 * spark will run on port 7000
		 */
		port(7000);

		/*
		 * get request takes the url of the website as a parameter
		 */
		get("/take-screenshot/:url", new Route() {
			public Object handle(Request req, Response res) throws Exception {
				String url = req.params("url");
				new TakeScreenshot().takeScreenshot(url);
				res.type("application/json");
				return "{\"message\":\"200: Success - Screenshot saved in your Desktop\"}";
			}
		});

		/*
		 * notFound will handle bad requests
		 */
		notFound(new Route() {
			public Object handle(Request req, Response res) throws Exception {
				res.type("application/json");
				return "{\"message\":\"404: Bad Request\"}";
			}
		});

		/*
		 * internalServerError will handle server side errors
		 */
		internalServerError(new Route() {
			public Object handle(Request req, Response res) throws Exception {
				res.type("application/json");
				return "{\"message\":\"500: Server Error\"}";
			}
		});
	}

	/*
	 * takeScreenshot(String url) is responsible for taking screenshots using
	 * Selenium and Chrome Driver
	 * 
	 * Screenshot will be stored under src/main/resources/screenshots/ folder in the
	 * workspace
	 */
	private void takeScreenshot(String url) {
		System.setProperty("webdriver.chrome.driver", "src/main/resources/drivers/chromedriver.exe");
		ChromeOptions options = new ChromeOptions();
        options.addArguments("headless");
		WebDriver driver = new ChromeDriver(options);
		driver.get(url);
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		Screenshot s = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(1000)).takeScreenshot(driver);
		try {
			ImageIO.write(s.getImage(), "PNG", new File("C:\\Users\\" + System.getProperty("user.name") +"\\Desktop\\Screenshot.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		driver.quit();
	}
}
