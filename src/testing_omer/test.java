
package testing_omer;

import java.io.*;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class test {

	public static WebDriver driver = new FirefoxDriver();
	
	public static void getInput(String page) throws IOException, InterruptedException{
			driver.navigate().to("https://newyork.craigslist.org/search/aap?query=manhattan");
			List<WebElement> text = driver.findElements(By.className("row"));
			
			int count=0;
			FileWriter fileWriter = new FileWriter("tryzz-"+page+".txt");
			while(!text.isEmpty())
			{
				String temp=text.get(count).getText();
				//System.out.println(temp);
				fileWriter.write(temp);
				fileWriter.write("\n");
				count++;
			}
			
			System.out.println(text);
			
			
			fileWriter.close();
			driver.close();
//			Thread.sleep(100);
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		getInput("0");
		driver.close();
	}
}