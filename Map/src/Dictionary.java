import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Dictionary {

	public static void main(String[] args) throws IOException{
	
	//constructing the dictionary map from imported text file
	Map<String, String> dictionary = new HashMap<String, String>();
		
	BufferedReader in = new BufferedReader(new FileReader("resource/EnglishToArabicDictionary.txt"));
	
	for(String line = in.readLine(); line != null; line = in.readLine()){
		if(line.charAt(0) == 65279){line = line.substring(1);}
		dictionary.put(line.toLowerCase().trim(), in.readLine().toLowerCase().trim());
	}
	
	in.close();
	
	//UI
	Scanner scanner = new Scanner(System.in);
	String userinput;
	Boolean Run = true;
	System.out.println("Welcome to English to Arabic Translator. \nPress Q to exit the program.\n");
	
	while (Run){
		System.out.println("Please enter word in english:");
		userinput = scanner.nextLine().trim().toLowerCase();
		System.out.println("UI: " + userinput);
		if(userinput.equals("q")){
			System.out.println("Quite");
			Run = false;
			break;
		}
		if(dictionary.containsKey(userinput)){
			System.out.println("Translation: " + dictionary.get(userinput));
		}else{
			System.out.println("Sorry, we don't have the word you are looking for.\n");
		}
	}

	}
}
