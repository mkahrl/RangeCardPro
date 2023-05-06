package marks.rangecard.pro;

import android.app.*;
import android.content.*;
import android.widget.*;

public class EnableGPSDialog extends AlertDialog
{
    public EnableGPSDialog(Context ctx)
    {
    	super(ctx);
    	setTitle("Enable GPS"); 
    	setMessage("Please enable GPS: Settings -> Location. Set 'Mode' to 'High Accuracy'");
    	setButton(BUTTON_NEGATIVE, "Close", new ClickListener());
    }
    
    class ClickListener implements DialogInterface.OnClickListener
    {
    	public void onClick(DialogInterface dialog, int which)
    	{
    		switch(which)
    		{
    		   case BUTTON_NEGATIVE:
    				dismiss();
    				break;
    		}
    	}
    }
}