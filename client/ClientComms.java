import java.net.*;
import java.util.ArrayList;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ClientComms
{
	//socket of the client
	private Socket client;
	//output streams on the client side
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	
	public ClientComms()
	{
		
	}
	
	//connect to the server
	public void connect(int port)
	{
		try
		{
			//connect to specified port
			this.client = new Socket("localhost", port);
			//open streams
			oos = new ObjectOutputStream(client.getOutputStream());
			ois = new ObjectInputStream(client.getInputStream());
		}
		catch (IOException e)
		{
			System.err.println("Could not connect to server");
		}
	}
	
	//sending message to the server
	public void sendMessage(Message m)
	{
		try 
		{
			//write message through socket
			oos.writeObject(m);
		}
		catch (IOException e)
		{
			System.err.println("Could not send the register message");
		}
	}
	
	//receive result of the successfulness of an operation
	public boolean receiveConfirmationMessage()
	{
		try
		{
			//object received is a boolean to indicate if an operation is successful
			//no need for a separate message class that just wraps a simple boolean
			boolean success = (boolean) ois.readObject();
			return success;
		}
		catch(ClassNotFoundException | IOException e)
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
	//send user object to client if login is successful
	public User receiveLogInConfirmation()
	{
		try
		{
			//get the USer object from the socket in case of a successful login
			User user = (User) ois.readObject();
			return user;
		}
		catch(ClassNotFoundException | IOException e)
		{
			e.printStackTrace();
		}
		
		//null if the login was unsuccessful and no client has logged in
		return null;
	}
	
	//receiving items that were requested by user
	public ArrayList<Item> receiveItems()
	{
		try
		{
			@SuppressWarnings("unchecked")
			//object is an arraylist of items that match the user search criteria
			//no need for a separate message class that just wraps a simple arraylist
			ArrayList<Item> list = (ArrayList<Item>) ois.readObject();
			return list;
		}
		catch(ClassNotFoundException | IOException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	//receiving bids that were requested by user
	public ArrayList<Bid> receiveBids()
	{
		try
		{
			@SuppressWarnings("unchecked")
			//object is an arraylist of bids that match the user search criteria
			//no need for a separate message class that just wraps a simple arraylist
			ArrayList<Bid> list = (ArrayList<Bid>) ois.readObject();
			return list;
		}
		catch(ClassNotFoundException | IOException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	//closing the client socket and its streams
	public void close()
	{
		try
		{
			client.close();
			oos.flush();
			oos.close();
			ois.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
