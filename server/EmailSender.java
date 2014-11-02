import java.util.Properties;
 
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender
{
	//email that mail is sent from to every user
	private final String username = "petar2394@gmail.com";
	private final String password = "p53sot8SH";
	
	//create a new session
	private Session session;
	
	public EmailSender()
	{
		//set properties so that emails are sent from a gmail account
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
 
		//authenticate the provided email is valid and can send mails
		this.session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });
	}
	
	//send an email to a user with the selected text
	public void sendMail(String email, String text) throws MessagingException
	{
		Message message = new MimeMessage(this.session);
		//sender - system 
		message.setFrom(new InternetAddress(this.username));
		message.setRecipients(Message.RecipientType.TO,	InternetAddress.parse(email));
		message.setSubject("Auction System Message");
		message.setText(text);
 
		//send message
		Transport.send(message); 
	}
}
