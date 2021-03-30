import org.jsoup.Jsoup;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WeatherScraper{
    public static void main(String[] args) {
    	
    	String town = JOptionPane.showInputDialog("Where do you want to check temperature for?");
    	
    		try {
    			//connects to the given url
				Document doc = Jsoup.connect("https://www.google.com/search?q=" + town + "+weather").get();
				
				//gets temperature
				Element temp = doc.getElementById("wob_tm");
				
				//gets location
				Element loc = doc.getElementById("wob_loc");
				
				System.out.println("Temperature is " + temp.text() + " degrees fahrenheit in " + loc.text());
				
    		
    		} catch (Exception e) {
				System.out.println("Error in connection or search");
				System.exit(0);
		}
    }
}