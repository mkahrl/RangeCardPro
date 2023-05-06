package marks.rangecard.pro;

import android.graphics.*;
import android.content.*;
import android.view.*;
import android.widget.*;

public class ColorAdapter extends ArrayAdapter
    {
    	public ColorAdapter(Context context, int layout, Object[] colors)
    	{
    		super(context, layout, colors);
    	}
    	
    	public static int getColor(int pos)
    	{
    		switch (pos)
		 	{
			    case 0: return Color.BLUE;
			    case 1: return Color.RED;
			    case 2: return Color.GREEN;
			    case 3: return Color.YELLOW;
			    case 4: return Color.CYAN;
			    case 5: return Color.MAGENTA;
			    case 6: return Color.BLACK;
			    case 7: return Color.WHITE;
		 	}
		 	return Color.RED;
    	}
    	public View getView(int pos, View convert, ViewGroup parent)
    	{
    		
    		View v = super.getView(pos, convert, parent);
    		v.setBackgroundColor(getColor(pos));
    		if ( v instanceof TextView )
    		{
    			TextView tv = (TextView) v;
    			tv.setTextColor(getColor(pos));
    		}
    		return v;
    	}
    	
    	public View getDropDownView(int pos, View convert, ViewGroup parent)
    	{
    		View v = super.getView(pos, convert, parent);
    		v.setBackgroundColor(getColor(pos));
    		if ( v instanceof TextView )
    		{
    			TextView tv = (TextView) v;
    			tv.setTextColor(getColor(pos));
    		}
		return v;
    	}
    }
