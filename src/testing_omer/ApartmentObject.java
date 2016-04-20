package testing_omer;


public class ApartmentObject {
	public String price;
	public String title;
	public String address;
	
	
	public ApartmentObject(String title, String price, String address){
		this.price = price;
		this.title = title;
		this.address = address;
	}
	
	public String toString(){
		return "Title: " + this.title + "\tPrice: " + this.price + "\tAddress:" + this.address;
	}
}
