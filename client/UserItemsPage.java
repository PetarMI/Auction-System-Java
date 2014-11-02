import javax.swing.*;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

//window that shows the user their auctions
public class UserItemsPage extends JFrame
{
	//area to display item info
	private JTextArea area;
	private JButton close;
	
	//store received items
	private ArrayList<Item> items;
	
	public UserItemsPage(ArrayList<Item> list, String header)
	{
		super(header);
		this.items = list;
		area = new JTextArea(20, 17);
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
		
		//display auctions
		writeItems();
		
		this.pack();
		this.setVisible(true);
	}
	
	//displaying the auctions
	public void writeItems()
	{
		area.setText("");
		
		//depending on the status of the items use specific formatting
		for (Item i : items)
		{
			if (!i.isClosed())
			{
				area.append(formatItem(i));
			}
			else
			{
				area.append(formatClosedItem(i));
			}
		}
	}
	
	//displaying active auction
	private String formatItem(Item item)
	{
		Calendar st = item.getStartTime();
		Calendar ct = item.getEndTime();
		float pr = item.getBids().isEmpty() ? 0 : item.checkHighestBid().getPrice();
		
		//display the current highest bid and times 
		return (item.getTitle() + " (" + item.getStatus() + ")" + "\n" + 
				"Reserve price: " + item.getReservePrice() + "\n" +
				"Highest bid: " + pr + "\n" + 
				"Start date ---> " + st.get(Calendar.HOUR_OF_DAY) + ":" + st.get(Calendar.MINUTE) + " " + st.get(Calendar.DAY_OF_MONTH) + "/" + st.get(Calendar.MONTH) + "/" + st.get(Calendar.YEAR) + "\n" +
				"Start date ---> " + ct.get(Calendar.HOUR_OF_DAY) + ":" + ct.get(Calendar.MINUTE) + " " + ct.get(Calendar.DAY_OF_MONTH) + "/" + ct.get(Calendar.MONTH) + "/" + ct.get(Calendar.YEAR) + "\n\n");
	}
	
	//displaying the info for closed auctions
	private String formatClosedItem(Item item)
	{
		//get winner
		String winner = item.getSuccess() ? item.checkHighestBid().getBidder() : "No winner";
		//get the price
		Float pr = item.getBids().isEmpty() ? null : item.checkHighestBid().getPrice();
		
		//return formatted auction
		return (item.getTitle() + " (" + item.getStatus() + ")" + "\n" + 
				"Reserve price: " + item.getReservePrice() + "\n" +
				"Winner: " + winner + " - " + pr +"\n\n");
	}
	
	class CloseListener implements ActionListener
	{
		private UserItemsPage uip;
		
		public CloseListener(UserItemsPage p)
		{
			this.uip = p;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			uip.dispose();
		}	
	}
}
