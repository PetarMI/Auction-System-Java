
//send for log in request
public class LogInMessage extends Message
{
	//password for the username
	private char[] password;
	
	public LogInMessage(String u, char[] pass)
	{
		super(u);
		this.password = pass;
	}
	
	//getter for password
	public char[] getPassword()
	{
		return this.password;
	}
}
