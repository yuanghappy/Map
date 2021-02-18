package graphs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;


class KevinBaconGame {
	HashMap<Integer, String> actorName = new HashMap<Integer, String>();
	HashMap<Integer, String> movieName = new HashMap<Integer, String>();
	Graph KBGgraph = new Graph();
	JTextArea input1, input2, OutputDisplay;
	JButton SearchButton;
	JFrame frame;
	private final int width=800, height=800;
	//Map of String and HashSet for non duplicate actor connections for avg. connection function
	HashMap<String, HashSet<String>> NonDupMap;
	//Map of string and HashSet of strings for movie connectivity
	//key of map is movie name, value is a hashset that contains all actors in that movie
	HashMap<String, HashSet<String>> MovieConnMap;
	
	public KevinBaconGame() throws IOException{
		buildCodetoNameMaps();
		buildVertices();
		connectVertices();
	//UI
		//MainPanel
		JPanel Panel = new JPanel();
		BoxLayout layout = new BoxLayout(Panel, BoxLayout.Y_AXIS);
		//Input Sub-panel
		JPanel InputPanel = new JPanel();
			//Text input area
			input1 = new JTextArea();
			input1.setPreferredSize(new Dimension(3*width/8, height/16));
			input1.setText("Actor/Movie 1");
			input1.setEditable(true);
			input1.setBackground(Color.white);
			input2 = new JTextArea();
			input2.setPreferredSize(new Dimension(3*width/8, height/16));
			input2.setText("Actor/Movie 2");
			input2.setEditable(true);
			input2.setBackground(Color.white);
			InputPanel.setPreferredSize(new Dimension (width-50, height/4));
			InputPanel.setBorder(BorderFactory.createTitledBorder("Input Panel"));
			InputPanel.add(input1);
			InputPanel.add(input2);
			Panel.add(InputPanel);
		
			
		//BUTTONS
		//Search Button
		SearchButton = new JButton("Search");
		SearchButton.setPreferredSize(new Dimension (100, 30));
		SearchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				//disable all buttons to prevent interruption
				disableButtons(true);
				search(input1.getText().trim().toLowerCase(),input2.getText().trim().toLowerCase());
			}
		});
		
		//button layout
		JPanel ButtonPanel = new JPanel();
		ButtonPanel.add(SearchButton);
		ButtonPanel.setPreferredSize(new Dimension (width-50, height/4));
		ButtonPanel.setBorder(BorderFactory.createTitledBorder("Functions"));
		Panel.add(ButtonPanel);
		
		//Output display
		OutputDisplay = new JTextArea();
		OutputDisplay.setPreferredSize(new Dimension(width-50, height/4));
		OutputDisplay.setText("Welcome to KBG!");
		OutputDisplay.setEditable(false);
		OutputDisplay.setBackground(Color.white);
		Panel.add(OutputDisplay);
		
		//JFrame
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(width, height);
		frame.setFocusable(true);
		frame.setResizable(false);
		frame.add(Panel);
		frame.setVisible(true);
	}
	
	
	//return string with first character of each word capitalized. Input string is all lower case.
	private String capitalize(String s){
		char[] charArray = s.toCharArray();
	    boolean foundSpace = true;

	    for(int i = 0; i < charArray.length; i++) {

	      if(Character.isLetter(charArray[i])) {  
		      if(foundSpace) {
		        charArray[i] = Character.toUpperCase(charArray[i]);
		        foundSpace = false;
		      }   
	      }else{	  
	    	    foundSpace = true;   
	      }
	    }
	    
	    s = String.valueOf(charArray);
	    return s;
	}
	
	private boolean buildCodetoNameMaps() throws IOException{
		//reading the actors name file generating code to actor name map
				BufferedReader in = new BufferedReader(new FileReader("resource/actors.txt"));
				String code = "";
				String name = "";
				int control;
				for(String line = in.readLine(); line != null; line = in.readLine()){
					if(line.charAt(0) == 65279){line = line.substring(1);}
					//control for reading code or name, turns to 1 once read ~
					control = 0;
					for(int i = 0; i < line.length(); i++){
						if(line.charAt(i) == '~'){ control = 1; continue;}
						if(control == 0){code += line.charAt(i);}
						else{name += line.charAt(i);}
					}
					actorName.put(Integer.parseInt(code), name.trim().toLowerCase());					
					code = "";
					name = "";			
				}
				in.close();
				System.out.println("actorName size: " + actorName.size());
				
		//reading the movie name file generating code to movie name map
				BufferedReader in1 = new BufferedReader(new FileReader("resource/movies.txt"));
				for(String line = in1.readLine(); line != null; line = in1.readLine()){
					if(line.charAt(0) == 65279){line = line.substring(1);}
					//control for reading code or name, turns to 1 once read ~
					control = 0;
					for(int i = 0; i < line.length(); i++){
						if(line.charAt(i) == '~'){ control = 1; continue;}
						if(control == 0){code += line.charAt(i);}
						else{name += line.charAt(i);}
					}
					movieName.put(Integer.parseInt(code), name.trim().toLowerCase());
					code = "";
					name = "";			
				}
				in.close();
				System.out.println("movieName size: " + movieName.size());
				return true;
	}
	
	private boolean buildVertices(){
		//creating a set of vertices with each one holding name of one actor
		for (String name : actorName.values()){
			KBGgraph.addVertex(name);
		}
		return true;
	}
	
	//connects all vertices with edges
	//make non duplicated HashMap for actor avg. connectivity function
	//make MovieConnMap HashMap for movie connectivity function
	private boolean connectVertices() throws IOException{
		String movieCode = "";
		int connection = 0;
		String lastMovieCode = "";
		String actorCode = "";
		int control;

		//reader for movie-actor file
		BufferedReader in = new BufferedReader(new FileReader("resource/movie-actors.txt"));
		
		//Array list storing names of all actors in one movie. 
		//Cleared once moved on to another movie.
		ArrayList<Integer> actorList = new ArrayList<Integer>();
		
		//HashMap for movie connectivity
		MovieConnMap = new HashMap<String, HashSet<String>>();

		//HashMap for NonDuplicated connections for avg connectivity function
		NonDupMap = new HashMap<String, HashSet<String>>();
		
		//Buffered reader goes through movie-actor file
		//go through each line of the text file
		for(String line = in.readLine(); line != null; line = in.readLine()){
			if(line.charAt(0) == 65279){line = line.substring(1);}
			//control for reading movie code or actor code, turns to 1 once read '~'
			control = 0;
			for(int i = 0; i < line.length(); i++){
				if(line.charAt(i) == '~'){ control = 1; continue;}
				if(control == 0){movieCode += line.charAt(i);}
				else{actorCode += line.charAt(i);}
			}
			//check if the loop has iterated to a new movie
			//same movie: continue to add actors to the actor list
			if(lastMovieCode.length()==0 || lastMovieCode.equals(movieCode)){
				lastMovieCode = movieCode;
				actorList.add(Integer.parseInt(actorCode));
				movieCode = "";
				actorCode = "";	
			}else{
				//new movie: build connections for all actors
				//in the previous movie. Clear the previous actor list. Start new
				//actor list for the new movie and add in actors
				
				//TEST
				//build map for movie connectivity function
				String mN = movieName.get(Integer.parseInt(lastMovieCode));
				if(!MovieConnMap.containsKey(mN)){
					MovieConnMap.put(mN, new HashSet<String>());
				}
				
				//connect all actors in in the same movie
				//two for loops
				for(int i = 0; i < actorList.size(); i++){
					//TEST
					MovieConnMap.get(mN).add(actorName.get(actorList.get(i)));
					
					//Second for loop
					for( int j = i+1; j < actorList.size(); j++){
						String n1, n2;
					//connect vertices with edges
						n1 = actorName.get(actorList.get(i));
						n2 = actorName.get(actorList.get(j));
						KBGgraph.connect(n1, n2, mN);
						connection++;
						
					//build the non duplicated actor connection.
						//hashcode is used to guarantee that the same actor is used as key
						//no matter the direction of connection.
						String ntemp;
						if(n1.hashCode() > n2.hashCode()){
							ntemp = n1;
							n1 = n2;
							n2 = ntemp;
						}
						//check if the same connection already exists
						if(NonDupMap.containsKey(n1)){
							if(!NonDupMap.get(n1).contains(n2)){
								NonDupMap.get(n1).add(n2);
							}
						}else{
							NonDupMap.put(n1, new HashSet<String>());
							NonDupMap.get(n1).add(n2);
						}
					}
				}
				actorList.clear();
				actorList.add(Integer.parseInt(actorCode));
				lastMovieCode = movieCode;
				movieCode = "";
				actorCode = "";	
			}		
		}
		in.close();
		System.out.println("connections established: " + connection);
		return true;
	}
    
	//disable or enable all function buttons
	//prevent interruption function execution
	private void disableButtons(boolean b){
		if(b){
			SearchButton.setEnabled(false);
			
		}else{
			SearchButton.setEnabled(true);
			
		}
	}
	
	//FUNCTION #1
	//this calls BFS search in the graph class
	private void search(String n1, String n2){
		
		ArrayList<String>traceList;
		
		//check for input validity. More efficient by confirming that inputs are valid
		//instead of checking if they are invalid
		if(actorName.containsValue(n1) && actorName.containsValue(n2)){
			traceList = KBGgraph.search(n1, n2);
			input1.setText("Actor/Movie 1");
			input2.setText("Actor/Movie 2");		
		}
		//Invalid input responses
		else{
			if(!actorName.containsValue(n1)){
				input1.setText("Actor not found");
			}
			if(!actorName.containsValue(n2)){
				input2.setText("Actor not found");
			}
			if(n1.equals("") || n1.equals("actor/movie 1")){
				input1.setText("Please enter actor name");
			}
			if(n2.equals("") || n2.equals("actor/movie 2")){
				input2.setText("Please enter actor name");
			}
			disableButtons(false);
			return;
		}
		
		//display of search result
		OutputDisplay.setText("");
		if(traceList == null){
			OutputDisplay.setText("No connection exist between "+ capitalize(n1) + 
					" and " + capitalize(n2) + ".");
		}else{
			int curr = 0, connLevel = 0;
			String output = "";
			for(int i = 0; i<(traceList.size()-1)/2; i++){
				connLevel++;
				output += ("\n " + (connLevel) + ". '" + capitalize(traceList.get(curr)) + 
						"' is connected to '" + 
						capitalize(traceList.get(curr+2)) + "' in <" + 
						capitalize(traceList.get(curr+1)) + ">\n");
				curr += 2;
			}
			output += ("\n '" + capitalize(traceList.get(0)) + "' and '" + 
					capitalize(traceList.get(traceList.size()-1)) + "' are " + connLevel +
					" connections away.\n");
			OutputDisplay.setText(output);
		}
		
		
		//enable all buttons since method is finished
		disableButtons(false);
		return;
	}
	
	//FUNCTION #2
	//this calculates the average number of actors each actor is connected to
	private int avgConnectivity(){
		int totalConnection = 0;
		for(HashSet hset : NonDupMap.values()){
			totalConnection += hset.size();
		}
		return (int)totalConnection/actorName.size();	
	}
	
	//FUNCTION #3
	//this returns the overlapping actors between two movies and a movie connectivity score
	private ArrayList<String> movieConnectivity(String n1, String n2){
		n1 = n1.trim().toLowerCase();
		n2 = n2.trim().toLowerCase();
		ArrayList<String> commonActors = new ArrayList<String>();
		//check if two input movies exist
		if(!MovieConnMap.containsKey(n1) || !MovieConnMap.containsKey(n2)){
			commonActors.add("Movie input does not exist.");
			return commonActors; 
		}
		
		//check for common actors
		for(String actor1 : MovieConnMap.get(n1)){
			if(MovieConnMap.get(n2).contains(actor1)){
				commonActors.add(capitalize(actor1));
			}
		}
		if(commonActors.size()==0){commonActors.add("There is no common actor");}
		return commonActors;
	}
	
	//FUNCTION #4
	//this returns the average number of common actors of all movies
	private double avgMovieConnectivity(){
		ArrayList<String> movieNameList = new ArrayList<String>();
		float totalConnectivity = 0;
		float moviePairs = 0;
		
		//convert name of all movies from map to array list of easier operation
		for (String n : movieName.values()){
			movieNameList.add(n);
		}
		
		for(int i = 0; i < movieNameList.size(); i++){
			for(int j = i+1; j < movieNameList.size(); j++){
				//only call movieConnnectivity method once for runtime efficiency
				ArrayList<String> commonActorList = movieConnectivity(movieNameList.get(i), movieNameList.get(j));
				if(commonActorList.get(0).equals("There is no common actor")){continue;}
				totalConnectivity += commonActorList.size();
				moviePairs ++;
			}	
		}
		System.out.println(totalConnectivity + " " + moviePairs);

		return Math.round((totalConnectivity/moviePairs)*1000)/1000.0;
	}
	public static void main(String[] args) throws IOException{
		KevinBaconGame myGame = new KevinBaconGame();
		myGame.KBGgraph.search("kelly kilgour", "Lucy Briant");
		//myGame.search("Sam Worthington", "Jon Curry");
		//System.out.println("\n" + "Avg connectivity is: " + myGame.avgConnectivity());
		//System.out.println("\nCommon actors between The Dark Knight Rises and Batman Begins: " + 
		//"\n" + myGame.movieConnectivity(" the dark Knight rises", "batman begins"));
		//System.out.println("\n" + "Avg movie connectivity is: " + myGame.avgMovieConnectivity());
	}
}
