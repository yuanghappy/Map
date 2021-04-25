import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WikiGame {
	
	HashMap shortUrlToDisplayedName = new HashMap<String, String>();
	private final int height=800, width=1000;
	private JTextArea input1, input2, display;
	private JSpinner iterationSelector;
	
	public WikiGame(){
		//UI
		JPanel panel = new JPanel();
		BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
		panel.setBorder(BorderFactory.createTitledBorder("Wiki Game"));
		
		input1 = new JTextArea();input1.setEditable(true);input1.setPreferredSize(new Dimension(width-100, height/12));
		input2 = new JTextArea();input2.setEditable(true);input2.setPreferredSize(new Dimension(width-100, height/12));
		display = new JTextArea();display.setEditable(false);display.setLineWrap(true);display.setWrapStyleWord(true);
		SpinnerModel model = new SpinnerNumberModel(320, 10, 1000, 10);     
		iterationSelector = new JSpinner(model);

		JScrollPane scroll = new JScrollPane(display, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setPreferredSize(new Dimension(width-100, height/2));
		
		JButton searchButton = new JButton("Search");
		searchButton.setPreferredSize(new Dimension (width-500, 30));
		searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ArrayList<String> displayArray = new ArrayList<String>();	
				String s1, s2, displayS = "";
				s1 = input1.getText().trim();
				s2 = input2.getText().trim();
				//check to see if input exists
				if(s1.equals("") || s2.equals("")){
					display.setText("Please enter keywords.");
					return;
				}
				//change formatting
				s1 = addUnderScore(s1).toLowerCase();
				s2 = addUnderScore(s2).toLowerCase();
				//check if starting keywords has a valid wiki entry
				try {
					Jsoup.connect("https://en.wikipedia.org/wiki/" + s1).get();
				} catch (IOException e1) {
					display.setText("No wiki entry exists for starting keyword.\nTry another one.");
					return;
				}
				displayArray = search(s1, s2);
				//return will be null once the while loop in search exceeds 350 iterations
				//this prevents the program from finding connections that do not exist 
				if(displayArray==null){
					display.setText("Your starting point and target are too far apart.");
					return;
				}
				//generate return display format
				displayS += "Starting point: " + s1 + "\nUltimate Target: " + s2 + "\nIteration: " + (int)iterationSelector.getValue() + "\n\n";
				for(int i = 0; i < displayArray.size(); i++){
					displayS += Integer.toString(i+1) + ". " + displayArray.get(i) + "\n";
				}
				display.setText(displayS);
				input1.setText("");
				input2.setText("");
			}

			private String addUnderScore(String s) {
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
		});
		
		JLabel label1 = new JLabel("Starting Point");
		JLabel label2 = new JLabel("Ultimate Target");
		JLabel label3 = new JLabel("Max Search Iterations");
		panel.add(label1);
		panel.add(input1);
		panel.add(label2);
		panel.add(input2);
		panel.add(label3);
		panel.add(iterationSelector);
		panel.add(searchButton);
		panel.add(scroll);
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(width, height);
		frame.setFocusable(true);
		frame.setResizable(false);
		frame.add(panel);
		frame.setVisible(true);
		
		display.setText("Welcome to the Wiki Game \nEverything connected.");
	}

	public ArrayList<String> search(String start, String target) {
		
		String startUrl = "/wiki/" + start.trim().toLowerCase();
		
		ArrayList<String> toVisit = new ArrayList<String>();
		toVisit.add(startUrl);
		HashSet<String> visited = new HashSet<String>();
		visited.add(startUrl);
		
		HashMap<String, String> leadsTo = new HashMap<String, String>();
		
		int i = 0; 
		
		while (!toVisit.isEmpty() && i< (int)iterationSelector.getValue()){
			
			Vertex curr = new Vertex(toVisit.remove(0));
						
			for (String s : curr.neighborSet) {
								
				if (visited.contains(s)) continue;
				
				leadsTo.put(s, curr.shortUrl);
				
				if (this.compareKeyWords(s, target)) {		
					return backtrace(s, leadsTo);
				}else {
					toVisit.add(s);
					visited.add(s);
				}
			}
			i++;
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
			return path;	
	}
	
	
	
	public boolean compareKeyWords (String s, String a){
		
		HashSet<String>keywordSet1 = new HashSet<String>();
		HashSet<String>keywordSet2 = new HashSet<String>();
		
		String r = "";
		for (int i = 6; i < s.length(); i++){
			
			if(s.charAt(i) != '_'){
				r += s.charAt(i);
			}else{
				keywordSet1.add(r.toLowerCase());
				r = "";
			}
		}
		//put leftover string into the set
		if(!r.equals("")){
			keywordSet1.add(r.toLowerCase());
		}
		
		r = "";
		for (int i = 0; i < a.length(); i++){
					
					if(a.charAt(i) != '_'){
						r += a.charAt(i);
					}else{
						keywordSet2.add(r.toLowerCase());
						r = "";
					}
				}
		//put leftover string into the set
		if(!r.equals("")){
			keywordSet2.add(r.toLowerCase());
		}
		
		for(String keyword : keywordSet1){
			if(keywordSet2.contains(keyword)){
				return true;
			}
		}
		return false;
	}
	
	
	public static void main(String[] args){
		WikiGame g = new WikiGame();
	}
	
	
}

class Vertex{
	String shortUrl;
	HashSet<String> neighborSet;
	
	public Vertex(String url){
		this.shortUrl = url;
		neighborSet = new HashSet<String>();
		//parse all links and add in as neighbors
		Document doc;
		
			try {
				doc = Jsoup.connect("https://en.wikipedia.org" + this.shortUrl).get();

			
			Elements links = doc.select(".mw-content-ltr p a[href]");
			//if page is directory page
			if(links.isEmpty()){
				links = doc.select(".mw-content-ltr ul li a[href]");
			}
			
		    Element firstLink = links.first();
		    Element lastLink = links.last();
		    Element p;
		    p = firstLink;
		    for (int i = 0; p != lastLink; i++) {
		        p = links.get(i);
		        if(p.text().isEmpty()){continue;}
		        if(p.text().charAt(0) != '[' && p.attr("href").charAt(0) == '/'){
		        	this.neighborSet.add(p.attr("href"));
		        }
		    }
			} catch(org.jsoup.HttpStatusException e1){
				
			} catch(java.net.UnknownHostException e1){
				
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}