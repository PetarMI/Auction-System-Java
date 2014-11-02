import java.io.Serializable;

//abstract class for every message
public abstract class Message implements Serializable
{
	//every message has a sender 
	private String user;
	
	public Message(String u)
	{
		this.user = u;
	}
	
	//method inherited by all subclasses
	public String getUser()
	{
		return this.user;
	}
}
