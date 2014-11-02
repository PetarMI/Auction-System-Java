import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;

public class ServerComms 
{
	private ServerSocket serverSocket;
	//socket of the server that is connected to the client socket
	private Socket server;
	//socket streams
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	
	//constructor used when Server starts
	public ServerComms(int port)
	{
		try
		{
			serverSocket = new ServerSocket(port);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	//constructor used to pass connection to a client to a thread that deals with that client
	public ServerComms(Socket s)
	{
		this.server = s;
		try
		{
			oos = new ObjectOutputStream(server.getOutputStream());
			ois = new ObjectInputStream(server.getInputStream());
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	//allow clients to connect to server
	public void connect()
	{
		try
		{
			server = serverSocket.accept();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	//after a client connects pass that info to the thread for that client
	public Socket passConnection()
	{
		return this.server;
	}
	
	//receive message from client
	public Message receiveMessage()
	{
		try 
		{
			Message m = (Message) ois.readObject();
			return m;
		} 
		catch (ClassNotFoundException | IOException e) 
		{
			this.close();
		}
		return null;
	}
	
	//send info to user for the success of an operation
	//no message class because there is no need for a separate class that is just used to store a boolean
	public void sendConfirmation(boolean success)
	{
		try
		{
			//send info to connected socket
			oos.writeObject(success);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	//Send a User object if login was successful
	public void sendLogInSuccess(User user)
	{
		try
		{
			oos.writeObject(user);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	//send array of the items that match users criteria
	public void sendItems(ArrayList<Item> items)
	{
		try
		{
			oos.writeObject(items);
			//reset so that new items are sent next time
			oos.reset();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	//send bids that match user criteria
	public void sendBids(ArrayList<Bid> bids)
	{
		try
		{
			oos.writeObject(bids);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	//close the connection
	public void close()
	{
		try
		{
			server.close();
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
