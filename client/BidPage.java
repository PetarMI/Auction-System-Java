import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

//window for when the user places a bid
public class BidPage extends JFrame 
{
	//field for price
	private JTextField price = new JTextField(10);
	//buttons
	private JButton place = new JButton("Place bid");
	private JButton cancel = new JButton("Cancel");
	
	//user that is currently logged in
	private User user;
	private ClientComms comms;
	//item that the bid is going to be on
	private Item item;
	
	//constructor
	public BidPage(User u, ClientComms c, Item i)
	{
		super("Bid for item");
		this.user = u;
		this.comms = c;
		this.item = i;
		this.setSize(300, 200);
	}
	
	public void init()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		this.setContentPane(panel);

		JLabel pr = new JLabel("Price");
		
		//placing the components within the frame
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(10, 0, 0, 10);
		
		panel.add(pr, c);
		c.gridwidth = 2;
		c.gridx = 1;
		panel.add(price, c);
		c.gridwidth = 1;
		c.gridy = 2;
		panel.add(place, c);
		c.gridx = 2;
		panel.add(cancel, c);
		
		//adding action listeners for the buttons
		place.addActionListener(new BidListener(this));
		cancel.addActionListener(new CancelListener(this));
		this.setVisible(true);
	}
	
	//make a bid for the item
	class BidListener implements ActionListener
	{
		private BidPage bp;
	
		public BidListener(BidPage bp)
		{
			this.bp = bp;
		}
		
		public void actionPerformed(ActionEvent e) 
		{
			//get the price from the text field
			String offeredPrice = price.getText();
			
			//validate price
			if (checkPrice(offeredPrice))
			{
				//send message to the server containing bid info 
				comms.sendMessage(new BidMessage(item.getID(), Float.parseFloat(offeredPrice), user.getName()));
				//receive info about whether makong the bid was successful
				boolean success = comms.receiveConfirmationMessage();
				if(success)
				{
					JOptionPane.showMessageDialog(bp, "Bid added successfully");
					bp.dispose();
				}
				else
				{
					JOptionPane.showMessageDialog(bp, "Price is lower than current highest. Refresh your list!", 
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			else
			{
				JOptionPane.showMessageDialog(bp, "Price is not valid or lower than highest", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}	
	}
	
	//when user cancel making the bid
	class CancelListener implements ActionListener
	{
		private BidPage bp;
		
		public CancelListener(BidPage bp)
		{
			this.bp = bp;
		}

		public void actionPerformed(ActionEvent e)
		{
			//dispose of the frame
			bp.dispose();
		}
	}
	
	//helper method for verifying price is valid
	private boolean checkPrice(String pr)
	{
		//check if field is non empty and if price is a number
		if (!pr.isEmpty() && pr.matches("\\d+\\.?\\d*"))
		{
			//ensure price is positive and higher than current highest bid
			if (Double.parseDouble(pr) > 0 && (item.getBids().isEmpty() || Double.parseDouble(pr) > item.checkHighestBid().getPrice()))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		
		return false;
	}
	
}
