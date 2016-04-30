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

import com.google.common.base.CharMatcher;

public class Main {

	public static int renthopPageLimit = 1;
	public static int craigslistPageLimit = 1;
	public static int streetEasyPageLimit = 1;
	public static int listing_count=0;
	public static int nomap_count=0;
	public static int noimage_count=0;

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
			
			neighborhood=neighborhood.replace(" ", "");
			neighborhood=neighborhood.toLowerCase();
			ApartmentObject aptObj = new ApartmentObject(neighborhood, aptType, Integer.parseInt(rent));
			renthopFileWriter.write(aptObj.toString() + "\n");
		}
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
				neighbor=neighbor.toLowerCase();
				neighbor=neighbor.replace(" ","");
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
		int minprice=1000;
		int maxprice=1250;
		for(int j=0;j<1;j++)
		{
		maxprice+=500;
		minprice+=500;
		for(int i = 0; i < craigslistPageLimit; i++){
			try {
				getCraigsPages(i,minprice,maxprice);
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
			catch(Exception e){
				System.out.println("lel");
			}
		}
		}
	}

	public static void getCraigsPages(int page,int minprice, int maxprice) throws IOException{
		
		
		String navigationString = "https://newyork.craigslist.org/search/mnh/aap";
		
		if(page != 0){
			navigationString = navigationString.concat("?s="+(100*page)+"&min_price="+minprice+"&max_price="+maxprice);
		}
		else
		{
			navigationString = navigationString.concat("?min_price="+minprice+"&max_price="+maxprice);
		}
		
		craigslistBrowser.navigate().to(navigationString);
		// Javascript part is optional. Just to see if allows to traverse without getting caught 
		((JavascriptExecutor)craigslistBrowser).executeScript("scroll(0,400)");
		List<WebElement> text = craigslistBrowser.findElements(By.className("row"));


		for(int i = 0; i < text.size(); i++){
			listing_count++;
			String temp = text.get(i).getText();

			temp = CharMatcher.inRange((char)0, (char)128).retainFrom(temp);
			WebElement thisElement = text.get(i);
			boolean hasImage = false;
			int hasMap=0;
			
			int numImages = 0;
			
			try {
				List<WebElement> x = thisElement.findElements(By.className("maptag"));
				x.get(0);
				hasMap = 1;
			} catch (Exception e) {
				nomap_count++;
				//System.out.println("nomap");
			}
			//System.out.println(hasMap);
			try {
				List<WebElement> x = thisElement.findElements(By.className("swipe-wrap"));
				x.get(0).getText();
				hasImage = true;
			} catch (Exception e) {
				noimage_count++;
				numImages = 0;
			}
			if(hasImage){
				WebElement newImages = thisElement.findElement(By.className("swipe-wrap"));
				try {
					if(newImages != null){
						List<WebElement> children = newImages.findElements(By.xpath(".//div"));
						numImages = children.size();
					}
				} catch (NoSuchElementException e) {
					// TODO: handle exception
					numImages = 1;
				}
			}	

			ApartmentObject apt = new ApartmentObject();
			apt.isCraigsList = true;
			apt.numImages = numImages;
			apt.description = thisElement.findElement(By.className("hdrlnk")).getText();
			apt.hasMap=hasMap;

			int rentStart = temp.indexOf("$");
			while(temp.charAt(rentStart +1) >= '0' && temp.charAt(rentStart +1) <= '9'){
				apt.rent += temp.charAt(rentStart+1) - '0';
				apt.rent *= 10;
				rentStart++;
			}
			apt.rent /= 10;
			String lowerCase = temp.toLowerCase();
			int bedroomIndex = lowerCase.indexOf("br");
			if(bedroomIndex == -1){
				bedroomIndex = lowerCase.indexOf("bedroom");
			}
			if(bedroomIndex == -1){ // No info as bedroom or br.
				apt.numBedrooms = 1;
			}

			else if(bedroomIndex > 0 && lowerCase.charAt(bedroomIndex-1) >= '0' && lowerCase.charAt(bedroomIndex-1) <= '9'){
				apt.numBedrooms = lowerCase.charAt(bedroomIndex-1) - '0';
			}
			else if(bedroomIndex >= 2 && lowerCase.charAt(bedroomIndex-2) >= '0' && lowerCase.charAt(bedroomIndex-2) <= '9'){
				apt.numBedrooms = lowerCase.charAt(bedroomIndex -2) - '0';
			}
			else{
				apt.numBedrooms = 1;
			}

			int neighborhoodStartIndex = temp.lastIndexOf('(');
			int neighborhoodEndIndex = temp.lastIndexOf(')');
			String neighborhood = temp.substring(neighborhoodStartIndex+1, neighborhoodEndIndex);
			neighborhood=neighborhood.toLowerCase();
			neighborhood=neighborhood.replace(" ", "");
			if(neighborhood.contains("\\")){
				apt.neighborhood = neighborhood.split("\\")[0];
			}
			else if(neighborhood.contains("/")){
				apt.neighborhood = neighborhood.split("/")[0];
			}
			else{
				apt.neighborhood = neighborhood;
			}
			craigslistFileWriter.write(apt + "\n");
		}
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
		
		System.out.println("total listings "+listing_count);
		System.out.println("listings with no images "+noimage_count);
		System.out.println("listings with no map location "+nomap_count);
	}
}
