package marks.rangecard.pro;

import android.app.*;
import android.content.*;
import android.widget.*;

public class DeleteTargetDialog extends AlertDialog
{
    TargetActivity targAct;
    
    
    public DeleteTargetDialog(TargetActivity ta)
    {
    	super(ta);
    	this.targAct=ta;
    	
    	setTitle("Delete Location");
    	setMessage("Delete: "+targAct.info.name+" ?");
    	setButton(BUTTON_POSITIVE, "Delete", new ClickListener());
    	setButton(BUTTON_NEGATIVE, "Cancel", new ClickListener());
    }
    
    class ClickListener implements DialogInterface.OnClickListener
    {
    	public void onClick(DialogInterface dialog, int which)
    	{
    		switch(which)
    		{
    		   case BUTTON_POSITIVE:
                    targAct.removeTarget();
    				dismiss();
    				break;
    		   case BUTTON_NEGATIVE:
    				dismiss();
    				break;
    		}
    	}
    }
}