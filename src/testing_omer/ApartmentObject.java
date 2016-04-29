package testing_omer;

import java.util.concurrent.atomic.AtomicInteger;

public class ApartmentObject {
	
	private static AtomicInteger nextId = new AtomicInteger();
    private int id;
    
    public double rent;
	public String neighborhood;
	public int numBedrooms;
	
	// These are for Craigslist listings.
	public boolean isCraigsList;
	public String description;
	public boolean hasImage;
	
	private static String delimiter = "\t";
	
	public ApartmentObject(){
		this.id = nextId.incrementAndGet();
		this.neighborhood = "";
		this.rent = 0.0;
		this.numBedrooms = 0;
		this.isCraigsList = false;
	}
	
	public ApartmentObject(String neighborhood, int numBedrooms, double rent){
		this.id = nextId.incrementAndGet();
		this.neighborhood = neighborhood;
		this.rent = rent;
		this.numBedrooms = numBedrooms;
		
	}
	
	public String toString(){
		if(!isCraigsList)
			return this.id + delimiter + this.neighborhood + delimiter + this.rent + delimiter + this.numBedrooms;
		else
			return this.id + delimiter + this.neighborhood + delimiter + this.rent + delimiter + this.numBedrooms + delimiter + this.hasImage + delimiter + this.description;
	}
}
