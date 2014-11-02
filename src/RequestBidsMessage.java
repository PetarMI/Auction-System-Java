
//message when user wants to see his bids on active or closed auctions
public class RequestBidsMessage extends Message
{
	//indicating which bids the user wants to see (active or closed)
	private boolean showClosed;
	
	public RequestBidsMessage(String name, boolean sc)
	{
		super(name);
		this.showClosed = sc;
	}
	
	public boolean showClosed()
	{
		return this.showClosed;
	}
}
