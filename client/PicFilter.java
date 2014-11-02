import java.io.File;
import javax.swing.filechooser.*;
 
//Text filter used by 'JavaFiles'
public class PicFilter extends FileFilter 
{
    //Accept all directories and all png and jpeg files.
    public boolean accept(File f)
    {
        if (f.isDirectory()) 
        {
            return true;
        }
 
        //get the extension using static method from Utils class
        String extension = Utils.getExtension(f);
        
        if (extension != null)
        {
        	//Display file if it has the right extension according to the filter
            if (extension.equals(Utils.png) || extension.equals(Utils.jpg))
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
 
    //The description of this filter
    public String getDescription()
    {
        return "Just images";
    }
}