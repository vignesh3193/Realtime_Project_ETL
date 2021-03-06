package aptReduceCrawler;

import java.util.concurrent.atomic.AtomicInteger;

public class ApartmentObject {
	// Make it atomic so that multiple threads do not overlap.
	private static AtomicInteger nextId = new AtomicInteger();
    private int id;
    
    public double rent;
	public String neighborhood;
	public int numBedrooms;
	
	// These are for Craigslist listings.
	public boolean isCraigsList;
	public String description;
	public int numImages;
	public int hasMap;
	
	private static String delimiter = "\t";
	
	public ApartmentObject(){
		this.id = nextId.incrementAndGet();
		this.neighborhood = "";
		this.rent = 0.0;
		this.numBedrooms = 0;
		this.isCraigsList = false;
		this.numImages = 0;
		this.hasMap=0;
	}
	
	public ApartmentObject(String neighborhood, int numBedrooms, double rent){
		this.id = nextId.incrementAndGet();
		this.neighborhood = neighborhood;
		this.rent = rent;
		this.numBedrooms = numBedrooms;
	}
	
	// To print, check if extra fields are needed for craigslist.
	public String toString(){
		if(!isCraigsList)
			return this.id + delimiter + this.neighborhood + delimiter + this.rent + delimiter + this.numBedrooms;
		else
			return this.id + delimiter + this.neighborhood + delimiter + this.rent + delimiter + this.numBedrooms + delimiter + this.numImages + delimiter + this.hasMap + delimiter + this.description;
	}
}
