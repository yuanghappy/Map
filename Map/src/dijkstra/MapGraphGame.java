package dijkstra;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import dijkstra.Graph.Edge;
import dijkstra.Graph.Vertex;
import dijkstra.MapGraphGame.Circle;

class MapGraphGame {
	int Mode;
	Graph<Circle> g;
	int width = 825;
	int height = 800;
	//*EDIT* Background image file path
	String BackgroundPath = "resource/BackgroundImg.jpg";
	//*EDIT* map file saving path
	String MapPath = "resource/Saved_Map";

	MapGraphGame(){
		g = new Graph<Circle>();
		Mode = 0;
		//Game Panel
		Gamepanel gp = new Gamepanel();
		gp.setPreferredSize(new Dimension (width-50, 7*height/8));
		//MainPanel
		JPanel MainPanel = new JPanel();
		BoxLayout layout = new BoxLayout(MainPanel, BoxLayout.Y_AXIS);
		//Control Panel
			//Mode Selector
			String[]s1 = {"Add Vertex", "Remove Vertex", "Manage Connection", "Navigate"};
			JComboBox ModeSelector = new JComboBox(s1);
			ModeSelector.setSelectedIndex(0);
			ModeSelector.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){
					Mode = ModeSelector.getSelectedIndex();
					gp.allWhite();
					gp.cStored = null;
				}
			});
			//Confirm Setup Button
			JButton ConfirmButton = new JButton("Save Map");
			ConfirmButton.setPreferredSize(new Dimension (150, 30));
			ConfirmButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){
					ConfirmSetup();
				}
			});
			//Import Setup Button
			JButton ImportButton = new JButton("Import Saved Map");
			ImportButton.setPreferredSize(new Dimension (150, 30));
			ImportButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){
					ImportMap(MapPath);
					gp.repaint();
				}
			});
			//Instruction Button
			//Import Setup Button
			JButton InstructionButton = new JButton("?");
			InstructionButton.setPreferredSize(new Dimension (30, 30));
			InstructionButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){
					String info = "Welcome to the most robust Interstellar Navigator! \n\n"
							    + "Use the mode selector combobox to choose \n"
							    + "the function of this application. \n\n"
							    + "1.You can add and remove vertices and connections. \n"
							    + "2.You can use navigate mode to find the shortest \n"
							    + "path between any two connected vertices. \n"
							    + "3.You can store the vertices and connections \n"
							    + "you made to conveniently import them next time \n"
							    + "you use the application!";
			        JOptionPane.showMessageDialog(null, info, "Instructions", JOptionPane.INFORMATION_MESSAGE);

				}
			});
		JPanel ControlPanel = new JPanel();
		ControlPanel.add(ModeSelector);
		ControlPanel.add(ConfirmButton);
		ControlPanel.add(ImportButton);
		ControlPanel.add(InstructionButton);
		ControlPanel.setPreferredSize(new Dimension (width-50, height/10));
		ControlPanel.setBorder(BorderFactory.createTitledBorder("GameControl"));
		MainPanel.add(ControlPanel);
		MainPanel.add(gp);
		//JFrame
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(width, height);
		frame.setFocusable(true);
		frame.setResizable(false);
		frame.add(MainPanel);
		frame.setVisible(true);
		
	}
	
	private void ConfirmSetup(){
		//write text file
		try {
   		 BufferedWriter writer = new BufferedWriter(new FileWriter(MapPath));
   		    for(Circle c : g.vertices.keySet()){
   		    	int x;
   		    	writer.write(c.name + "\n" + c.getX() + "\n" + c.getY());
   		    	writer.newLine();
   		    }
   		    writer.write("@~~Connections_Below");
   		    for(Edge e : g.connections){
   	   		    writer.newLine();
   		    	writer.write(e.v1.x + "\n" + e.v1.y + "\n" + e.v2.x + "\n" + e.v2.y);
   		    }
   		    writer.close();
   	      System.out.println("Map Saved");
   	    } catch (IOException a) {
   	      System.out.println("An error occurred in saving process");
   	      a.printStackTrace();
   	      System.exit(0);
   	    }
	}
	
	//building graph from user's saved info
	
	private void ImportMap(String path){
		System.out.print("Import");
		//read file
		try {
			BufferedReader reader = new BufferedReader(new FileReader(path));
			String s;
			//Since we are importing a new map, wiping anything the user might have created
			//prior to importing
			g.vertices.clear();
			g.connections.clear();
			//Read file
			//Hash map of coordinates and circles for connections
			HashMap<Point, Circle> PointCircleMap = new HashMap<Point, Circle>();
			//boolean determining if reader has progressed to connection section of the text file
			Boolean isConnection = false;
			//control variable
			int LineOrder = 1;
			String name = null;
			int x = 0; int x1 = 0; int x2 = 0;
			int y = 0; int y1 = 0; int y2 = 0;
			
			while((s = reader.readLine()) != null){
				//check if this line is the separator between vertex and connection storage
				if(s.equals("@~~Connections_Below")){isConnection = true; continue;}
				//In the vertex storage section
				if(!isConnection){
					switch(LineOrder){
						case 1:
							name = s;
							LineOrder++;
							break;
						case 2:
							x = Integer.parseInt(s);
							LineOrder++;
							break;
						case 3:
							y = Integer.parseInt(s);
							Circle c = new Circle(name, x, y);
							g.addVertex(c, x, y);
							PointCircleMap.put(new Point(x,y), c);
							LineOrder = 1;
							break;
					}
				}else{
					switch(LineOrder){
					case 1:
						x1 = Integer.parseInt(s);
						LineOrder++;
						break;
					case 2:
						y1 = Integer.parseInt(s);
						LineOrder++;
						break;
					case 3:
						x2 = Integer.parseInt(s);
						LineOrder++;
						break;
					case 4:
						y2 = Integer.parseInt(s);
						g.connect(PointCircleMap.get(new Point(x1,y1)), PointCircleMap.get(new Point(x2,y2)));
						LineOrder = 1;
						break;
					}
				}
			}
			reader.close();
		} catch (IOException e) {
			System.out.print("Map file not found. Please edit its path at top of this program or save new map.");
			System.exit(0);
		}
	}
	

	class Gamepanel extends JPanel implements MouseListener{
		//image for background
		BufferedImage img;
		//Array List storing all the user set node (circles)
		ArrayList<Circle> circleArrayList = new ArrayList<Circle>();
		//Array list for storing connections
		ArrayList<Circle> connectionArrayList = new ArrayList<Circle>();
		//Circle for storing the first user input for connection function
		Circle cStored;
		//Array list for path
		ArrayList<Circle> pathList;
		
		public Gamepanel(){
			//background image
			try{
				img = ImageIO.read(new File(BackgroundPath));
			}catch (IOException e){
				System.out.print("background image file not found. Please edit its path at top of this program.");
				System.exit(0);
			}
			this.addMouseListener(this);
			cStored = null;
		}
		
		@Override
	  public void paintComponent(Graphics g) {
	      super.paintComponent(g);
			g.drawImage(img,0,0,null);
	      drawimg(g);
	      Toolkit.getDefaultToolkit().sync();
	  }
	  
		//method for drawing out the circles and connections on the panel
	  private void drawimg(Graphics gr) {
		  if(g.vertices.size()>0){
			  for(Vertex v : g.vertices.values()){
					Circle c = (Circle) v.info;
					c.draw(gr);
			  }
			  
			  gr.setColor(Color.WHITE);
			  for(Edge e : g.connections){
				  gr.drawLine(e.v1.x, e.v1.y, e.v2.x, e.v2.y);
			  }
			  
			  //draw search path if there is one
			  if(pathList!=null){
				  gr.setColor(Color.green);
				  for(int i = 0; i < pathList.size()-1; i++){
					  gr.drawLine(pathList.get(i).getX(), pathList.get(i).getY(), pathList.get(i+1).getX(), pathList.get(i+1).getY());
				  }
			  }
		  }
	  }
	  
	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println("Mouse");
		int x = e.getX();
		int y = e.getY();
			//add vertex mode
			if(Mode == 0){
				//make sure vertices are not overlapping
				for(Vertex v : g.vertices.values()){
					Circle c = (Circle) v.info;
					 if(c.isOn(x, y)){
						 return;
					 }
				}
				String name = JOptionPane.showInputDialog("Name the node:");
				if(name!=null){
				Circle c = new Circle (name, x, y);
				g.addVertex(c, x, y);
				System.out.println("Added: " + e.getX() + ", " + e.getY());
				this.repaint();
				}
			}
			//remove vertex mode
			//*****EDIT, how to remove the associated vertex and edges in graph?
			else if(Mode == 1){
				for(Vertex v : g.vertices.values()){
					Circle c = (Circle) v.info;
					if(c.isOn(x, y)){
						 g.removeVertex(c);
						 this.repaint();
						 return;
					}
				}
			}
			//connect vertex mode
			else if(Mode == 2){
				for(Vertex v : g.vertices.values()){
					Circle c = (Circle) v.info;
					if(c.isOn(x, y)){
						 if(cStored==null){
							 cStored = c;
							 c.changeColor(Color.GREEN);
						 }else if(!c.equals(cStored) && !connectionExists(c, cStored)){
							 c.changeColor(Color.GREEN);
							 this.repaint();
							 int dialogButton = JOptionPane.YES_NO_OPTION;
							 int dialogResult = JOptionPane.showConfirmDialog(this, "Connect", "Confirm Connection", dialogButton);
							 if(dialogResult == 0){
								g.connect(c, cStored);
							 }
							 //add connection line
							 c.changeColor(Color.WHITE);
							 cStored.changeColor(Color.WHITE);
							 cStored = null;
						 }else{
							 //user can cancel their selected node by clicking it again
							 cStored.changeColor(Color.WHITE);
							 int dialogButton = JOptionPane.YES_NO_OPTION;
							 int dialogResult = JOptionPane.showConfirmDialog(this, "Remove Connection", "Connection Exists", dialogButton);
							 if(dialogResult == 0){
								removeConnection(cStored, c);
							 }
							 cStored = null;
						 }
						 this.repaint();
						 break;
					 }
				}	 
				this.repaint();
			}
			//Dijkstra Search Mode
			else if(Mode == 3){
				for(Vertex v : g.vertices.values()){
					Circle c = (Circle) v.info;
					if(c.isOn(x, y)){
						//when user has not selected initial vertex
						 if(cStored==null){
							 //change all vertex to white
							 allWhite();
							 cStored = c;
							 c.changeColor(Color.GREEN);
						 }
						 //when user selected initial vertex and current selection is different
						 else if(!c.equals(cStored)){
							 c.changeColor(Color.GREEN);
							 this.repaint();
							 int dialogButton = JOptionPane.YES_NO_OPTION;
							 int dialogResult = JOptionPane.showConfirmDialog(this, "Navigate", "Find Path", dialogButton);
							 if(dialogResult == 0){
								pathList = g.search(c, cStored);
								this.repaint();
							 }else{
								 cStored.changeColor(Color.WHITE);
								 c.changeColor(Color.WHITE);
							 }
							 cStored = null;
						 }
						 //when user selected same vertex as initial
						 else{
							 cStored.changeColor(Color.WHITE);
							 cStored = null;
						 }
						 this.repaint();
						 break;
					 }
				}	 
				this.repaint();
			}
	}
	
	//set all vertex color to white
	private void allWhite() {
		for(Vertex v2 : g.vertices.values()){
			Circle c2 = (Circle) v2.info;
			c2.changeColor(Color.WHITE);
	 }
	 this.repaint();
	}

	private void removeConnection(Circle c1, Circle c2) {
		for(Edge e : g.vertices.get(c1).neighbors){
			if(e.getneighbor(g.vertices.get(c1)).info.equals(c2)){
				g.vertices.get(c1).neighbors.remove(e);
				g.vertices.get(c2).neighbors.remove(e);
				System.out.print("remove connection 1");
				break;
			}
		}
		// remove connection edge from the connections hash set in graph
		for(Edge e: g.connections){
			if(e.v1.info.equals(c1) && e.v2.info.equals(c2)){
				g.connections.remove(e);
				break;
			}
			if(e.v2.info.equals(c1) && e.v1.info.equals(c2)){
				g.connections.remove(e);
				break;
			}
		}
	}

	//check if a connection is already stored in connectionArrayList
	private boolean connectionExists(Circle c, Circle cStored2) {
		for(Edge e : g.vertices.get(c).neighbors){
			if(e.getneighbor(g.vertices.get(c)).info.equals(cStored2)){
				System.out.print("Connection Exists");
				return true;
			}
		}
		System.out.print("No Connection");
		return false;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
	

	class Circle{
		
		String name;
		int width, UpperLeftX, UpperLeftY;
		Color c;
		Point point;
		
		public Circle(String name, int x, int y) {
			this.name = name.trim();
			//if no input, set name to a space to serve as space filler
			if(name.trim().length()==0){this.name = " ";}
			point = new Point(x,y);
			width = 30;
			c = Color.WHITE;
			//x and y are center coordinates
			UpperLeftX = x-width/2;
			UpperLeftY = y-width/2;
		}
		
		public void changeColor(Color c){
			this.c = c;
		}

		public int getX(){
			return point.x;
		}
		
		public int getY(){
			return point.y;
		}

		public void draw(Graphics g) {
			UpperLeftX = point.x-width/2;
			UpperLeftY = point.y-width/2;
			g.setColor(c);
			g.fillOval(UpperLeftX, UpperLeftY, width, width);
			g.drawString(name, UpperLeftX, UpperLeftY);
		}

		public boolean isOn(int mouseX, int mouseY) {
			if(Math.sqrt((mouseX-point.x)*(mouseX-point.x)+(mouseY-point.y)*(mouseY-point.y)) <= width/2){
				System.out.println("true");
				return true;
			}	
			return false;
		}

		public void resize(int width) {
			this.width = width;
			UpperLeftX = point.x-width/2;
			UpperLeftY = point.y-width/2; 
		}

	}
	
	

	public static void main(String[] args){
		MapGraphGame myGame = new MapGraphGame();
	}
}
