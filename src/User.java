import java.io.Serializable;

//user that uses the system
public class User implements Serializable
{
	//username of the user
	private String name;
	//real name of user
	private String fullName;
	private Integer userID;
	private String email;
	private char[] password;

	//constructor for user
	public User(String name, String famName, char[] pass, Integer id, String email)
	{
		this.name = name;
		this.fullName = famName;
		this.password = pass;
		this.userID = id;
		this.email = email;
	}
	
	//get user password
	public char[] getPassword()
	{
		return this.password;
	}
	
	//get username
	public String getName()
	{
		return this.name;
	}
	
	//get real name of user
	public String getFamName()
	{
		return this.fullName;
	}
	
	public Integer getID()
	{
		return userID;
	}
	
	public String getEmail()
	{
		return this.email;
	}
}
