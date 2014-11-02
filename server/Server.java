
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.*;

public class Server
{
	//communications used by the server
	public ServerComms comms;
	//users that are registered to the system
	private List<User> users;
	//IDs that users have. Used to quickly find unused ID for new user
	private List<Integer> userIDs;
	//items up for auction
	private List<Item> items;
	//IDs that items have. Used to quickly find unused ID for new items
	private List<Long> itemIDs;
	//finished auctions
	private List<Item> closedAuctions;
	/*map of logged clients
	* Server Comms are used to keep the communication to that user
	* not used in this version of the program but makes a future extension where server sends messages at all times easy*/
	private Map<String, ServerComms> loggedClients;
	//used to save all data to file
	private DataPersistance data;
	
	public Server(int port)
	{
		//create comms to that port
		comms = new ServerComms(port);
		//initialize all collections
		data = new DataPersistance();
		users = Collections.synchronizedList(new ArrayList<User>());
		userIDs = Collections.synchronizedList(new ArrayList<Integer>());
		items = new CopyOnWriteArrayList<Item>();
		itemIDs = Collections.synchronizedList(new ArrayList<Long>());
		closedAuctions =  new CopyOnWriteArrayList<Item>();
		loggedClients = new ConcurrentHashMap<String, ServerComms>();
	}
	
	public static void main(String[] args)
	{	
		System.setProperty("java.net.preferIPv4Stack" , "true");
		//bind server to that port
		Server serv = new Server(Integer.parseInt(args[0]));
		//read all data server has from files and write it to the collections
		serv.setUp();
		//Start the console view of the server
		Server.ServerGUI servGUI = serv.new ServerGUI();	
		servGUI.init();
		//start thread that checks when auctions start and finish 
		Runnable t = new AuctionsThread(serv.items, serv.closedAuctions, serv.users, serv.data, servGUI.area);
		new Thread(t).start();
		
		while(true)
		{
			//every time a client connects make a new thread to deal with that connection
			serv.connect();
			Runnable r = new ClientThread(new ServerComms(serv.passConnection()), serv.users, serv.userIDs, serv.items, serv.itemIDs, serv.closedAuctions, serv.data, serv.loggedClients, servGUI.activity);
			new Thread(r).start();
		}
	}
	
	//write data to collections
	public void setUp()
	{
		data.initServer(users, userIDs, items, itemIDs, closedAuctions);
	}
	
	//connects a client to the server
	public void connect()
	{
		comms.connect();
	}
	
	//used to pass the connected client to a new thread that deals with that client
	public Socket passConnection()
	{
		return this.comms.passConnection();
	}
	
	//server GUI
	class ServerGUI extends JFrame
	{
		//area displaying finished auctions state
		JTextArea area = new JTextArea(24, 25);
		//server activity
		JTextArea activity = new JTextArea(24, 38);
		
		public ServerGUI()
		{
			this.area.setEditable(false);
			this.activity.setEditable(false);
		}
		
		public void init()
		{
			JPanel panel = new JPanel();
			panel.setLayout(new GridBagLayout());
			this.setContentPane(panel);
			
			JScrollPane scr = new JScrollPane(area);
			JScrollPane log = new JScrollPane(activity);
			
			//Arrange GUI
			GridBagConstraints c = new GridBagConstraints();
			c.insets = new Insets(10, 0, 0, 10);
			panel.add(new JLabel("Closed Auctions"), c);
			c.gridx = 1;
			panel.add(new JLabel("Server Activity"), c);
			c.gridy = 1;
			c.gridx = 0;
			c.weightx = 1.0;
			c.weighty = 1.0;
			c.fill = GridBagConstraints.BOTH;
			panel.add(scr, c);
			c.gridx = 1;
			panel.add(log, c);

			//read the auctions from file 
			writeAuctions();
			//read all the log into the server
			initLog();
			
			this.pack();
			this.setVisible(true);
		}
		
		private void writeAuctions()
		{
			//display closed auctions to the area
			for (Item i : closedAuctions)
			{
				this.area.append(this.formatAuction(i) + "\n");
			}
		}
		
		//formatting of each auction
		private String formatAuction(Item i)
		{
			//depending on succes display different info in area
			if (i.getSuccess())
			{
				return ("User: " + i.checkHighestBid().getBidder() + " won " + i.getTitle() + "\n");
			}
			else
			{
				return ("Auction for " + i.getTitle() + " sold by " + i.getSeller() + " was unsuccessful" + "\n");
			}
		}
	
		//display all activity server has recorded before stopping last time
		private void initLog()
		{
			//file to read from
			File serverActivity = new File("serverActivity.txt");
			
			//if file exists then server has already run at least one time
			if (serverActivity.exists())
			{
				try
				{
					FileReader reader = new FileReader("serverActivity.txt");
			        BufferedReader br = new BufferedReader(reader);
			        //allow the default JTextArea method to read the contents from the file 
			        this.activity.read( br, null );
			        br.close();
				}
				catch(IOException e)
				{
					this.activity.append("Error while reading server log\n");
				}
			}
		}
	}

}	
