package marks.rangecard.pro;

import android.app.Activity;
import android.widget.*;
import android.view.*;
import android.content.*;
import android.database.DataSetObserver;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.util.*;

public class TargetAdapter implements ListAdapter
{
	private Activity ctx;
	private DataManager dataManager;
	private LayoutInflater inflater;
	private Vector<TargetInfo> targets;
    private final static String RANGE_FORMAT = "%.2f";
    private final static String BRG_FORMAT = "%.1f";
    static String UNITS = "units";
  //  static String BRNG_UNITS = "brng_units";
    private String units = "m";
    ListView listView;

    public TargetAdapter(Activity ctx)
    {
    	this.ctx=ctx;
        //this.listView = listView;
    	dataManager = DataManager.getInstance(ctx);
    	inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        units = sp.getString(UNITS, units);
        targets = dataManager.getTargets();
    }
    
    public int getCount()
    {
    	return targets.size()+1;
    }
    
    public String getItem(int idx)
    {
        if ( idx < 1 ) return "GPS";
        else idx--;
        TargetInfo ti = targets.get(idx);
    	return ti.name;
    }

    public String getTargetID(int idx)
    {
        if ( idx < 1 ) return "GPS";
        else idx--;
        TargetInfo ti = targets.get(idx);
        return ti.id;
    }

    public long getItemId(int idx)
    {
    	return (long) idx;
    }
    
    public View getView(int position, View convertView, ViewGroup parent)
    {
       // if (convertView != null ) return convertView;
        if (position < 1)
        {
            View gps = getGPSItem();
            gps.setOnClickListener(new TargetListener(position));
            return gps;
        }
        ViewGroup vg = null;
        if (( position % 2) > 0) vg = (ViewGroup) inflater.inflate(R.layout.target_item, null);
        else vg = (ViewGroup) inflater.inflate(R.layout.target_item_lg, null);

    	//TextView ttype = (TextView) vg.findViewById(R.id.target_type);
        ImageView swatch = (ImageView) vg.findViewById(R.id.swatch);
        TextView tname = (TextView) vg.findViewById(R.id.target_name);
        TextView trange = (TextView) vg.findViewById(R.id.target_range);
        TextView tbrng = (TextView) vg.findViewById(R.id.target_bearing);

        targets = dataManager.getTargets();
        TargetInfo ti = targets.get(position-1);
        if ( ti.type > 0 )swatch.setImageResource(R.drawable.frienddroidx);

        else
        {
            switch ( ti.color)
            {
                case 0: swatch.setImageResource(R.drawable.azure_circle);
                    break;
                case 1: swatch.setImageResource(R.drawable.blue_circle);
                    break;
                case 2: swatch.setImageResource(R.drawable.cyan_circle);
                    break;
                case 3: swatch.setImageResource(R.drawable.green_circle);
                    break;
                case 4: swatch.setImageResource(R.drawable.magenta_circle);
                    break;
                case 5: swatch.setImageResource(R.drawable.orange_circle);
                    break;
                case 6: swatch.setImageResource(R.drawable.red_circle);
                    break;
                case 7: swatch.setImageResource(R.drawable.rose_circle);
                    break;
                case 8: swatch.setImageResource(R.drawable.violet_circle);
                    break;
                case 9: swatch.setImageResource(R.drawable.yellow_circle);

            }
        }

     //   else ttype.setText("Target: ");
        tname.setText(ti.name);

        double rng = UnitConvertor.convertDistance((double) ti.rng, units);
        String s = String.format(RANGE_FORMAT, rng);
        String abr = UnitConvertor.getAbbr(units);
        s = "Range: "+s+" "+abr;
        trange.setText(s);

        float br = ti.brng;
        if (br < 0) br+=360;
        s = String.format(BRG_FORMAT, br);
        s = "Bearing: "+s;
        tbrng.setText(s);
        vg.setOnClickListener(new TargetListener(position));

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        String refpt = sp.getString("refPoint", "gps");
        ImageView drd = (ImageView) vg.findViewById(R.id.droid);
        if (refpt.equals(ti.id)) drd.setVisibility(View.VISIBLE);
        else drd.setVisibility(View.GONE);

        return vg;
    }

    View getGPSItem()
    {
        ViewGroup vg = (ViewGroup) inflater.inflate(R.layout.gps_item, null);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        String refpt = sp.getString("refPoint", "gps");
        ImageView drd = (ImageView) vg.findViewById(R.id.droid);
        if (refpt.equals("gps")) drd.setVisibility(View.VISIBLE);
        else drd.setVisibility(View.GONE);
        return vg;
    }
    
    public boolean hasStableIds()
    {
    	return false;
    }
    
    public int getViewTypeCount()
    {
    	return 2;
    }
    
    public boolean isEnabled(int pos)
    {
    	return true;
    }
    
    public boolean areAllItemsEnabled()
    {
    	return true;
    }
    
    public int getItemViewType(int position)
    { 
    	if (position < 1) return 0;
        else return 1;
    }
    
    public boolean isEmpty() 
    {
    	return (targets.size() < 1);
    }
    
    public void registerDataSetObserver (DataSetObserver observer){}
    public void unregisterDataSetObserver (DataSetObserver observer){}

    class TargetListener implements View.OnClickListener
    {
        int idx;
        public TargetListener(int idx)
        {
            this.idx = idx;
        }
        public void onClick(View v)
        {
            if (idx < 1 )
            {
                Intent intent = new Intent();
                intent.setClassName("marks.rangecard.pro", "marks.rangecard.pro.GPSActivity");
                ctx.startActivityForResult(intent, 18);
            }
            else
            {
                targets = dataManager.getTargets();
                TargetInfo ti = targets.get(idx-1);
                Intent intent = new Intent();
                intent.setClassName("marks.rangecard.pro", "marks.rangecard.pro.TargetActivity");
                ///  dataManager.save();
                intent.putExtra("id", ti.id);
                ctx.startActivityForResult(intent, 13);
            }
        }
    }
    
}