package aptReduceCrawler;

import java.io.*;
import java.util.*;


public class ETL {


	public static FileWriter renthopFileWriter;
	public static FileWriter craigslistFileWriter;
	public static FileWriter streetEasyFileWriter;
	
	public static void renthop()throws IOException
	{
		
		String text;
		try(BufferedReader br = new BufferedReader(new FileReader("Renthop/RenthopInput.txt"))) {
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		     text = sb.toString();
		}
		int aptType = 0;
		
		String[] listings = text.split("Check Availability");
		//System.out.println(listings[0]);
		for(String s:listings){
			
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
		
	}
	
	public static void streeteasy()throws IOException
	{
		File streeteasy=new File("StreetEasy/StreetEasyinput.txt");
		Scanner sc=new Scanner(streeteasy);
		
		ArrayList<ApartmentObject> aptList = new ArrayList<>();
		for(int i = 0; i < 15; i++){
			aptList.add(new ApartmentObject());
		}
		/*
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
			streetEasyFileWriter.write(apt + "\n");*/

	}
	
	public static void main(String args[])throws IOException
	{
		renthopFileWriter=new FileWriter("Final/input.txt");
		craigslistFileWriter=new FileWriter("Final/input.txt",true);
		streetEasyFileWriter=new FileWriter("Final/input.txt",true);
		renthop();
		
	}
}
