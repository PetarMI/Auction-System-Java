
//when user wants to see a list of items
public class RequestItemsMessage extends Message
{
	//seller of the item
	private String seller;
	//category of the item
	private String category;
	//id of the item
	private Long id;
	//search or not for inactive auctions
	private boolean showInactive;
	//search or not for closed auctions (when user wants to see his closed auctions)
	private boolean showClosed;
	
	//constructor
	public RequestItemsMessage(String sender, String s, String cat, Long id, boolean showInactive, boolean showClosed)
	{
		super(sender);
		this.seller = s;
		this.category = cat;
		this.id = id;
		this.showInactive = showInactive;
		this.showClosed = showClosed;
	}
	
	//get the seller to search for
	public String getSeller()
	{
		return this.seller;
	}
	
	//get the category to search for
	public String getCategory()
	{
		return this.category;
	}
	
	//get the id to search for
	public Long getID()
	{
		return this.id;
	}
	
	//show inactive auctions or not
	public boolean showInactive()
	{
		return this.showInactive;
	}
	
	//show closed auctions or not
	public boolean showClosed()
	{
		return this.showClosed;
	}
}
