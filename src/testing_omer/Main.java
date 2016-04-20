package testing_omer;

import java.io.*;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class Main {

	public static int renthopPageLimit = 10;
	public static int craigslistPageLimit = 10;
	
	public static WebDriver renthopBrowser = new FirefoxDriver();
	public static WebDriver craigslistBrowser = new FirefoxDriver();
	
	public static void renthopDriver(){
		for(int i = 0; i < renthopPageLimit; i++){
			try {
				getRenthopPages(i);
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
	
	public static void getRenthopPages(int page) throws IOException{
		renthopBrowser.navigate().to("https://www.renthop.com/search/nyc?page="+page);

		String text = renthopBrowser.findElement(By.id("search-results-box")).getText();
		String[] listings = text.split("Check Availability");
		FileWriter fileWriter = new FileWriter("renthop-page-"+page+".txt");
		for(String s: listings){
			int index = 0;
			if(s.charAt(0) == '\n')
				s = s.replaceFirst("\n", "");

			if(s.contains("No Fee"))
				index = 1;
			
			String parsedString[] = s.split("\n");
			ApartmentObject aptObj = new ApartmentObject(parsedString[index], parsedString[index+2], parsedString[index+1]);
			fileWriter.write(aptObj.toString());
//			System.out.println("An apartment: " + aptObj);
		}

		fileWriter.close();
	}
	
	public static void craigslistDriver(){
		for(int i = 0; i < craigslistPageLimit; i++){
			try {
				getCraigsPages(i);
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
	
	public static void getCraigsPages(int page) throws IOException{
		craigslistBrowser.navigate().to("https://newyork.craigslist.org/search/mnh/aap?s="+(100*page));
		// Javascript part is optional. Just to see if allows to traverse without getting caught 
//		((JavascriptExecutor)craigslistBrowser).executeScript("scroll(0,400)");
		List<WebElement> text = craigslistBrowser.findElements(By.className("row"));
		FileWriter fileWriter = new FileWriter("craigslist-page-"+page+".txt");

		for(int i = 0; i < text.size(); i++){
			String temp=text.get(i).getText();
			fileWriter.write(temp);
			fileWriter.write("\n");
		}
		
		fileWriter.close();
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		Thread renthopThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				renthopDriver();
			}
		});
		renthopThread.start();
		
		Thread craigslistThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				craigslistDriver();
			}
		});
		craigslistThread.start();
		
		renthopBrowser.close();
		craigslistBrowser.close();
	}
}
