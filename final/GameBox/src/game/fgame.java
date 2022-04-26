package game;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
public class fgame extends JFrame {
	private static fgame obj=null;
	public static fgame getinstance(){ 
		if (obj == null) 
		{
			obj=new fgame();
		}
		
		return obj;
					
	}
    private fgame() { 
    	Object[] options = {"Login",
        "Register"};
    	int n = JOptionPane.showOptionDialog(null,
    			"Would you like to Login or Register?",
    			"Hello",
    			JOptionPane.YES_NO_OPTION,
    			JOptionPane.QUESTION_MESSAGE,
    			null,     //do not use a custom Icon
    			options,  //the titles of buttons
    			options[1]); //default button title
    	if(n==-1)
    	{
    		System.exit(0);
    	}
    	String name = (String)JOptionPane.showInputDialog(null, "Enter name",
    			"Input", JOptionPane.QUESTION_MESSAGE,null,null,"");
    	if(name==null)
    	{
    		System.exit(0);
    	}
    	while(name.equals(""))
    	{
    		name =JOptionPane.showInputDialog(null,"Enter email:","Name not entered",JOptionPane.WARNING_MESSAGE);
    		if(name==null)
	    	{
	    		System.exit(0);
	    	}
    		
    	}
    	String password = (String)JOptionPane.showInputDialog(null, "Enter password",
    			"Input", JOptionPane.QUESTION_MESSAGE,null,null,"");
		if(password==null)
    	{
    		System.exit(0);
    	}
		while(password.equals(""))
    	{
    		password =JOptionPane.showInputDialog(null,"Enter password:","Password not entered",JOptionPane.WARNING_MESSAGE);
    		if(password==null)
	    	{
	    		System.exit(0);
	    	}
    	}
		String n1=name;
    	int ans=1;
    	int ct=0;
    	while(ans==1)
    	{
    	    ans=0;
    	    if(ct!=0)
    	    {
    	    	
    	    	name =(String)JOptionPane.showInputDialog(null, "Enter name",
    	    			"Input", JOptionPane.QUESTION_MESSAGE,null,null,"");
    	    	if(name==null)
    	    	{
    	    		System.exit(0);
    	    	}
    	    	while(name.equals(""))
    	    	{
    	    		name =JOptionPane.showInputDialog(null,"Enter email:","Name not entered",JOptionPane.WARNING_MESSAGE);
    	    		if(name==null)
        	    	{
        	    		System.exit(0);
        	    	}
    	    		
    	    	}
    	    	password = (String)JOptionPane.showInputDialog(null, "Enter password",
    	    			"Input", JOptionPane.QUESTION_MESSAGE,null,null,"");
    			if(password==null)
    	    	{
    	    		System.exit(0);
    	    	}
    			while(password.equals(""))
    	    	{
    	    		password =JOptionPane.showInputDialog(null,"Enter password:","Password not entered",JOptionPane.WARNING_MESSAGE);
    	    		if(password==null)
        	    	{
        	    		System.exit(0);
        	    	}
    	    	}
    	    }
    	    ct+=1;
    		if(n==1)
    		{
    		
        	
    			try {
    				Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/loginreg", "root", "");
    				String query1 = "SELECT password FROM lr WHERE name='"+name+"'";
    				Statement sta1 = connection.createStatement();
    				ResultSet y = sta1.executeQuery(query1);
    				if(y.next())
    				{
    					ans=1;
    				  	String[] buttons = {"Login"};

    				  	int rc = JOptionPane.showOptionDialog(null, "You have already registered!", "Retry",
    				        JOptionPane.WARNING_MESSAGE, 0, null, buttons, buttons[0]);
    				  	n=0;
    				}
    				else
    				{
    					String query2 = "INSERT INTO lr values('" + name + "','" + password + "')";
    					Statement sta2 = connection.createStatement();
    					int x = sta2.executeUpdate(query2);
    					connection.close();
    				}
    			} 
    			catch (SQLException e1) {
    				e1.printStackTrace();
    			}
			
    		}
    		else
    		{
    			try {
    				Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/loginreg", "root", "");
    				String query1 = "SELECT password FROM lr WHERE name='"+name+"'";
    				Statement sta1 = connection.createStatement();
    				ResultSet y = sta1.executeQuery(query1);
    				if(y.next()) 
        			{
    					
    					if(!password.equals(y.getString("password")))
    					{
    						ans=1;
        				  	String[] buttons = {"ok"};

        				  	int rc = JOptionPane.showOptionDialog(null, "Wrong Password", "Retry",
        				        JOptionPane.WARNING_MESSAGE, 0, null, buttons, buttons[0]);
        				
    					}
        			}
    				else
    				{
    					ans=1;
    				  	String[] buttons = {"Register"};

    				  	int rc = JOptionPane.showOptionDialog(null, "You have not registered!", "Retry",
    				        JOptionPane.WARNING_MESSAGE, 0, null, buttons, buttons[0]);
    				  	n=1;
    				}
        		
    			} 
    			catch (SQLException e1) {
    				e1.printStackTrace();
    			}
        	
    		}
    	}
    	JFrame f=new JFrame("GAME BOX");  
    	f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JButton b1=new JButton("Minesweeper");  
        b1.setBounds(80,30,120,40);
        f.add(b1); 
        b1.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                
            	Minesweeper ms = new Minesweeper(n1);
            	ms.setVisible(true);
            	
            	
			}
        });
        
        JButton b2=new JButton("Flappy Bird");  
        b2.setBounds(80,100,120,40);  
        f.add(b2);  
        b2.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                
            	FlappyBird.domain(null,n1);
            	

			}
        });
        JButton b3=new JButton("Snake");  
        b3.setBounds(80,170,120,40);  
        f.add(b3);  
        b3.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
               
            	new GameFrame(n1);

			}
        });
        f.setSize(500,300);  
        f.setLayout(null);  
        f.setVisible(true); 
        JButton b4=new JButton("Minesweeper Leaderboard");  
        b4.setBounds(210,30,120,40);  
        b4.setSize(200,40);
        f.add(b4); 
        b4.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
            	try {
					Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/minedb", "root", "");
					
					String query = "SELECT * FROM mine ORDER BY score DESC";
	        		Statement sta1 = connection.createStatement();
	        		ResultSet y = sta1.executeQuery(query);
	        		String s="";
	        		while(y.next()) {
	        			
	        			s+=y.getString("email")+"  "+y.getInt("score")+"\n";
	        		}
	        		JOptionPane.showMessageDialog(null,s,"Minesweeper Leaderboard",JOptionPane.PLAIN_MESSAGE);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
            	
			}
        });
        JButton b5=new JButton("FlappyBird Leaderboard");  
        b5.setBounds(210,100,120,40);  
        b5.setSize(200,40);
        f.add(b5); 
        b5.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
            	try {
					Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/flappybird", "root", "");
					
					String query = "SELECT * FROM flappy ORDER BY score DESC";
	        		Statement sta1 = connection.createStatement();
	        		ResultSet y = sta1.executeQuery(query);
	        		String s="";
	        		while(y.next()) {
	        			
	        			s+=y.getString("name")+"  "+y.getInt("score")+"\n";
	        		}
	        		JOptionPane.showMessageDialog(null,s,"FlappyBird Leaderboard",JOptionPane.PLAIN_MESSAGE);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
            	
			}
        });
        JButton b6=new JButton("Snake Leaderboard");  
        b6.setBounds(210,170,120,40);  
        b6.setSize(200,40);
        f.add(b6);
        b6.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
            	try {
					Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/snakedb", "root", "");
					
					String query = "SELECT * FROM snake ORDER BY score DESC";
	        		Statement sta1 = connection.createStatement();
	        		ResultSet y = sta1.executeQuery(query);
	        		String s="";
	        		while(y.next()) {
	        			
	        			s+=y.getString("name")+"  "+y.getInt("score")+"\n";
	        		}
	        		JOptionPane.showMessageDialog(null,s,"Snake Leaderboard",JOptionPane.PLAIN_MESSAGE);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
            	
			}
        });
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
    }
	public static void main(String[] args) {
	    fgame f = fgame.getinstance();
     	
    }

}
