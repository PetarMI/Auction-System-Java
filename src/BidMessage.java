
//Message sent to server when user makes a bid
public class BidMessage extends Message
{
	//id of item to bid for 
	private Long itemID;
	//price of the bid
	private float price;
	
	//constructor of the message
	public BidMessage(Long id, float pr, String bidder)
	{
		super(bidder);
		this.itemID = id;
		this.price = pr;
	}
	
	//getter for id of the item
	public Long getID()
	{
		return this.itemID;
	}
	
	//getter for price of the bid
	public float getPrice()
	{
		return this.price;
	}
}
