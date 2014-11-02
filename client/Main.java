
public class Main 
{
	public static void main(String[] args)
	{
		//application starts by opening a login page
		LogInPage p = new LogInPage(Integer.parseInt(args[0]));
		p.init();
	}
}
