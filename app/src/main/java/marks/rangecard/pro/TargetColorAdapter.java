package marks.rangecard.pro;

import android.graphics.*;
import android.content.*;
import android.view.*;
import android.widget.*;
import android.content.*;

public class TargetColorAdapter extends ArrayAdapter
{
        LayoutInflater inflator;
    	public TargetColorAdapter(Context context, int layout, Object[] colors)
    	{
    	    super(context, layout, colors);
            inflator = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	}
    	
    	public static int getColor(int pos)
    	{
    		switch (pos)
		{
			case 0: return getColor("#007FFF");// azure
			case 1: return Color.BLUE;
			case 2: return Color.CYAN;
			case 3: return Color.GREEN;
			case 4: return Color.MAGENTA;
			case 5: return getColor("#ffa500"); // orange
			case 6: return Color.RED;
			case 7: return getColor("#FF66CC"); // rose
			case 8: return getColor("#7F00FF"); // violet
			case 9: return Color.YELLOW; // yellow
		}
		return Color.BLACK;
    	}
        
        static int getColor(String s)
        {
            int c = Color.BLACK;
            try
            {
                c = Color.parseColor(s);
            }
            catch(Exception e)
            {
                System.out.println(e.toString());
            }
            return c;
        }
        
    	public View getView(int pos, View convert, ViewGroup parent)
    	{
            if ( convert != null ) return convert;
            View v = super.getView(0, convert, parent);
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
