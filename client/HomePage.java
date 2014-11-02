import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

//home page of the auction
public class HomePage extends JFrame
{
	//combobox to choose the seller from
	private JTextField seller= new JTextField(10);
	//combobox to choose the category from
	private JComboBox<String> category;
	//choose item by its ID number
	private JTextField itemID = new JTextField(10);
	//Display the items according to the filters
	private JList<Item> items;
	private DefaultListModel<Item> itemsModel;
	//Area for item info
	private JTextArea itemInfo = new JTextArea(5, 15);
	//show photo of item if there is one
	private JLabel itemPhoto = new JLabel();
	//Buttons
	private JButton filter = new JButton("Refine search");
	private JButton sell = new JButton("Sell item");
	private JButton bid = new JButton("Bid");
	private JButton checkBids = new JButton("Check Bids");
	private JButton checkOldBids = new JButton("Check Old Bids");
	private JButton checkItems = new JButton("Check Active Auctions");
	private JButton checkOldItems = new JButton("Check Closed Auctions");
	private JButton logOut = new JButton("Log Out");
	
	//store items that are last sent by the server
	private ArrayList<Item> list = new ArrayList<Item>();
	//comparator used to sort the items
	private Comparator<Item> comparator = Item.ItemNameComparator;
	//disable showing of auctions that have not started yet
	private JCheckBox showInactiveItems = new JCheckBox("Show \"To Start\" auctions", false);
	
	//user that is currently logged in
	private User user;
	private ClientComms comms;
	
	public HomePage(User u, ClientComms c)
	{	
		this.user = u;
		this.comms = c;
		
		//model of the JList that displays auctions
		itemsModel = new DefaultListModel<Item>();
		items = new JList<Item>(itemsModel);
	
		//actions upon closing of the window
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent windowEvent)
			{
				//sends a message to server to stop the thread that deals with that user
	            comms.sendMessage(new DisconnectMessage(user.getName()));
	            //close the communication means of this client
	            comms.close();
	        }        
		});
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setTitle("Home Page");
		this.setSize(850, 600);
		this.setLayout(new BorderLayout());
	}
	
	public void init()
	{
		//panel for viewing items
		JPanel itemPanel = new JPanel();
		itemPanel.setLayout(new GridBagLayout());
		//panel for user actions
		JPanel actionPanel = new JPanel();
		
		String[] cats = {"Any", "Books", "Clothing", "Electronics", "Games", "Hardware", "Music", "Sport", "Software"};
		category = new JComboBox<String>(cats);
		
		//making the items panel
		JLabel bySeller = new JLabel("By Seller");
		JLabel byCategory = new JLabel("By Category");
		JLabel byID = new JLabel("By Item ID");
		JLabel loggedUser = new JLabel("Logged in as " + user.getName());
		JLabel sort = new JLabel("Sort");
		JScrollPane scrPane = new JScrollPane(items);
		JScrollPane scrPaneInfo = new JScrollPane(itemInfo);
		itemPhoto.setIcon(null);
		itemPhoto.setText("No Photo avaliable");
		
		//specify how to sort items
		JRadioButton byName = new JRadioButton("By name", true);
		JRadioButton byAuctioner = new JRadioButton("By seller", false);
		JRadioButton byEndDate = new JRadioButton("By end date", false);
		
		//displayng the panel 
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(0, 0, 10, 10);
		c.gridy = 0;
		c.gridx = 3;
		itemPanel.add(new JLabel("Auctions"), c);
		c.gridy = 0;
		c.gridx = 4;
		itemPanel.add(new JLabel("Item Info"), c);
		c.gridx = 0;
		c.gridy = 1;
		itemPanel.add(bySeller, c);
		c.gridx = 1;
		itemPanel.add(seller, c);
		c.gridy = 2;
		c.gridx = 0;
		itemPanel.add(byCategory, c);
		c.gridx = 1;
		itemPanel.add(category, c);
		c.gridy = 3;
		c.gridx = 0;
		itemPanel.add(byID, c);
		c.gridx = 1;
		itemPanel.add(itemID, c);
		c.gridwidth = 2;
		c.gridy = 4;
		c.gridx = 0;
		itemPanel.add(showInactiveItems, c);
		c.gridwidth = 1;
		c.gridy = 5;
		c.gridx = 0;
		itemPanel.add(filter, c);
		c.gridy = 6;
		c.gridx = 0;
		itemPanel.add(sort, c);
		c.gridx = 1;
		itemPanel.add(byName, c);
		c.gridy = 7;
		c.gridx = 1;
		itemPanel.add(byAuctioner, c);
		c.gridy = 8;
		itemPanel.add(byEndDate, c);
		c.gridy = 9;
		itemPanel.add(loggedUser, c);
		c.gridy = 10;
		c.gridx = 3;
		itemPanel.add(bid, c);
		c.gridheight = 9;
		c.gridy = 1;
		c.gridx = 3;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		itemPanel.add(scrPane, c);
		c.gridheight = 5;
		c.gridx = 4;
		itemPanel.add(scrPaneInfo, c);
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.anchor = GridBagConstraints.CENTER;
		c.gridheight = 4;
		c.gridy = 6;
		itemPanel.add(this.itemPhoto, c);
		
		//sorting the items
		ButtonGroup searchGroup = new ButtonGroup();
		searchGroup.add(byName);
		searchGroup.add(byAuctioner);
		searchGroup.add(byEndDate);
		
		//listener that changes the way items are sorted
		byName.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED)
				{
					comparator = Item.ItemNameComparator;
					Collections.sort(list, comparator);
					writeItems();
				}
			}
		});
		
		//listener that changes the way items are sorted
		byAuctioner.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED)
				{
					comparator = Item.ItemSellerComparator;
					Collections.sort(list, comparator);
					writeItems();
				}
			}
		});
		
		//listener that changes the way items are sorted
		byEndDate.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED)
				{
					comparator = Item.ItemEndDateComparator;
					Collections.sort(list, comparator);
					writeItems();
				}
			}
		});
		
		//listener for button that user requests items with
		filter.addActionListener(new FilterListener(this));
		//add panel to the main panel
		this.add(itemPanel, BorderLayout.CENTER);
		
		//forbid the user to make bids at that stage
		bid.setEnabled(false);
		itemInfo.setEditable(false);
		
		//making the action panel that contains buttons for user requests
		actionPanel.add(sell);
		actionPanel.add(checkBids);
		actionPanel.add(checkOldBids);
		actionPanel.add(checkItems);
		actionPanel.add(checkOldItems);
		actionPanel.add(logOut);
		
		//listeners for buttons that user uses to get info from server
		items.addListSelectionListener(new SelectItemListener());
		UserListener ul = new UserListener(this);
		sell.addActionListener(ul);
		bid.addActionListener(ul);
		checkBids.addActionListener(ul);
		checkOldBids.addActionListener(ul);
		checkItems.addActionListener(ul);
		checkOldItems.addActionListener(ul);
		logOut.addActionListener(new LogOutListener(this));
		
		//add panel to main panel
		this.add(actionPanel, BorderLayout.SOUTH);
		
		this.setVisible(true);
	}
	
	//write the items that are sent from the server to the JList
	private void writeItems()
	{
		this.itemsModel.clear();
		for (int i = 0; i < list.size(); i++)
		{
			//add all elements from list to the model
			itemsModel.addElement(list.get(i));
		}
	}
	
	//listener for when the user requests auctions from the server
	class FilterListener implements ActionListener
	{
		private HomePage hp;
		
		public FilterListener(HomePage hp)
		{
			this.hp = hp;
		}
		
		//when user wants to receive auctions
		public void actionPerformed(ActionEvent e) 
		{
			//get specified seller
			String sell = seller.getText();
			//get specified category
			String cat = category.getSelectedItem().toString();
			//get specified item id
			String id = itemID.getText();
			
			//if user wants some items but not by ID
			if (id.isEmpty())
			{
				//send message with the specified by user filters
				hp.comms.sendMessage(new RequestItemsMessage(user.getName(), sell, cat, null, showInactiveItems.isSelected(), false));
				list = comms.receiveItems();
				//sort the items received by the specified comparator
				Collections.sort(list, comparator);
				//display items to user
				writeItems();
			}
			//when user wants a specific item
			else
			{
				if (id.matches("\\d+"))
				{
					//send message with the specified by user filters
					comms.sendMessage(new RequestItemsMessage(user.getName(), sell, cat, Long.parseLong(id), showInactiveItems.isSelected(), false));
					//sort the items received by the specified comparator
					list = comms.receiveItems();
					//display items to user
					writeItems();
				}
				else
				{
					JOptionPane.showMessageDialog(hp, "ID format is not correct", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			
			//after receiving items no item is selected so fields for item info and photo are empty
			itemInfo.setText("");
			itemPhoto.setIcon(null);
			itemPhoto.setText("No Photo avaliable");
		}
	}
	
	//listener for the other operation the user might use
	//it is a design decision to use one listener for the rest of the actions instead of many simple listeners for each action
	class UserListener implements ActionListener
	{
		private HomePage hp;
		
		public UserListener(HomePage hp)
		{
			this.hp = hp;
		}
		
		public void actionPerformed(ActionEvent e) 
		{
			//if user wants to sell a new item
			if (e.getSource() == sell)
			{
				//open window for selling an item
				SellPage sp = new SellPage(hp.user, hp.comms);
				sp.init();
			}
			//if user wants to bid for an item
			else if (e.getSource() == bid)
			{
				//open window for bidding for an item
				BidPage bp = new BidPage(hp.user, hp.comms, items.getSelectedValue());
				bp.init();
			}
			//if user wants to check their bids on active auctions
			else if (e.getSource() == checkBids)
			{
				//get list of bids
				comms.sendMessage(new RequestBidsMessage(hp.user.getName(), false));
				ArrayList<Bid> bids = comms.receiveBids();
				
				//display then in a new window
				UserBidPage ubp = new UserBidPage(bids, "Bids on ongoing Auctions");
				ubp.init();
			}
			//when user wants a list of their active auctions ant their state
			else if (e.getSource() == checkItems)
			{
				//get list of auctions
				comms.sendMessage(new RequestItemsMessage(user.getName(), hp.user.getName(), "Any", null, true, false));
				ArrayList<Item> list = comms.receiveItems();
				
				//display in new window
				UserItemsPage uip = new UserItemsPage(list, "Your ongoing auctions");
				uip.init();
			}
			//if user wants to check their bids on closed auctions
			else if (e.getSource() == checkOldBids)
			{
				//get list of bids
				comms.sendMessage(new RequestBidsMessage(hp.user.getName(), true));
				ArrayList<Bid> bids = comms.receiveBids();
				
				//display then in a new window
				UserBidPage ubp = new UserBidPage(bids, "Bids on Closed Auctions");
				ubp.init();
			}
			//when user wants a list of their closed auctions ant their state
			else if (e.getSource() == checkOldItems)
			{
				//get list of auctions
				comms.sendMessage(new RequestItemsMessage(user.getName(), hp.user.getName(), "Any", null, true, true));
				ArrayList<Item> list = comms.receiveItems();
				
				//display in new window
				UserItemsPage uip = new UserItemsPage(list, "Your finished auctions");
				uip.init();
			}
		}
	}
	
	//when user selects an item from the list of auctions
	class SelectItemListener implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent e)
		{
			if (e.getValueIsAdjusting() == false) 
			{
				if (items.getSelectedIndex() == -1)
				{
		            //No selection, disable fire button.
		            bid.setEnabled(false);    
				} 
				else 
				{
					//Selection of an item
					//clear info about previous item 
					itemInfo.setText("");
					Item selectedItem = items.getSelectedValue();
					//show info about new item
					itemInfo.append(formatItem(selectedItem));
					//is user is selling the item or if the auction is not active forbid making of a bid
					if (selectedItem.getSeller().equals(user.getName()) || !selectedItem.isActive())
					{
						bid.setEnabled(false);
					}
					else
					{
						//else enable bid making
						bid.setEnabled(true);
					}
					//depending on the presence of a photo display it
					if (selectedItem.getPhoto() != null)
					{
						itemPhoto.setIcon(selectedItem.getPhoto());
						itemPhoto.setText("");
					}
					else
					{
						itemPhoto.setIcon(null);
						itemPhoto.setText("No Photo avaliable");
					}
		        }
		    }
		}
	}
	
	//when user logs out
	class LogOutListener implements ActionListener
	{
		private HomePage hp;
		
		public LogOutListener(HomePage hp)
		{
			this.hp = hp;
		}
		
		public void actionPerformed(ActionEvent e) 
		{
			//close main page
			hp.comms.sendMessage(new LogOutMessage(hp.user.getName()));
			hp.dispose();
			//open new login page
			LogInPage lip = new LogInPage(hp.comms);
			lip.init();
		}
		
	}
	
	//formating an item and the way it is displayed in the area for item information
	private String formatItem(Item item)
	{
		Calendar st = item.getStartTime();
		Calendar ct = item.getEndTime();
		float pr = item.getBids().isEmpty() ? 0 : item.checkHighestBid().getPrice();
		
		return (item.getID() + " " + item.getTitle() + " (" + item.getStatus() + ")" + "\n" + 
				"Seller: " + item.getSeller() + "\n" +
				"Description: " + item.getDescription() + "\n" + 
				"Highest Bid " + pr + "\n" +
 				"Start time ---> " + st.get(Calendar.HOUR_OF_DAY) + ":" + st.get(Calendar.MINUTE) + " " + st.get(Calendar.DAY_OF_MONTH) + "/" + (st.get(Calendar.MONTH) + 1) + "/" + st.get(Calendar.YEAR) + "\n" +
				"End time ---> " + ct.get(Calendar.HOUR_OF_DAY) + ":" + ct.get(Calendar.MINUTE) + " " + ct.get(Calendar.DAY_OF_MONTH) + "/" + (ct.get(Calendar.MONTH) + 1) + "/" + ct.get(Calendar.YEAR) + "\n\n");
	}
}