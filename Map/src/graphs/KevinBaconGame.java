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
	JFrame frame;
	private final int width=800, height=800;
	//HashSet for non duplicate actor connections for avg. connection function
	//HashSet
	
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
			input1.setPreferredSize(new Dimension(200, 30));
			input1.setText("Actor 1");
			input1.setEditable(true);
			input1.setBackground(Color.white);
			input2 = new JTextArea();
			input2.setPreferredSize(new Dimension(200, 30));
			input2.setText("Actor 2");
			input2.setEditable(true);
			input2.setBackground(Color.white);
			InputPanel.setPreferredSize(new Dimension (width-50, height/4));
			InputPanel.setBorder(BorderFactory.createTitledBorder("Input Panel"));
			InputPanel.add(input1);
			InputPanel.add(input2);
			Panel.add(InputPanel);
		
			
		//BUTTONS
		//Search Button
		JButton SearchButton = new JButton("Search");
		SearchButton.setPreferredSize(new Dimension (100, 30));
		SearchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(input1.getText().trim().equals("") || input1.getText().trim().equals("Actor 1")){
					input1.setText("Please enter actor name");
				}
				if(input2.getText().trim().equals("") || input2.getText().trim().equals("Actor 1")){
					input2.setText("Please enter actor name");
				}
				if(!actorName.containsValue(capitalize(input1.getText().trim().toLowerCase()))){
					input1.setText("Actor not found");
				}
				if(!actorName.containsValue(capitalize(input2.getText().trim().toLowerCase()))){
					input2.setText("Actor not found");
				}
				
				//input1.setText("");
				//input2.setText("");
			}
		});
		
		//button layout
		JPanel ButtonPanel = new JPanel();
		ButtonPanel.add(SearchButton);
		ButtonPanel.setPreferredSize(new Dimension (width-50, height/4));
		ButtonPanel.setBorder(BorderFactory.createTitledBorder("Control Panel"));
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
					actorName.put(Integer.parseInt(code), name);					
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
					movieName.put(Integer.parseInt(code), name);
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
	
	private boolean connectVertices() throws IOException{
		String movieCode = "";
		int connection = 0;
		String lastMovieCode = "";
		String actorCode = "";
		//reader for movie-actor file
		BufferedReader in = new BufferedReader(new FileReader("resource/movie-actors.txt"));
		int control;
		//Array list storing names of all actors in one movie. 
		//Cleared once moved on to another movie.
		ArrayList<Integer> actorList = new ArrayList<Integer>();
		
		for(String line = in.readLine(); line != null; line = in.readLine()){
			if(line.charAt(0) == 65279){line = line.substring(1);}
			//control for reading code or name, turns to 1 once read ~
			control = 0;
			for(int i = 0; i < line.length(); i++){
				if(line.charAt(i) == '~'){ control = 1; continue;}
				if(control == 0){movieCode += line.charAt(i);}
				else{actorCode += line.charAt(i);}
			}
			//check if the loop has iterated to a new movie
			if(lastMovieCode.length()==0 || lastMovieCode.equals(movieCode)){
				lastMovieCode = movieCode;
				actorList.add(Integer.parseInt(actorCode));
				movieCode = "";
				actorCode = "";	
			}else{
				lastMovieCode = movieCode;
				//connect all actors in in the same movie
				for(int i = 0; i < actorList.size(); i++){
					for( int j = i+1; j < actorList.size(); j++){
						KBGgraph.connect(actorName.get(actorList.get(i)),
								actorName.get(actorList.get(j)), movieName.get(Integer.parseInt(movieCode)));
						//**
						connection++;
					}
				}
				actorList.clear();
				actorList.add(Integer.parseInt(actorCode));
				movieCode = "";
				actorCode = "";	
			}		
		}
		in.close();
		System.out.println("connections established: " + connection);
		return true;
	}
	
	//this calculates the average number of actors each actor is connected to
	private int avgConnectivity(){
		return 0;
	}
	public static void main(String[] args) throws IOException{
		KevinBaconGame myGame = new KevinBaconGame();
		myGame.KBGgraph.search("Sam Worthington", "Jon Curry");
		System.out.println(myGame.KBGgraph.AverageConnectivity());
	}
}
