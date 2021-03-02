package dijkstra;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
	
	MapGraphGame(){
		g = new Graph<Circle>();
		Mode = 0;
		//MainPanel
		JPanel MainPanel = new JPanel();
		BoxLayout layout = new BoxLayout(MainPanel, BoxLayout.Y_AXIS);
		//Control Panel
			//Mode Selector
			String[]s1 = {"Add Vertex", "Remove Vertex", "Manage Connection"};
			JComboBox ModeSelector = new JComboBox(s1);
			ModeSelector.setSelectedIndex(0);
			ModeSelector.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){
					Mode = ModeSelector.getSelectedIndex();
					System.out.println(Mode);
				}
			});
			//Confirm Setup Button
			JButton ConfirmButton = new JButton("Confirm Setup");
			ConfirmButton.setPreferredSize(new Dimension (150, 30));
			ConfirmButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){
					ConfirmSetup();
				}
			});
		JPanel ControlPanel = new JPanel();
		ControlPanel.add(ModeSelector);
		ControlPanel.add(ConfirmButton);
		ControlPanel.setPreferredSize(new Dimension (width-50, height/10));
		ControlPanel.setBorder(BorderFactory.createTitledBorder("SetUp"));
		MainPanel.add(ControlPanel);
		//Game Panel
		Gamepanel gp = new Gamepanel();
		gp.setPreferredSize(new Dimension (width-50, 7*height/8));
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
		//***SETUP
		//build graph
		g = new Graph<Circle>();

		//write text file
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
		
		public Gamepanel(){
			//background image
			try{
				img = ImageIO.read(new File(BackgroundPath));
			}catch (IOException e){
				e.printStackTrace();
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
				Circle c = new Circle (x, y);
				g.addVertex(c, x, y);
				System.out.println("Added: " + e.getX() + ", " + e.getY());
				this.repaint();
				//Circle is the type of information that vertices carries
				//g.addVertex(c, e.getX(), e.getY());
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
				System.out.println(connectionArrayList.size());
			}
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
		
		private int x, y, width, UpperLeftX, UpperLeftY;
		Color c;
		
		
		public Circle(int x, int y) {
			this.x = x;
			this.y = y;
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
			return x;
		}
		
		public int getY(){
			return y;
		}

		public void draw(Graphics g) {
			UpperLeftX = x-width/2;
			UpperLeftY = y-width/2;
			g.setColor(c);
			g.fillOval(UpperLeftX, UpperLeftY, width, width);
		}

		public boolean isOn(int mouseX, int mouseY) {
			if(Math.sqrt((mouseX-x)*(mouseX-x)+(mouseY-y)*(mouseY-y)) <= width/2){
				System.out.println("true");
				return true;
			}	
			return false;
		}

		public void resize(int width) {
			this.width = width;
			UpperLeftX = x-width/2;
			UpperLeftY = y-width/2;	
		}

	}
	
	public static void main(String[] args){
		MapGraphGame myGame = new MapGraphGame();
	}
}
