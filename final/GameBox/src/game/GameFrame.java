package game;

import javax.swing.JFrame;

public class GameFrame extends JFrame{


	GameFrame(String name){
		this.add(new GamePanel(name));
		this.setTitle("Snake");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setResizable(false);
		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null);
		
	}
}