package testing_omer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;

public class Main {

	public static int renthopPageLimit = 0;
	public static int craigslistPageLimit = 0;
	public static int streetEasyPageLimit = 0;

	public static WebDriver renthopBrowser = new FirefoxDriver();
	public static WebDriver craigslistBrowser = new FirefoxDriver();
	public static WebDriver streetEasyBrowser = new FirefoxDriver();
	
	public static FileWriter renthopFileWriter;
	public static FileWriter craigslistFileWriter;
	public static FileWriter streetEasyFileWriter;
	
	public static void renthopDriver(){
		for(int i = 0; i < renthopPageLimit; i++){
			try {
				getRenthopPages(i);
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			} catch (NoSuchElementException e){
				System.out.println("No such element? " + i);
			} catch (Exception e){
				System.out.print("Exception? ");
				e.printStackTrace();
			} finally {
				System.out.println("Final index: " + i);
			}
		}
	}

	public static void getRenthopPages(int page) throws IOException{
		renthopBrowser.navigate().to("https://www.renthop.com/search/nyc?page="+page);

		String text = renthopBrowser.findElement(By.id("search-results-box")).getText();
		String[] listings = text.split("Check Availability");
		
		int aptType = 0;
		for(String s: listings){
			int index = 0;
			if(s.charAt(0) == '\n')
				s = s.replaceFirst("\n", "");

			if(s.contains("No Fee"))
				index = 1;

			String parsedString[] = s.split("\n");
			if(s.contains("Studio"))
				aptType = 0;
			else if(s.contains("BR,")){
				aptType = Integer.parseInt(s.substring(s.indexOf("BR")-1, s.indexOf("BR")));
			}

			String rent = parsedString[index+2].substring(1).replace(",","");
			String[] neighborhoods = parsedString[index+1].split(", ");
			String neighborhood = "";
			if(neighborhoods.length >= 3)
				neighborhood = neighborhoods[neighborhoods.length-3];
			else if(neighborhoods.length == 2)
				neighborhood = neighborhoods[neighborhoods.length-2];
			else 
				neighborhood = neighborhoods[0];
			ApartmentObject aptObj = new ApartmentObject(neighborhood, aptType, Integer.parseInt(rent));
			renthopFileWriter.write(aptObj.toString() + "\n");
//			System.out.println("An apartment: " + aptObj);
		}
		renthopFileWriter.flush();
	}
	
	public static void streetEasyDriver(){
		for(int i = 0; i < streetEasyPageLimit; i++){
			try {
				getStreetEasyPages((i+1));
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}

	public static void getStreetEasyPages(int page) throws IOException{
		streetEasyBrowser.navigate().to("http://streeteasy.com/for-rent/manhattan?page="+page);
		
		List<WebElement> priceList = streetEasyBrowser.findElements(By.className("price"));
		List<WebElement> numBedrooms = streetEasyBrowser.findElements(By.className("first_detail_cell"));
		
		List<WebElement> neighborhood = streetEasyBrowser.findElements(By.className("details_info"));
		ArrayList<ApartmentObject> aptList = new ArrayList<>();
		for(int i = 0; i < 15; i++){
			aptList.add(new ApartmentObject());
		}
		
		int index = 0;
		for(int i = 0; i < neighborhood.size(); i++){
			String text = neighborhood.get(i).getText();
			if(text.contains(" in ")){
				String neighbor = text.split(" in ")[1];
				aptList.get(index++).neighborhood = neighbor;
			}
		}
		index = 0;
		for(int i = 0; i < priceList.size(); i++){
			String text = priceList.get(i).getText();
			if(!text.equalsIgnoreCase("")){
				text = text.replace("$", "").replace(",", "");
				aptList.get(index++).rent = Double.parseDouble(text);
			}
		}
		
		index = 0;
		for(int i = 0; i < numBedrooms.size(); i++){
			String text = numBedrooms.get(i).getText();
			aptList.get(index++).numBedrooms = Integer.parseInt(text.split(" ")[0]);
		}
		aptList.remove(aptList.size()-1);
		
		for(ApartmentObject apt: aptList)
			streetEasyFileWriter.write(apt + "\n");
		
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
			} catch(UnreachableBrowserException e){
				System.out.println();
				e.printStackTrace();
			}
		}
	}

	public static void getCraigsPages(int page) throws IOException{
		String navigationString = "https://newyork.craigslist.org/search/mnh/aap";
		if(page != 0){
			navigationString = navigationString.concat("?s="+(100*page));
		}
		craigslistBrowser.navigate().to(navigationString);
		// Javascript part is optional. Just to see if allows to traverse without getting caught 
		((JavascriptExecutor)craigslistBrowser).executeScript("scroll(0,400)");
		List<WebElement> text = craigslistBrowser.findElements(By.className("row"));
		List<WebElement> images = craigslistBrowser.findElements(By.className("slider-info"));
		for(WebElement y: images){
			System.out.println("Val: " + y.getText());
		}
		
		for(int i = 0; i < text.size(); i++){
			WebElement thisElement = text.get(i).findElement(By.className("slider-info"));
			if(thisElement != null){
				System.out.println("Image? " + thisElement.getText());
			}
			
			String temp = text.get(i).getText();
			ApartmentObject apt = new ApartmentObject();
			apt.isCraigsList = true;
			List<WebElement> imageInfo = text.get(i).findElements(By.className("slider-info"));
//			if(imageInfo != null){ // Listing contains an image
//				for(WebElement x: imageInfo)
//					System.out.println("Value? " + x.getText());
//			}
//			if(temp.contains("image")){ 
//				
//				apt.hasImage = true;
//			}
//			System.out.println("Temp: "+temp);
//			craigslistFileWriter.write(temp + "\n");
		}

		craigslistFileWriter.close();
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		renthopFileWriter = new FileWriter("Renthop/RenthopInput.txt", true);
		streetEasyFileWriter = new FileWriter("StreetEasy/StreetEasyInput.txt");
		craigslistFileWriter = new FileWriter("Craigslist/CraigslistInput.txt");
		
		Thread renthopThread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try{
					renthopDriver();
					renthopFileWriter.close();
				} catch (IOException e){
					e.printStackTrace();
				} finally{
					renthopBrowser.close();
				}
			}
		});


		Thread craigslistThread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try{
					craigslistDriver();
					craigslistFileWriter.close();
				} catch (IOException e){
					e.printStackTrace();
				} finally{
					craigslistBrowser.close();
				}
			}
		});

		
		Thread streetEasyThread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try{
					streetEasyDriver();
					streetEasyFileWriter.close();
				} catch (IOException e){
					e.printStackTrace();
				} finally{
					streetEasyBrowser.close();
				}
			}
		});
		
		renthopThread.start();
		craigslistThread.start();
		streetEasyThread.start();
	}
}
