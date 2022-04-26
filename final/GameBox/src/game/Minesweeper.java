package game;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;

public class Minesweeper extends JFrame {
    private JLabel status;

    public Minesweeper(String name) {
    	
        
        	status = new JLabel("");
        	add(status, BorderLayout.SOUTH);
        	add(new Board(status,name));
        	setResizable(false);
        	pack();
        	setTitle("Minesweeper");
        	setLocationRelativeTo(null);
        	setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
    }

    
}