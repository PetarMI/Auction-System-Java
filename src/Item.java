
import java.io.Serializable;
import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.PriorityQueue;

import javax.swing.ImageIcon;

public class Item implements Serializable
{
	//static variables that hold the state of the auction
	private static String ONGOING = "Ongoing";
	private static String TOSTART = "To start";
	private static String CLOSED = "Closed";
	
	//title of item
	private String title;
	private String description;
	//photo of the item
	private ImageIcon pic;
	private String category;
	//username of the seller
	private String seller;
	//id of the seller
	private Integer sellerID;
	private long IDcode;	
	//start and end time of the auction
	private Calendar startTime = new GregorianCalendar();
	private Calendar closeTime = new GregorianCalendar();	
	private float reservePrice;
	
	//current state of the auction
	private String status;
	//indicates whether there is a winner or not
	private boolean success = false;
	//priority queue to hold highest bid on top
	private PriorityQueue<Bid> bids;
	
	//constructor for an item
	public Item(String t, String d, ImageIcon img, String cat, String seller, Integer sellerID, Long code, Float pr, Calendar st, Calendar ed)
	{
		this.title = t;
		this.description = d;
		this.pic = img;
		this.category = cat;
		this.seller = seller;
		this.sellerID = sellerID;
		this.IDcode = code;
		this.reservePrice = pr;
		this.startTime = st;
		this.closeTime = ed;
		
		bids = new PriorityQueue<Bid>();
	}
	
	//add a bid for that item
	public void addBid(Bid bid)
	{
		bids.add(bid);
	}
	
	//check the highest bid
	public Bid checkHighestBid()
	{
		return bids.peek();
	}
	
	//getter for title
	public String getTitle()
	{
		return this.title;
	}
	
	//getter for description
	public String getDescription()
	{
		return this.description;
	}
	
	//getter for photo
	public ImageIcon getPhoto()
	{
		return this.pic;
	}
	
	//getter for category
	public String getCategory()
	{
		return this.category;
	}
	
	public long getID()
	{
		return this.IDcode;
	}
	
	//getter for username of the seller
	public String getSeller()
	{
		return this.seller;
	}
	
	//getter for id of the seller
	public Integer getSellerID()
	{
		return this.sellerID;
	}
	
	//getter for reserve price of the item
	public float getReservePrice()
	{
		return this.reservePrice;
	}
	
	public PriorityQueue<Bid> getBids()
	{
		return this.bids;
	}
	
	public Calendar getStartTime()
	{
		return this.startTime;
	}
	
	public Calendar getEndTime()
	{
		return this.closeTime;
	}
	
	//getter for the state of the auction
	public String getStatus()
	{
		return this.status;
	}
	
	//make auction active
	public void setActive()
	{
		this.status = ONGOING;
	}

	//set state of auction to not started yet
	public void setToStart()
	{
		this.status = TOSTART;
	}

	//close auction
	public void setClosed()
	{
		this.status = CLOSED;
	}
	
	//check if auction is in not started yet
	public boolean isToStart()
	{
		return this.status.equals(TOSTART);
	}
	
	//check if auction is active
	public boolean isActive()
	{
		return this.status.equals(ONGOING);
	}
	
	//check if auction is closed
	public boolean isClosed()
	{
		return this.status.equals(CLOSED);
	}
	
	//set the success of the auction
	public void setSuccess(boolean s)
	{
		this.success = s;
	}
	
	public boolean getSuccess()
	{
		return this.success;
	}
	
	//compare items by name
	public static Comparator<Item> ItemNameComparator = new Comparator<Item>() {
		public int compare(Item item1, Item item2) 
		{
			String itemTitle1 = item1.getTitle().toLowerCase();
			String itemTitle2 = item2.getTitle().toLowerCase();

			//ascending order
			return itemTitle1.compareTo(itemTitle2);
		}
	};
	
	//compare items by seller username
	public static Comparator<Item> ItemSellerComparator = new Comparator<Item>() {
		public int compare(Item item1, Item item2) 
		{
			String itemSeller1 = item1.getSeller().toLowerCase();
			String itemSeller2 = item2.getSeller().toLowerCase();

			//ascending order
			return itemSeller1.compareTo(itemSeller2);
		}
	};
	
	//compare items by end date of the auction
	public static Comparator<Item> ItemEndDateComparator = new Comparator<Item>() {
		public int compare(Item item1, Item item2) 
		{
			Calendar itemDate1 = item1.getEndTime();
			Calendar itemDate2 = item2.getEndTime();

			//ascending order
			return itemDate1.compareTo(itemDate2);
		}
	};
	
	@Override
	public String toString()
	{
		return (this.title + " (Seller: " + this.seller + ") " + status);
	}
	
}
