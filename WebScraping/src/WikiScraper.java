import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Scrollable;

public class WikiScraper {
	private final int height=800, width=1000;
	private JTextArea input, display;
	
	WikiScraper(){
		JPanel panel = new JPanel();
		BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
		panel.setBorder(BorderFactory.createTitledBorder("Wiki Direct"));
		
		input = new JTextArea();
		input.setEditable(true);
		input.setPreferredSize(new Dimension(width-100, height/6));
		display = new JTextArea();
		display.setEditable(false);
		display.setLineWrap(true);
		display.setWrapStyleWord(true);
		JScrollPane scroll = new JScrollPane(display, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setPreferredSize(new Dimension(width-100, 2*height/3));



		
		input.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == '\n')
					search();
				}
			public void keyPressed(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {}
		});
		
		JButton sendButton = new JButton("Search");
		sendButton.setPreferredSize(new Dimension (width-500, 30));
		sendButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				search();
			}
		});
		
		panel.add(input);
		panel.add(scroll);
		panel.add(sendButton);
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(width, height);
		frame.setFocusable(true);
		frame.setResizable(false);
		frame.add(panel);
		frame.setVisible(true);
		
		display.setText("Welcome to Wiki Direct \n The most efficient tool to learn!");;
	}
	
	public void search(){
		
		if (!input.getText().trim().equals("")){
			String keyword = input.getText().trim().toLowerCase();
			try {
    			//connects to the given url
				Document doc = Jsoup.connect("https://en.wikipedia.org/wiki/" + keyword).get();
				
				Elements paragraphs = doc.select(".mw-content-ltr p");

			    Element firstParagraph = paragraphs.first();
			    Element lastParagraph = paragraphs.last();
			    Element p;
			    String s = "";
			    p = firstParagraph;
			    System.out.println(p.text());
			    for (int i = 1; p != lastParagraph; i++) {
			        p = paragraphs.get(i);
			        s += p.text();
			        s += "\n\n";
			    }
			    display.setText(s);
				
    		} catch (Exception e) {
				display.setText("Error in connection or keyword search");
				input.setText("");
			}
		}
		
		
	}
	
	
public static void main(String[] args) {
	new WikiScraper();
}
}