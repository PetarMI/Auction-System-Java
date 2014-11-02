import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

public class LogInPage extends JFrame
{
	private JLabel username = new JLabel("Username");
	private JLabel pass = new JLabel("Password");
	//field for username
	private JTextField user = new JTextField(15);
	//field for password
	private JPasswordField password = new JPasswordField(15);
	//buttons
	private JButton logIn = new JButton("Log In");
	private JButton register = new JButton("Register"); 
	
	private ClientComms comms;
	
	//constructor for when the application is opened for the fist time and it has to connect to a port
	public LogInPage(int port)
	{
		this.setTitle("Log In Page");
		this.setSize(400, 300);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		password.setEchoChar('*');
		
		//actions upon closing of the window
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent windowEvent)
			{
				//sends a message to server to stop the thread that deals with that user
	            comms.sendMessage(new DisconnectMessage("dummy"));
	            //close the communication means of this client
	            comms.close();
	        }        
		});
		//create communications and connect to specified port
		comms = new ClientComms();
		comms.connect(port);
	}

	//constructor for when login page is reopened after a user logs out
	public LogInPage(ClientComms c)
	{
		this.setTitle("Log In Page");
		this.setSize(400, 300);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		password.setEchoChar('*');
		
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent windowEvent)
			{
	            comms.sendMessage(new DisconnectMessage("dummy"));
	            comms.close();
	        }        
		});
		
		//take the already created comms
		this.comms = c;
	}
	
	public void init()
	{
		//pane for the window 
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		this.setContentPane(panel);
		
		//arrange pane
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(0, 0, 10, 10);
		panel.add(username, c);
		c.gridx = 1;
		c.gridwidth = 2;
		panel.add(user, c);
		c.gridy = 1;
		c.gridx = 0;
		c.gridwidth = 1;
		panel.add(pass, c);
		c.gridx = 1;
		c.gridwidth = 2;
		panel.add(password, c);
		c.gridwidth = 1;
		c.gridy = 2;
		c.gridx = 1;
		panel.add(logIn, c);
		c.gridx = 2;
		panel.add(register, c);
		
		//add action listeners
		register.addActionListener(new RegisterListener(this.comms));
		logIn.addActionListener(new LogInListener(this));
		
		this.setVisible(true);
	}
	
	//if a user wants to register
	class RegisterListener implements ActionListener
	{
		private ClientComms comms;
		
		public RegisterListener(ClientComms c)
		{
			this.comms = c;
		}
		
		public void actionPerformed(ActionEvent e) 
		{
			//pass communication to the new window for registration
			RegisterPage p = new RegisterPage(this.comms);
			p.init();
		}
		
	}
	
	//when user wants to loging
	class LogInListener implements ActionListener
	{
		LogInPage lp;
		
		public LogInListener(LogInPage l)
		{
			this.lp = l;
		}
		
		public void actionPerformed(ActionEvent e) 
		{
			//check if fields are filled
			if (lp.checkCredentials())
			{
				//send message to server with the provided credentials
				comms.sendMessage(new LogInMessage(lp.user.getText(), lp.password.getPassword()));
				//receive user object depending on login being successful or not
				User user = (User) comms.receiveLogInConfirmation();
				//if a user is received open a new session for that user
				if (user != null)
				{
					HomePage hp = new HomePage(user, lp.comms);
					hp.init();
					lp.dispose();
				}
				//if no user is received login was unsuccessful
				else
				{
					JOptionPane.showMessageDialog(lp, "Username and password do not match or\n you are already logged in",
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			else
			{
				JOptionPane.showMessageDialog(lp, "Not all spaces have been filled", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	//check if all fields are filled
	private boolean checkCredentials()
	{
		if (!user.getText().isEmpty() && (password.getPassword().length != 0))
		{
			return true;
		}
		
		return false;
	}
}


