import java.util.*;

import javax.mail.MessagingException;
import javax.swing.JTextArea;

public class AuctionsThread implements Runnable
{
	//reference the collections of the server
	private List<Item> items;
	private List<Item> closedAucs;
	private List<User> users;
	//temporarily stores auction that have finished
	private List<Item> temp;
	//used to notify users of winning an auction
	private EmailSender mailSender;
	private DataPersistance dataPers;
	private Calendar cal;
	//area of server GUI that displays closed auctions
	private JTextArea area;

	public AuctionsThread(List<Item> i, List<Item> ca, List<User> u, DataPersistance dp, JTextArea ar)
	{
		this.items = i;
		this.closedAucs = ca;
		this.users = u;
		this.dataPers = dp;
		this.area = ar;
		temp = new ArrayList<Item>();
		this.mailSender = new EmailSender();
	}
	
	@Override
	public void run() 
	{	
		//indicate if there are changes to the lists of items so that thread knows if it should save to file
		boolean toSave = false;
		
		//constantly loop through all items to check if auctions should start or end
		while(true)
		{
			for (Item i : items)
			{			
				//get the current time to compare against
				cal = new GregorianCalendar();
				//if start time of auction is after the current time auction should start 
				if (i.getStartTime().before(cal))
				{
					i.setActive();
				}
				
				//check if auction has finished
				if (i.getEndTime().before(cal))
				{		
					//check if the reserve price has been met
					if (!i.getBids().isEmpty() && i.getBids().peek().getPrice() >= i.getReservePrice())
					{
						//find winner's email address
						String mailAddress = findUserEmail(i.getBids().peek().getBidder());
						//message to the winner
						String message = ("You just won the auction for " + i.getTitle() + " with the bid of " + i.getBids().peek().getPrice());
						//send mail to winner
						try
						{
							this.mailSender.sendMail(mailAddress, message);
						}
						catch(MessagingException e)
						{
							System.err.println("Email could not be sent");
						}
						//set state of the auction to successful
						i.setSuccess(true);
					}
					//if price hasnt been met
					else
					{
						i.setSuccess(false);
					}
					
					//temprarily pur auction in that list
					this.temp.add(i);
					i.setClosed();
					//write to server area
					area.append(this.formatAuction(i));
					//there is a change in auctions and info should be written to files
					toSave = true;
				}
			}
			
			//if there is change in auctions
			if(toSave)
			{
				//add to closed actions
				this.closedAucs.addAll(temp);
				//remove from active auctions
				this.items.removeAll(temp);
				//save info to files
				dataPers.writeClosedAuction(closedAucs);
				dataPers.updateItem(items);
				temp.clear();
				toSave = false;
			}
		}
	}

	//format the auction when writing to the server log
	private String formatAuction(Item i)
	{
		if (i.getSuccess())
		{
			return ("User: " + i.checkHighestBid().getBidder() + " won " + i.getTitle() + "\n");
		}
		else
		{
			return ("Auction for " + i.getTitle() + " sold by " + i.getSeller() + " was unsuccessful" + "\n");
		}
	}
	
	//find the user email
	private String findUserEmail(String username)
	{
		//loop through every user until username of winner is found
		for (User u : users)
		{
			if (u.getName().equals(username))
			{
				return u.getEmail();
			}
		}
		
		return null;
	}
}
