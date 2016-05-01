package aptReduceCrawler;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.google.common.base.CharMatcher;

public class Main {

	//	Limit the pages so we do not get empty pages
	public static int renthopPageLimit = 1;
	public static int craigslistPageLimit = 1;
	public static int streetEasyPageLimit = 1;

	//	Collect statistics on data
	public static int listing_count=0;
	public static int nomap_count=0;
	public static int noimage_count=0;

	//	Instantiate the drivers for each data sources
	public static WebDriver renthopBrowser = new FirefoxDriver();
	public static WebDriver craigslistBrowser = new FirefoxDriver();
	public static WebDriver streetEasyBrowser = new FirefoxDriver();

	//	Make file writers global so they can be accessed everywhere.
	public static FileWriter renthopFileWriter;
	public static FileWriter craigslistFileWriter;
	public static FileWriter streetEasyFileWriter;

	public static void renthopDriver(){
		for(int i = 0; i < renthopPageLimit; i++){
			try {
				//Get pages based on the limit
				getRenthopPages(i);
				Thread.sleep(5);
			} catch (Exception e){ // Catch the exception and print out the stack trace
				//The reason we catch is to allow crawler to continue. No more crashing.
				System.out.print("Exception? ");
				e.printStackTrace();
			} finally {
				//Print the index of page
				System.out.println("Final index: " + i);
			}
		}
	}

	public static void getRenthopPages(int page) throws IOException{
		//Navigate to the page
		renthopBrowser.navigate().to("https://www.renthop.com/search/nyc?page="+page);

		//Get all listings
		String allListings = renthopBrowser.findElement(By.id("search-results-box")).getText();
		//Separate them by the keywords Check Availability
		String[] listings = allListings.split("Check Availability");

		int aptType = 0;
		//For each listing, parse the text and create the corresponding apartment object.
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
			//Write the apartment info to the text file.
			renthopFileWriter.write(aptObj.toString() + "\n");
		}
	}

	public static void streetEasyDriver(){
		for(int i = 0; i < streetEasyPageLimit; i++){
			try {
				//Get pages based on the limit
				getStreetEasyPages((i+1));
				Thread.sleep(100);
			} catch (Exception e) {
				//Continue parsing if something went wrong with this page
				e.printStackTrace();
			}
		}
	}

	public static void getStreetEasyPages(int page) throws IOException{
		//		Navigate to the corresponding page
		streetEasyBrowser.navigate().to("http://streeteasy.com/for-rent/manhattan?page="+page);

		//		Get price lists, number of bedrooms and neighborhoods
		List<WebElement> priceList = streetEasyBrowser.findElements(By.className("price"));
		List<WebElement> numBedrooms = streetEasyBrowser.findElements(By.className("first_detail_cell"));
		List<WebElement> neighborhood = streetEasyBrowser.findElements(By.className("details_info"));

		//		Each Streeteasy page has 14 listings and 1 empty listing. 
		ArrayList<ApartmentObject> aptList = new ArrayList<>();
		for(int i = 0; i < 15; i++){
			aptList.add(new ApartmentObject());
		}
		//		For each listing, set the fields of apartments
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
		//		Remove the empty apartment listing
		aptList.remove(aptList.size()-1);

		for(ApartmentObject apt: aptList)
			streetEasyFileWriter.write(apt + "\n");
	}

	public static void craigslistDriver(){
		//Setup the metrics
		int minprice=1000;
		int maxprice=1250;

		for(int j = 0; j < 1; j++){
			maxprice += 500;
			minprice += 500;
			for(int i = 0; i < craigslistPageLimit; i++){
				try {
					// Get pages based on the input
					getCraigsPages(i,minprice,maxprice);
					// Make the thread wait for a second so that craigslist does not block the IP address
					Thread.sleep(1000);
				} catch (Exception e){
					// Continue execution even if there is a fault
					e.printStackTrace();
				}
			}
		}
	}

	public static void getCraigsPages(int page,int minprice, int maxprice) throws IOException{
		// Setup the URL to navigate to
		String navigationString = "https://newyork.craigslist.org/search/mnh/aap";
		if(page != 0)
			navigationString = navigationString.concat("?s="+(100*page)+"&min_price="+minprice+"&max_price="+maxprice);
		
		else
			navigationString = navigationString.concat("?min_price="+minprice+"&max_price="+maxprice);

		craigslistBrowser.navigate().to(navigationString);
		// Javascript part is optional. Pretend to scroll down to not get blocked 
		((JavascriptExecutor)craigslistBrowser).executeScript("scroll(0,400)");
		// Get all listings on the page
		List<WebElement> allListings = craigslistBrowser.findElements(By.className("row"));

		for(int i = 0; i < allListings.size(); i++){
			listing_count++;
			String currentListing = allListings.get(i).getText();
			// Remove all unnecessary characters from the current listing
			currentListing = CharMatcher.inRange((char)0, (char)128).retainFrom(currentListing);
			WebElement thisElement = allListings.get(i);
			boolean hasImage = false;
			int hasMap = 0, numImages = 0;

			try {
				List<WebElement> x = thisElement.findElements(By.className("maptag"));
				x.get(0);
				hasMap = 1; // Mark it as having a map
			} catch (Exception e) { // Does not contain map location
				nomap_count++;
			}
			
			try {
				List<WebElement> x = thisElement.findElements(By.className("swipe-wrap"));
				x.get(0).getText();
				hasImage = true;
			} catch (Exception e) {
				// Does not contain a map
				noimage_count++;
				numImages = 0;
			}
			// If contains an image, then find the number of images
			if(hasImage){
				WebElement newImages = thisElement.findElement(By.className("swipe-wrap"));
				try {
					if(newImages != null){
						List<WebElement> children = newImages.findElements(By.xpath(".//div"));
						numImages = children.size();
					}
				} catch (NoSuchElementException e) {
					// If can not find the paths, it means contains only 1 image.
					numImages = 1;
				}
			}	

			ApartmentObject apt = new ApartmentObject();
			apt.isCraigsList = true;
			apt.numImages = numImages;
			apt.description = thisElement.findElement(By.className("hdrlnk")).getText();
			apt.hasMap=hasMap;
			// Get index of dollar sign and find the rent based no that index
			int rentStart = currentListing.indexOf("$");
			while(currentListing.charAt(rentStart +1) >= '0' && currentListing.charAt(rentStart +1) <= '9'){
				apt.rent += currentListing.charAt(rentStart+1) - '0';
				apt.rent *= 10;
				rentStart++;
			}
			apt.rent /= 10; // Adjust number to be accurate
			String lowerCase = currentListing.toLowerCase();
			int bedroomIndex = lowerCase.indexOf("br");
			if(bedroomIndex == -1){
				bedroomIndex = lowerCase.indexOf("bedroom");
			}
			if(bedroomIndex == -1){ // No info as bedroom or br, so it is a studio
				apt.numBedrooms = 1;
			}
			// Else, try to find into based on that index.
			else if(bedroomIndex > 0 && lowerCase.charAt(bedroomIndex-1) >= '0' && lowerCase.charAt(bedroomIndex-1) <= '9'){
				apt.numBedrooms = lowerCase.charAt(bedroomIndex-1) - '0';
			}
			else if(bedroomIndex >= 2 && lowerCase.charAt(bedroomIndex-2) >= '0' && lowerCase.charAt(bedroomIndex-2) <= '9'){
				apt.numBedrooms = lowerCase.charAt(bedroomIndex -2) - '0';
			}
			else{
				apt.numBedrooms = 1;
			}
			// Most craigslist listings have their location between paranthesis
			int neighborhoodStartIndex = currentListing.lastIndexOf('(');
			int neighborhoodEndIndex = currentListing.lastIndexOf(')');
			String neighborhood = currentListing.substring(neighborhoodStartIndex+1, neighborhoodEndIndex);
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
		// Instantiate file writers to append mode
		renthopFileWriter = new FileWriter("Renthop/RenthopInput.txt", true);
		streetEasyFileWriter = new FileWriter("StreetEasy/StreetEasyInput.txt", true);
		craigslistFileWriter = new FileWriter("Craigslist/CraigslistInput.txt", true);

		// Create background threads so that all web pages are processed simultaneously
		Thread renthopThread = new Thread(new Runnable() {
			@Override
			public void run() {
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

		//	Start all threads
		renthopThread.start();
		craigslistThread.start();
		streetEasyThread.start();

		//	Wait until all threads finished
		while(renthopThread.getState() != Thread.State.TERMINATED && craigslistThread.getState() != Thread.State.TERMINATED && streetEasyThread.getState() != Thread.State.TERMINATED){

			// Sleeping the thread would be more energy efficient 
			Thread.sleep(1000);
		}

		// Print the statistics
		System.out.println("total listings "+listing_count);
		System.out.println("listings with no images "+noimage_count);
		System.out.println("listings with no map location "+nomap_count);
	}
}
