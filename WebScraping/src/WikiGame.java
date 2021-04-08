import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WikiGame {

//make sure target is lower case
	public ArrayList<String> search(String start, String target) {
		
		//add error proof
		String startUrl = "https://en.wikipedia.org/wiki/" + start.trim().toLowerCase();
		
		ArrayList<String> toVisit = new ArrayList<String>();
		toVisit.add(startUrl);
		HashSet<String> visited = new HashSet<String>();
		visited.add(startUrl);
		
		HashMap<String, String> leadsTo = new HashMap<String, String>();
		
		while (!toVisit.isEmpty()){
			
			Vertex curr = new Vertex(toVisit.remove(0));
			
			
			for (String s : curr.neighborHashMap.keySet()) {
								
				if (visited.contains(s)) continue;
				
				leadsTo.put(s, curr.url);
				
				if (this.compareUrlContent(s, target)) {		
					return backtrace(s, leadsTo);
				}else {
					toVisit.add("https://en.wikipedia.org" + s);
					visited.add("https://en.wikipedia.org" + s);
				}
			}
		}
		return null;
	}
	
	public ArrayList<String> backtrace(String target, HashMap<String, String> leadsTo) {
			
			String curr = target;
			ArrayList<String> path = new ArrayList<String>();
			path.add(0, curr);
	
			while (leadsTo.get(curr) != null) {
				path.add(0, leadsTo.get(curr));
				curr = leadsTo.get(curr);
			}
			
			System.out.print(path);
			return path;	
	}
	
	public String addUnderScore (String s){
		String r = "";
		for(int i = 0; i < s.length(); i ++){
			if(s.charAt(i)==' '){
				r += "_";
			}else{
				r += s.charAt(i);
			}
		}
		return r;
	}
	
	public boolean compareUrlContent (String s, String a){
		HashSet<String>returnSet = new HashSet<String>();
		String r = "";
		for (int i = 6; i < s.length(); i++){
			if(s.charAt(i) != '_'){
				r += s.charAt(i);
			}else{
				returnSet.add(r.toLowerCase());
				r = "";
			}
		}
		if(!r.equals("")){
			returnSet.add(r.toLowerCase());
		}
		return (returnSet.contains(a.trim().toLowerCase()));
	}
	
	public static void main(String[] args){
		WikiGame g = new WikiGame();
		g.search("fish", "car");
	}
	
	
}

class Vertex{
	String url;
	HashMap<String,String> neighborHashMap;
	
	public Vertex(String url){
		this.url = url;
		neighborHashMap = new HashMap<String, String>();
		//parse all links and add in as neighbors
		Document doc;
		try {
			doc = Jsoup.connect(url).get();
			
			Elements links = doc.select(".mw-content-ltr p a[href]");

		    Element firstLink = links.first();
		    Element lastLink = links.last();
		    Element p;
		    p = firstLink;
		    for (int i = 0; p != lastLink; i++) {
		        p = links.get(i);
		        if(p.text().charAt(0) != '['){
		        	System.out.println(p.attr("href"));
		        	this.neighborHashMap.put(p.attr("href"), p.text());
		        }
		    }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}