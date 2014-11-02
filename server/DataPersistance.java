import java.io.*;
import java.util.*;

public class DataPersistance 
{
	
	public DataPersistance()
	{
		
	}
	
	//write data to the server collections
	public void initServer(List<User> users, List<Integer> userIDs, List<Item> items, List<Long> itemIDs, List<Item> ca)
	{
		this.readUsers(users, userIDs);
		this.readItems(items, itemIDs);
		this.readAuctions(ca);
	}
	
	//write the users and the IDs
	private void readUsers(List<User> users, List<Integer> userIDs)
	{	
		//files to read from
		File usersFile = new File("users.txt");
		File IDsFile = new File("userIDs.txt");
		
		//if they exist read info from them
		if (usersFile.exists() && IDsFile.exists())
		{
			try
			{
				//open streams to both files
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream("users.txt"));
				ObjectInputStream oisIDs = new ObjectInputStream(new FileInputStream("userIDs.txt"));
				
				//read arraylist form file 
				@SuppressWarnings("unchecked")
				ArrayList<User> u = (ArrayList<User>) ois.readObject();
				//write objects to the server's collection
				for (User us : u)
				{
					users.add(us);
				}
				
				//write objects to the server's collection
				@SuppressWarnings("unchecked")
				ArrayList<Integer> id = (ArrayList<Integer>) oisIDs.readObject();
				for (Integer i : id)
				{
					userIDs.add(i);
				}
				
				//close streams
				ois.close();
				oisIDs.close();
			}
			catch(EOFException e)
			{	

			}
			catch(ClassNotFoundException e)
			{
				e.printStackTrace();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	//write the items and the IDs
	private void readItems(List<Item> items, List<Long> itemIDs)
	{
		//files to read from
		File itemsFile = new File("items.txt");
		File itemIDsFile = new File("itemIDs.txt");

		//if they exist read info from them
		if (itemsFile.exists() && itemIDsFile.exists())
		{
			try
			{
				//open streams to both files
				ObjectInputStream iois = new ObjectInputStream(new FileInputStream("items.txt"));
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream("itemIDs.txt"));
				
				//read arraylist form file 
				@SuppressWarnings("unchecked")
				ArrayList<Item> i = (ArrayList<Item>) iois.readObject();
				//write objects to the server's collection
				for (Item it : i)
				{
					items.add(it);
				}
				//close stream
				iois.close();
				
				//read arraylist form file 
				@SuppressWarnings("unchecked")
				ArrayList<Long> ids = (ArrayList<Long>) ois.readObject();
				//write objects to the server's collection
				for (Long l : ids)
				{
					itemIDs.add(l);
				}
				//close stream
				ois.close();
			}
			catch(EOFException e)
			{	
	
			}
			catch(ClassNotFoundException e)
			{
				e.printStackTrace();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	//reading the closed auctions from file
	private void readAuctions(List<Item> clAuc)
	{
		//file to read from
		File closedAuctions = new File("ClosedAuctions.txt");
		
		//if it exists read info from it
		if (closedAuctions.exists())
		{
			try
			{
				//open stream to file
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream("ClosedAuctions.txt"));
				
				//read arraylist from file
				@SuppressWarnings("unchecked")
				ArrayList<Item> ca = (ArrayList<Item>) ois.readObject();
				//write info to the server collection
				for (Item sauc : ca)
				{
					clAuc.add(sauc);
				}
				ois.close();
	
			}
			catch(EOFException e)
			{	
	
			}
			catch(ClassNotFoundException e)
			{
				e.printStackTrace();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/*register new user
	 * because in Java you cannot append items to a file whole arraylist is written again 
	*/
	public void registerUser(List<User> users, List<Integer> ids)
	{
		try
		{
			//open streams to files
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("users.txt"));
			ObjectOutputStream oosIDs = new ObjectOutputStream(new FileOutputStream("userIDs.txt"));
				
			//write the info for the new user
			oos.writeObject(new ArrayList<User>(users));
			oos.writeObject(new ArrayList<Integer>(ids));
				
			oos.flush();
			oos.close();
			
			oosIDs.flush();
			oosIDs.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/*start new auction
	 * because in Java you cannot append items to a file whole arraylist is written again 
	*/
	public void auctionItem(List<Item> items, List<Long> ids)
	{
		try
		{
			//open streams to files
			ObjectOutputStream ioos = new ObjectOutputStream(new FileOutputStream("items.txt"));
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("itemIDs.txt"));
			
			//write the info for the new user
			ioos.writeObject(new ArrayList<Item>(items));
			oos.writeObject(new ArrayList<Long>(ids));
			
			ioos.flush();
			ioos.close();
			
			oos.flush();
			oos.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	//used when a new bid is added to an item
	public void updateItem(List<Item> items)
	{
		try
		{
			//write whole arraylist to the file again
			ObjectOutputStream ioos = new ObjectOutputStream(new FileOutputStream("items.txt"));
			
			ioos.writeObject(new ArrayList<Item>(items));
			
			ioos.flush();
			ioos.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	//when an auction closes save it to a file
	public void writeClosedAuction(List<Item> ca)
	{
		try
		{
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("ClosedAuctions.txt"));
			
			oos.writeObject(new ArrayList<Item>(ca));
			
			oos.flush();
			oos.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	//append the info from the log file to the end of the file that stores server activity
	public void saveLog(String time, String s)
	{
		try
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter("serverActivity.txt", true));
			bw.write(time + " " + s + "\n");
			bw.flush();
			bw.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
