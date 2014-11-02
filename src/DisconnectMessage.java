
//message that tells a client application is closed and closes the thread
public class DisconnectMessage extends Message
{
	public DisconnectMessage(String username)
	{
		super (username);
	}
}
