import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;

import javax.mail.MessagingException;
import javax.swing.JTextArea;

public class ClientThread implements Runnable
{
	//comms that the thread uses to communicate with the client
	public ServerComms comms;
	//server lists that the thread refers to 
	private List<User> users;
	private List<Integer> userIDs;
	private List<Item> items;
	private List<Long> itemIDs;
	private List<Item> closedAuctions;
	//using servers class to save info
	private DataPersistance data;
	//automatically writing to the log area of the server
	private JTextArea log;
	//know what the logged clients to the system are
	private Map<String, ServerComms> loggedClients;
	
	public ClientThread(ServerComms sc, List<User> u, List<Integer> uid, List<Item> i, List<Long> ids, 
			List<Item> ca, DataPersistance d, Map<String, ServerComms> lc, JTextArea l)
	{
		this.comms = sc;
		this.users = u;
		this.userIDs = uid;
		this.items = i;
		this.itemIDs = ids;
		this.closedAuctions = ca;
		this.data = d;
		this.loggedClients = lc;
		this.log = l;
	}
	
	//run method of the thread
	public void run()
	{
		Message m;
		//listen to new message constantly
		while(true)
		{		
			//receive the message
			m = comms.receiveMessage();
			
			//find type of message, handle it accordingly and send info back to user
			if (m instanceof RegisterMessage)
			{
				RegisterMessage rm = (RegisterMessage) m;
				boolean success = handleRegistration(rm);
				comms.sendConfirmation(success);
			}
			else if (m instanceof LogInMessage)
			{
				LogInMessage lim = (LogInMessage) m;
				User user = handleLogIn(lim);
				comms.sendLogInSuccess(user);			
			}
			else if (m instanceof SellItemMessage)
			{
				SellItemMessage sim = (SellItemMessage) m;
				boolean success = handleSellRequest(sim);
				comms.sendConfirmation(success);			
			}
			else if (m instanceof RequestItemsMessage)
			{
				RequestItemsMessage rim = (RequestItemsMessage) m;
				ArrayList<Item> list = handleItemsRequest(rim);
				comms.sendItems(list);	
			}
			else if (m instanceof BidMessage)
			{
				BidMessage bm = (BidMessage) m;
				boolean success = handleBidMessage(bm);
				comms.sendConfirmation(success);	
			}
			else if (m instanceof RequestBidsMessage)
			{
				RequestBidsMessage brm = (RequestBidsMessage) m;
				ArrayList<Bid> bids = handleBidRequest(brm);
				comms.sendBids(bids);	
			}
			else if (m instanceof LogOutMessage)
			{
				LogOutMessage lom = (LogOutMessage) m;
				this.handleLogOut(lom.getUser());
			}
			else if (m instanceof DisconnectMessage)
			{
				this.handleLogOut(m.getUser());
				this.comms.close();
				break;
			}
		}
	}
	
	//handle user registration
	public boolean handleRegistration(RegisterMessage rm)
	{
		//generating user id for new user
		Random rand = new Random();
		//desired username
		String username = rm.getUser();
		//user email address
		String email = rm.getEmail();
		
		//check if username or email is already in use by looping through all users
		for (User u : users)
		{
			if (username.equals(u.getName()))
			{
				this.writeLog(("Unsuccessful registration. Username " + "\"" + username + "\" is already taken"));
				return false;
			}
			if (email.equals(u.getEmail()))
			{
				this.writeLog(("Unsuccessful registration. Email " + "\"" + email + "\" is already in use"));
				return false;
			}
		}
		
		//send an email to the user to notify him for registering
		try
		{
			EmailSender es = new EmailSender();
			es.sendMail(email, "You registered to the auction system");
		}
		//if email could not be send registration is unsuccessful and user is prompted to gibe a valid email
		catch(MessagingException e)
		{
			this.writeLog(("Unsuccessful registration. Email " + "\"" + email + "\" does not exist"));
			return false;
		}
		
		//assign a unique ID to the user
		int id = rand.nextInt();
		
		//ensure ID does not exist
		while(itemIDs.contains(id) || id < 1)
		{
			id = rand.nextInt();
		}
		
		//add id to list
		userIDs.add(id);
		//create new user
		User user = new User(username, rm.getFamilyName(), rm.getPassword(), id, email);
		//add him to list
		users.add(user);
		//save new info to file
		data.registerUser(users, userIDs);
		//write to log console
		this.writeLog(("Successful registration. Username " + "\"" + username + "\""));
		return true;
	}
	
	//handle user login request
	public User handleLogIn(LogInMessage lim)
	{
		//get credentials
		String username = lim.getUser();
		char[] pass = lim.getPassword();

		//ensure user is not already logged in
		if (!this.loggedClients.containsKey(username))
		{
			//check if username and password match
			for (User u : users)
			{
				if (username.equals(u.getName()))
				{
					if (Arrays.equals(pass, u.getPassword()))
					{
						this.writeLog(("User " + "\"" + username + "\" logged in to the system"));
						//add user to list of logged users
						this.loggedClients.put(username, this.comms);
						return u;
					}
					else
					{
						this.writeLog(("Failed login atempt because of wron password for username " + "\"" + username + "\""));
						return null;
					}
				}
			}
			this.writeLog(("Failed login atempt for non - existing username " + "\"" + username + "\""));
		}
		else
		{
			this.writeLog(("User " + "\"" + username + "\"" + "tried to connect again"));
		}
		return null;
	}
	
	//handle making of a new auction
	public boolean handleSellRequest(SellItemMessage sim)
	{
		//assign a unique ID to the auction/item
		Random rand = new Random();
		long id = rand.nextLong();
		
		while(itemIDs.contains(id) || id < 1)
		{
			id = rand.nextLong();
		}
		
		itemIDs.add(id);
		
		//create a new item
		Item item = new Item(sim.title, sim.description, sim.pic, sim.category, sim.getUser(), sim.sellerID, id, sim.reservePrice, sim.startTime, sim.closeTime);
		//check if it is active or not
		GregorianCalendar gc = new GregorianCalendar();
		//check if it should be starting and set the state of the auction
		if (gc.after(sim.startTime))
		{
			item.setActive();
		}
		else
		{
			item.setToStart();
		}
		//add item to list
		items.add(item);
		//save item to file
		data.auctionItem(items, itemIDs);
		
		this.writeLog(("User " + "\"" + sim.getUser() + "\" started an auction for " + item.getTitle()));
		return true;
	}
	
	//respond to user request to see some items
	//used to handle user searching for new items, user searching for their active auction and closed auctions as well
	public ArrayList<Item> handleItemsRequest(RequestItemsMessage rim)
	{
		//hold items that should be sent to user
		ArrayList<Item> it = new ArrayList<Item>();
		
		//get info that is used to search for auctions
		String seller = rim.getSeller();
		String category = rim.getCategory();
		Long id = rim.getID();
		boolean showInactive = rim.showInactive();
		boolean showClosed = rim.showClosed();
		
		//depending the items user wants determine which list to read from
		List<Item> temp = showClosed ? closedAuctions : items;
		//loop through list
		for (Item item : temp)
		{
			//check if item matches user criteria
			if((seller.isEmpty() || item.getSeller().equals(seller)) && (category.equals("Any") || item.getCategory().equals(category))
					&& (id == null || id.equals(item.getID())) && (showInactive == true || item.isActive() == true))
			{
				//if so add item to list that is sent back
				it.add(item);
			}
		}
		
		//depending on whether the user that sent the request is the same as the seller looking for write users intentions to log
		if (seller.equals(rim.getUser()))
		{
			if (showClosed)
			{
				this.writeLog(("User " + "\"" + rim.getUser() + "\" requested to see his active auctions"));
			}
			else
			{
				this.writeLog(("User " + "\"" + rim.getUser() + "\" requested to see his closed auctions"));
			}
		}
		else
		{
			this.writeLog(("User " + "\"" + rim.getUser() + "\" requested to see current auctions"));
		}
		
		return it;
	}
	
	//handle user bidding for an item
	public boolean handleBidMessage(BidMessage bm)
	{
		//get info from message
		Long id = bm.getID();
		String u = bm.getUser();
		float pr = bm.getPrice();
		
		//loop through all items
		for (Item it : items)
		{
			//find the item to make the bid on
			if(it.getID() == id && !it.getSeller().equals(u) && it.isActive() && (it.checkHighestBid() == null || pr > it.checkHighestBid().getPrice()))
			{
				//add bid for that item
				it.addBid(new Bid(u, pr, it));
				//update items in the file
				data.updateItem(items);
				this.writeLog(("User " + "\"" + bm.getUser() + "\" placed a bid for" + it.getTitle()));
				return true;
			}
		}
		
		return false;
	}
	
	//handle user request to see his bids either on active auctions or closed auctions
	public ArrayList<Bid> handleBidRequest(RequestBidsMessage rbm)
	{
		//get username that sent message (user to look for)
		String username = rbm.getUser();
		ArrayList<Bid> bidsToReturn = new ArrayList<Bid>();
		
		//depending on which bids user wants to see search in appropriate collection
		boolean showClosed = rbm.showClosed();
		List<Item> temp = showClosed ? closedAuctions : items;
		
		// for every item in that list
		for (Item i : temp)
		{
			//iterate over its bids
			PriorityQueue<Bid> bids = i.getBids();
			Iterator<Bid> itr = bids.iterator();
		    while(itr.hasNext()) 
		    {
		    	Bid b = itr.next();
		    	if(b.getBidder().equals(username))
		    	{
		    		//if bid is made by that user send bid 
		    		bidsToReturn.add(b);
		    	}
		    }
		}
		//depending on user intentions write different things to log
		if (showClosed)
		{
			this.writeLog(("User " + "\"" + username + "\" requested to see his bids on closed items"));
		}
		else
		{
			this.writeLog(("User " + "\"" + username + "\" requested to see his active bids"));
		}
		return bidsToReturn;
	}
	
	//logging out a user
	public void handleLogOut(String username)
	{
		//remove from logged in list
		this.loggedClients.remove(username);
	}
	
	//update the log console in the server
	private void writeLog(String message)
	{
		//attach the time the event happened
		String time = getTime(new GregorianCalendar());
		//save event to file as well
		data.saveLog(time, message);
		this.updateLog(time, message);
	}
	
	//write automatically to the log console
	private void updateLog(String time, String message)
	{
		this.log.append(time + " " + message + "\n");
	}
	
	//get time of an event
	private String getTime(Calendar cal)
	{
		return (cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND) + " " +
				cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR));
	}
}
