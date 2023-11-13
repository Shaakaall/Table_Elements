package caw.Tests;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import caw.POJO.Person;
import io.github.bonigarcia.wdm.WebDriverManager;

public class Runner {

	public static void main(String[] args) throws InterruptedException, JsonIOException, JsonSyntaxException,
			FileNotFoundException, JsonProcessingException {

		// Initiating the browser driver
		WebDriverManager.chromedriver().setup();
		WebDriver driver = new ChromeDriver();

		driver.manage().window().maximize();

		// Navigating to the desired URL
		driver.get("https://testpages.herokuapp.com/styled/tag/dynamic-table.html");

		driver.findElement(By.cssSelector("summary")).click();
		driver.findElement(By.id("jsondata")).clear();

		// Parsing the Json file
		JsonArray obj = (JsonArray) JsonParser.parseReader(
				new FileReader(System.getProperty("user.dir") + "//src//main//java//caw//Data//Entries.json"));

		// Sending the input data for the table
		driver.findElement(By.id("jsondata")).sendKeys(obj.toString());

		driver.findElement(By.id("refreshtable")).click();
		WebElement table = driver.findElement(By.id("dynamictable"));

		// Retrieving and storing all the records
		List<WebElement> allEntries = table.findElements(By.tagName("tr"));

		// Initiating POJO
		Person person = new Person();

		ObjectMapper map = new ObjectMapper();

		// Iterating through all the entries
		for (int row = 1; row < allEntries.size(); row++) {

			// Retrieving and storing cell values for a record
			List<WebElement> data = allEntries.get(row).findElements(By.tagName("td"));
            
			// Storing respective data into object
			person.setName(data.get(0).getText());
			person.setAge(Integer.parseInt(data.get(1).getText()));
			person.setGender(data.get(2).getText());
			
			// Converting the object into respective string value
			String value = map.writeValueAsString(person);
			
			// Asserting the records
			Assert.assertEquals(value, obj.get(row - 1).toString());
		}
		System.out.println("The table has been updated correctly with the expected data");
		driver.quit();
	}

}
