import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.*;

public class UserBidPage extends JFrame
{
	//area to display bid info
	private JTextArea area;
	private JButton close;
	
	//store bids to display
	private ArrayList<Bid> bids;
	
	public UserBidPage(ArrayList<Bid> bidList, String header)
	{
		super(header);
		this.bids = bidList;
		area = new JTextArea(15, 15);
		close = new JButton("Close");
	}
	
	public void init()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		this.setContentPane(panel);
		
		area.setEditable(false);
		JScrollPane scr = new JScrollPane(area);
		
		//arrange GUI
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(10, 0, 0, 10);
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		panel.add(scr, c);
		
		GridBagConstraints c1 = new GridBagConstraints();
		c1.gridy = 1;
		panel.add(close, c1);
		
		close.addActionListener(new CloseListener(this));
		
		//display bids in area
		writeBids();
		
		this.pack();
		this.setVisible(true);
	}
	
	//display bids
	public void writeBids()
	{
		area.setText("");
		
		//for every bid display its info
		for (Bid b : bids)
		{
			area.append(formatBid(b) + "\n");
		}
	}
	
	//format the bid according to its state
	private String formatBid(Bid bid)
	{
		//get the winner of the item the bid is for
		String winner = bid.getItem().getSuccess() ? bid.getItem().checkHighestBid().getBidder() : "No winner" ;
		
		//get the info to display depending on whether the auction is ongoing or closed
		//if it is closed show winner if there is such
		//if not show highest bid
		String info = bid.getItem().isClosed() ? ("Winner: " +  winner) : 
			"Highest Bid: " + bid.getItem().checkHighestBid().getPrice();
		
		//return formatted item
		return (bid.getItem().getTitle() + " (Seller: " + bid.getItem().getSeller() + ")" + "\n" + 
				"Your bid: " + bid.getPrice() + "\n" + 
				info + "\n");
	}
	
	//closing the window
	class CloseListener implements ActionListener
	{
		private UserBidPage ubp;
		
		public CloseListener(UserBidPage p)
		{
			this.ubp = p;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			ubp.dispose();
		}	
	}
}
