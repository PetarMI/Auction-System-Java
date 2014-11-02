import java.io.File;

public class Utils
{
	//extension that will be accepted by the TextFilter
    public final static String jpg = "jpg";
    public final static String png = "png";
    
    //Get the extension of a file
    
    public static String getExtension(File f)
    {
        String ext = null;
        String s = f.getName();
        //find the position of the last '.' in the string
        //Everything after it is the extension
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1)
        {
        	//the extension of the file is what is left after the last '.'
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
}
