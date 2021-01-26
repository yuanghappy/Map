package graphs;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

class KevinBaconGame {
	HashMap<Integer, String> actorName = new HashMap<Integer, String>();
	HashMap<Integer, String> movieName = new HashMap<Integer, String>();
	Graph KBGgraph = new Graph();
	
	public KevinBaconGame() throws IOException{
		buildCodetoNameMaps();
		buildVertices();
		connectVertices();
		
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
		//reading the actors name file generating code to actor name map
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
								actorName.get(actorList.get(j)), movieName.get(movieCode));
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
	
	public static void main(String[] args) throws IOException{
		KevinBaconGame myGame = new KevinBaconGame();
		
	}
}
