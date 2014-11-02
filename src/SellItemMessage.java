import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.ImageIcon;

//message sent when users opens an auction for an item
public class SellItemMessage extends Message
{
	//item information
	//only message that has public instance variables
	public String title;
	public String description;
	public ImageIcon pic;
	public String category;
	public Integer sellerID;
	public float reservePrice;
	
	//start and end time of the auction
	public Calendar startTime = new GregorianCalendar();
	public Calendar closeTime = new GregorianCalendar();
	
	//constructor for the message
	public SellItemMessage(String sender, Integer sellerID, String t, String d, ImageIcon img, String cat, Float pr, Calendar st, Calendar ed)
	{
		super(sender);
		this.sellerID = sellerID;
		this.title = t;
		this.description = d;
		this.pic = img;
		this.category = cat;
		this.reservePrice = pr;
		this.startTime = st;
		this.closeTime = ed;
	}
}
