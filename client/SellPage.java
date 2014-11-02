import javax.imageio.ImageIO;
import javax.swing.*;

import com.toedter.calendar.JDateChooser;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;

public class SellPage extends JFrame
{
	//general item info
	private JTextField title = new JTextField(10);
	private JTextArea description = new JTextArea(10, 20);
	private JComboBox<String> category;
	//components for the auctioned item
	private JTextField resPrice = new JTextField(5);
	private JComboBox<Integer> startHour;
	private JComboBox<Integer> startMin;
	//JDateChooser is a component from JCalendar. See jar file 
	private JDateChooser sdc = new JDateChooser();
	private JComboBox<Integer> endHour;
	private JComboBox<Integer> endMin;
	private JDateChooser edc = new JDateChooser();
	
	//using current time for the start time of the auction
	private JCheckBox currentTime = new JCheckBox("Use current time", false);
	
	//buttons for user options
	private JButton sale = new JButton("Put for sale");
	private JButton cancel = new JButton("Cancel");
	
	//components for image selecting
	private JButton addPic = new JButton("Add photo");
	private JFileChooser fc = new JFileChooser();
	private ImageIcon pic = null;
	
	//user that is currently logged in
	private User user;
	private ClientComms comms;
	
	//Dates for the start of the auction
	GregorianCalendar auctionStart = new GregorianCalendar();
	GregorianCalendar auctionEnd = new GregorianCalendar();
	
	public SellPage(User u, ClientComms c)
	{
		this.user = u;
		this.comms = c;
		
		this.setSize(600, 500);
	}
	
	public void init()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		this.setContentPane(panel);
		
		//adding functionality to components connected to item optional info (description and photo)
		JScrollPane scr = new JScrollPane(description);
		fc.setAcceptAllFileFilterUsed(false);
		fc.setFileFilter(new PicFilter());
		
		//Initialize comboboxes
		String[] cats = {"Books", "Clothing", "Electronics", "Games", "Hardware", "Music", "Sport", "Software"};
		category = new JComboBox<String>(cats);
		Integer[] hours = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23};
		Integer[] mins = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59};
		startHour = new JComboBox<Integer>(hours);
		startMin = new JComboBox<Integer>(mins);
		endHour = new JComboBox<Integer>(hours);
		endMin = new JComboBox<Integer>(mins);
		
		//forbid user to select dates before the current date
		sdc.setMinSelectableDate(new Date());
		edc.setMinSelectableDate(new Date());
		
		//labels
		JLabel name = new JLabel("Title*");
		JLabel descr = new JLabel("Description");
		JLabel cat = new JLabel("Category*");
		JLabel price = new JLabel("Reserve price*");
		JLabel st = new JLabel("Start time*");
		JLabel sd = new JLabel("Start date*");
		JLabel et = new JLabel("End time*");
		JLabel ed = new JLabel("End date*");
		JLabel delimeter = new JLabel(":");
		JLabel delimeter2 = new JLabel(":");
		
		//arrange the GUI
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 0, 0, 10);
		//add labels
		panel.add(name, c);
		c.gridy = 1;
		panel.add(descr, c);
		c.gridy = 3;
		panel.add(cat, c);
		c.gridy = 4;
		panel.add(price, c);
		c.gridy = 5;
		panel.add(st, c);
		c.gridy = 6;
		panel.add(sd, c);
		c.gridy = 7;
		panel.add(et, c);
		c.gridy = 8;
		panel.add(ed, c);
		
		//add elements 
		c.gridwidth = 3;
		c.gridx = 1;
		c.gridy = 0;
		panel.add(title, c);
		c.gridheight = 2;
		c.gridy = 1;
		panel.add(scr, c);
		c.gridx = 1;
		c.gridheight = 1;
		c.gridy = 3;
		panel.add(category, c);
		c.gridy = 4;
		panel.add(resPrice, c);
		c.gridy = 6;
		panel.add(sdc, c);
		c.gridy = 8;
		panel.add(edc, c);
		
		//add comboboxes for times
		c.gridwidth = 1;
		c.gridx = 4; 
		c.gridy = 5;
		panel.add(currentTime, c);
		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_END;
		panel.add(startHour, c);
		c.gridx = 2;
		c.anchor = GridBagConstraints.CENTER;
		panel.add(delimeter, c);
		c.gridx = 3;
		c.anchor = GridBagConstraints.LINE_START;
		panel.add(startMin, c);
		c.gridx = 1;
		c.gridy = 7;
		c.anchor = GridBagConstraints.LINE_END;
		panel.add(endHour, c);
		c.gridx = 2;
		c.anchor = GridBagConstraints.CENTER;
		panel.add(delimeter2, c);
		c.gridx = 3;
		c.anchor = GridBagConstraints.LINE_START;
		panel.add(endMin, c);
		
		c.gridx = 1;
		c.gridy = 9;
		panel.add(addPic, c);
		c.gridy = 10;
		panel.add(sale, c);
		c.gridx = 3;
		panel.add(cancel, c);
		c.gridx = 0;
		c.gridy = 11;
		c.gridwidth = 2;
		panel.add(new JLabel("Fields marked with '*' are required"), c);
		
		//add listeners
		currentTime.addItemListener(new TimeListener());
		cancel.addActionListener(new CancelListener(this));
		addPic.addActionListener(new PicListener());
		sale.addActionListener(new SaleListener(this));
		this.setVisible(true);
	}
	
	//when user wants to sell the item
	class SaleListener implements ActionListener
	{
		private SellPage sp;
		
		public SaleListener(SellPage p)
		{
			this.sp = p;
		}
		
		public void actionPerformed(ActionEvent e) 
		{
			//get information from fields
			String t = title.getText();
			String descr = description.getText();
			String cat = category.getSelectedItem().toString();
			String pr = resPrice.getText();
			
			//multiple nested ifs provide specific info displayed to user for the errors
			if (checkTitle(t))
			{
				if (checkPrice(pr))
				{
					if (checkDateValid())
					{
						//after ensuring all info is correct send message to server
						comms.sendMessage(new SellItemMessage(user.getName(), user.getID(), t, descr, sp.pic, cat, Float.parseFloat(pr), auctionStart, auctionEnd));
						//receive the success of the operation
						boolean success = comms.receiveConfirmationMessage();
						//display info to user
						if(success)
						{
							JOptionPane.showMessageDialog(sp, "Item added successfully");
							sp.dispose();
						}
						else
						{
							JOptionPane.showMessageDialog(sp, "Item could not be put for sale", "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
					else
					{
						JOptionPane.showMessageDialog(sp, "Start of auction should be before its end", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
				else
				{
					JOptionPane.showMessageDialog(sp, "Invalid price", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			else
			{
				JOptionPane.showMessageDialog(sp, "Set a title for the item", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}	
	}
	
	//ensure title field is filled
	private static boolean checkTitle(String t)
	{
		if (!t.isEmpty())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private boolean checkDateValid()
	{
		//set the end date of the auction
		auctionEnd.setTime(edc.getDate());
		auctionEnd.set(GregorianCalendar.HOUR_OF_DAY, (Integer) endHour.getSelectedItem()); 
		auctionEnd.set(GregorianCalendar.MINUTE, (Integer) endMin.getSelectedItem()); 
		auctionEnd.set(GregorianCalendar.SECOND, 00); 
		
		//depending on user choice set the start time
		//if user wants to start the auction some time in the future
		if (!currentTime.isSelected())
		{
			//get user input form the comboboxes
			auctionStart.setTime(sdc.getDate());
			auctionStart.set(GregorianCalendar.HOUR_OF_DAY, (Integer) startHour.getSelectedItem()); 
			auctionStart.set(GregorianCalendar.MINUTE, (Integer) startMin.getSelectedItem());
			auctionStart.set(GregorianCalendar.SECOND, 00);
		}
		//if user wants the auction to start right away
		else
		{
			//set current time
			auctionStart.setTime(new Date());
			auctionStart.set(GregorianCalendar.SECOND, 00);
		}

		//ensure start date is before end date
		if (auctionStart.before(auctionEnd))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	//ensure price is valid
	private static boolean checkPrice(String rp)
	{
		//not empty and a number
		if (!rp.isEmpty() && rp.matches("\\d+\\.?\\d*"))
		{
			//price bigger than 0
			if (Double.parseDouble(rp) > 0)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		
		return false;
	}
	
	//forbids the user to choose a start date if 'use current date' is clicked
	class TimeListener implements ItemListener
	{
		public void itemStateChanged(ItemEvent e)
		{
			//if checkbox is selected disable calendar for the start date
			if (e.getStateChange() == ItemEvent.SELECTED)
			{
				startHour.setEnabled(false);
				startMin.setEnabled(false);
				sdc.setEnabled(false);
			}
			else
			{
				startHour.setEnabled(true);
				startMin.setEnabled(true);
				sdc.setEnabled(true);
			}
		}
	}
	
	//let user chose a photo for the item
	class PicListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			//open the custom dialog 
			int returnVal = fc.showDialog(null, "Open");
			
			//if the user has selected a file
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				//save it in a file
	            File file = fc.getSelectedFile();
	            
	            try
	            {
	            	//turn chosen file into image
	            	BufferedImage img = ImageIO.read(file);
	            	//get type of the image
	            	int type = img.getType() == 0? BufferedImage.TYPE_INT_ARGB : img.getType();
	            	//get new dimensions for the image
	            	Dimension newSize = getScaledDimension(new Dimension(img.getWidth(),  img.getHeight()), new Dimension(200, 200));
	            	//get the new resized image
	            	pic = new ImageIcon(resizeImage(img, type, newSize));
	            	
	            	//show photo to the user and ask if they want to keep it
	            	int dialogButton = JOptionPane.YES_NO_OPTION;
	            	int dialogResult = JOptionPane.showConfirmDialog(null, new JLabel(pic), "Keep current photo",dialogButton);
	            	if (dialogResult == JOptionPane.NO_OPTION)
	            	{
	            		pic = null;
	            	}
	            }
	            catch(IOException e1) 
	            {
	            	e1.printStackTrace();
	            }
	        } 
		}
		
		private BufferedImage resizeImage(BufferedImage originalImage, int type, Dimension dim)
		{
			BufferedImage resizedImage = new BufferedImage((int)dim.getWidth(), (int)dim.getHeight(), type);
			//resize the image
			Graphics2D g = resizedImage.createGraphics();
			g.drawImage(originalImage, 0, 0, (int)dim.getWidth(), (int)dim.getHeight(), null);
			g.dispose();
		 
			return resizedImage;
		}
		
		//get new Dimensions of the image
		private Dimension getScaledDimension(Dimension imgSize, Dimension boundary)
		{
		    int originalWidth = imgSize.width;
		    int originalHeight = imgSize.height;
		    int boundWidth = boundary.width;
		    int boundHeight = boundary.height;
		    int newWidth = originalWidth;
		    int newHeight = originalHeight;

		    // first check if there is need to scale width
		    if (originalWidth > boundWidth) 
		    {
		        //scale width to fit
		        newWidth = boundWidth;
		        //scale height to maintain aspect ratio
		        newHeight = (newWidth * originalHeight) / originalWidth;
		    }

		    //check if there is need to scale even with the new height
		    if (newHeight > boundHeight)
		    {
		        //scale height to fit instead
		        newHeight = boundHeight;
		        //scale width to maintain aspect ratio
		        newWidth = (newHeight * originalWidth) / originalHeight;
		    }

		    //return new size of the image
		    return new Dimension(newWidth, newHeight);
		}
	}
	
	class CancelListener implements ActionListener
	{
		private SellPage sp;
		
		public CancelListener(SellPage sp)
		{
			this.sp = sp;
		}

		public void actionPerformed(ActionEvent e)
		{
			sp.dispose();
		}
	}
}
