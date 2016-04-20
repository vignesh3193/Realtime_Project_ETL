package testing_omer;

import java.io.*;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class renthop {

	public static WebDriver driver = new FirefoxDriver();

	public static void getInput(String page) throws IOException, InterruptedException{
			driver.navigate().to("https://www.renthop.com/search/nyc?page="+page);
			String text = driver.findElement(By.id("search-results-box")).getText();
			FileWriter fileWriter = new FileWriter("renthop-page-"+page+".txt");
//			System.out.println("Text: "+ text);
			String[] listings = text.split("Check Availability");
			
			for(String s: listings){
				int index = 0;
//				s = s.replaceFirst("\n", "");
				if(s.charAt(0) == '\n')
					s = s.replaceFirst("\n", "");
//				System.out.println("s: "+ s);
				if(s.contains("No Fee"))
					index = 1;
				String parsedString[] = s.split("\n");
				
//				System.out.println("split: "+ parsedString[0] + "\t" + parsedString[1]);
				ApartmentObject aptObj = new ApartmentObject(parsedString[index], parsedString[index+2], parsedString[index+1]);
				fileWriter.write(aptObj.toString());
				fileWriter.write("\n");
//				System.out.println("An apartment: " + aptObj);
			}

			fileWriter.close();
			driver.close();
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		getInput("0");
		driver.close();
	}
}
	