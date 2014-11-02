
//message sent when a user wants to register
public class RegisterMessage extends Message
{
	//credentials of the user
	private String famName;
	private String email;
	private char[] password;
	
	//constructor
	public RegisterMessage(String u, String mail, char[] pass, String fn)
	{
		super(u);
		this.email = mail;
		this.password = pass;
		this.famName = fn;
	}
	
	//getter for password
	public char[] getPassword()
	{
		return this.password;
	}
	
	//getter for user name
	public String getFamilyName()
	{
		return this.famName;
	}
	
	//getter for user email
	public String getEmail()
	{
		return this.email;
	}
}
