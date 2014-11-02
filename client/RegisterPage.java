import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

//page for registering a user
public class RegisterPage extends JFrame
{
	//fields for inputting information
	private JTextField familyName = new JTextField(15);
	private JTextField user = new JTextField(15);
	private JTextField email = new JTextField(15);
	private JPasswordField password = new JPasswordField(15);
	
	private JButton register = new JButton("Register");
	
	private ClientComms comms;
	
	public RegisterPage(ClientComms c)
	{
		this.setTitle("Register Page");
		this.setSize(300, 200);
		password.setEchoChar('*');
		//take comms already created by login page
		this.comms = c;
	}
	
	public void init()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		this.setContentPane(panel);
		
		//labels 
		JLabel username = new JLabel("Username");
		JLabel pass = new JLabel("Password");
		JLabel fullName = new JLabel("Full Name");
		JLabel mail = new JLabel("Email");
		
		//arrange the panel/GUI
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(0, 0, 10, 10);
		panel.add(fullName, c);
		c.gridwidth = 2;
		c.gridx = 1;
		panel.add(familyName, c);
		c.gridy = 1;
		c.gridx = 0;
		c.gridwidth = 1;
		panel.add(username, c);
		c.gridwidth = 2;
		c.gridx = 1;
		panel.add(user, c);
		c.gridy = 2;
		c.gridx = 0;
		c.gridwidth = 1;
		panel.add(pass, c);
		c.gridx = 1;
		c.gridwidth = 2;
		panel.add(password, c);
		c.gridy = 3;
		c.gridx = 0;
		c.gridwidth = 1;
		panel.add(mail, c);
		c.gridy = 3;
		c.gridx = 1;
		c.gridwidth = 2;
		panel.add(email, c);
		c.gridwidth = 1;
		c.gridy = 4;
		c.gridx = 1;
		panel.add(register, c);
		
		register.addActionListener(new RegisterListener(this));
		
		this.setVisible(true);
	}
	
	class RegisterListener implements ActionListener
	{
		private RegisterPage p;
		
		public RegisterListener(RegisterPage p)
		{
			this.p = p;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{	
			//ensure all fields are filled
			if(p.checkCredentials())
			{
				//send a message with the register credentials
				comms.sendMessage(new RegisterMessage(user.getText(), email.getText(), password.getPassword(), familyName.getText()));
				
				//receive info whether registration is successful or not
				boolean s = comms.receiveConfirmationMessage();
				
				//display info to user
				if (s == true)
				{
					JOptionPane.showMessageDialog(p, "Registered successfully", "Reg message", JOptionPane.INFORMATION_MESSAGE);
					p.dispose();
				}
				else
				{
					JOptionPane.showMessageDialog(p, "Error caused by some of:\n"
							+ "1. Username is in use\n2. Email is already taken\n"
							+ "3. Email invalid", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			else
			{
				JOptionPane.showMessageDialog(p, "Not all spaces have been filled", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	//ensure all fields are filled
	public boolean checkCredentials()
	{
		if (!user.getText().isEmpty() && (password.getPassword().length != 0) && !familyName.getText().isEmpty() && !email.getText().isEmpty())
		{
			return true;
		}
		
		return false;
	}
}
