import java.io.Serializable;

public class Bid implements Serializable, Comparable<Bid>
{
	//username of the bidder
	private String bidder;
	//price of the bid
	private float price;
	//item that the bid is for
	private Item item;
	
	//constructor for the bod
	public Bid(String user, float pr, Item i)
	{
		this.bidder = user;
		this.price = pr;
		this.item = i;
	}
	
	//getter for user that made bid
	public String getBidder()
	{
		return this.bidder;
	}
	
	//getter for price of bid
	public float getPrice()
	{
		return this.price;
	}
	
	//getter for item
	public Item getItem()
	{
		return this.item;
	}

	@Override
	public String toString()
	{
		return (this.bidder + " " + this.price);
	}
	
	//compare two bids by price
	//used when putting them in a priority queue so highest is on top
	@Override
	public int compareTo(Bid o)
	{
		return (int) (o.price - this.price);
	}
}
