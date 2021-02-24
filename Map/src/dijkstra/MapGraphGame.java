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
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import dijkstra.MapGraphGame.Circle;

class MapGraphGame {
	int Mode;
	Graph<Circle> g;
	int width = 1000;
	int height = 900;
	//*EDIT* Background image file path
	String BackgroundPath = "/Map/resource/BackgroundImg.jpg";
	
	MapGraphGame(){
		//MainPanel
		JPanel MainPanel = new JPanel();
		BoxLayout layout = new BoxLayout(MainPanel, BoxLayout.Y_AXIS);
		//Control Panel
			//Mode Selector
			String[]s1 = {"Add Vertex", "Remove Vertex", "Add Connection", "Remove Connection"};
			JComboBox ModeSelector = new JComboBox(s1);
			ModeSelector.setSelectedIndex(0);
			ModeSelector.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){
					Mode = ModeSelector.getSelectedIndex();
				}
			});
			//Confirm Setup Button
			JButton ConfirmButton = new JButton("Confirm Setup");
			ConfirmButton.setPreferredSize(new Dimension (150, 50));
			ConfirmButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){
					ConfirmSetup();
				}
			});
		JPanel ControlPanel = new JPanel();
		ControlPanel.add(ModeSelector);
		ControlPanel.add(ConfirmButton);
		ControlPanel.setPreferredSize(new Dimension (width-50, height/8));
		ControlPanel.setBorder(BorderFactory.createTitledBorder("SetUp"));
		MainPanel.add(ControlPanel);
		//Game Panel
		Gamepanel gp = new Gamepanel();
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
		
		public Gamepanel(){
			
			//background image
			try{
				img = ImageIO.read(new File(BackgroundPath));
			}catch (IOException e){
				e.printStackTrace();
			}
		}
		
		@Override
	  public void paintComponent(Graphics g) {
	      super.paintComponent(g);
			g.drawImage(img,0,0,null);
	      drawimg(g);
	      Toolkit.getDefaultToolkit().sync();
	  }
	  
		//method for drawing out the character on the panel
	  private void drawimg(Graphics g) {
		  for(Circle c : circleArrayList){
			  c.draw(g);
		  }
	  }
	  
	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getSource().equals(this)){
			
			//add vertex mode
			if(Mode == 1){
				Circle c = new Circle (e.getX(), e.getY());
				circleArrayList.add(c);
				//Circle is the type of information that vertices carries
				//g.addVertex(c, e.getX(), e.getY());
			}
			//remove vertex mode
			//*****EDIT, how to remove the associated vertex and edges in graph?
			else if(Mode == 2 && circleArrayList.size() != 0){
				int x = e.getX();
				int y = e.getY();
				for(Circle c : circleArrayList){
					 if(c.isOn(x, y)){
						 circleArrayList.remove(c);
						 break;
					 }
				}
			}
			//connect vertex mode
			else if(Mode == 3 && circleArrayList.size() > 1){
				
			}
		}
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
