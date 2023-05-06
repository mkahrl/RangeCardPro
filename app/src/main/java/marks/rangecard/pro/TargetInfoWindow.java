
package marks.rangecard.pro;

import android.app.Activity;
import android.view.ViewGroup;
import android.view.View;
import android.widget.*;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.android.gms.maps.model.Marker;

public class TargetInfoWindow extends FrameLayout
{
    Marker marker;
    private final static String RANGE_FORMAT = "%.2f";
    private final static String BEARING_FORMAT = "%.1f";
    static String UNITS = "units";
    
    public TargetInfoWindow(MainMapActivity act, Marker marker)
    {
        super(act);
        this.marker = marker;
        
        View contents = act.getLayoutInflater().inflate(R.layout.marker_info, null);
        addView(contents);
        
        TextView title = (TextView) contents.findViewById(R.id.title);
        TextView range = (TextView) contents.findViewById(R.id.range);
        TextView bearing = (TextView) contents.findViewById(R.id.bearing);
        
        title.setText(marker.getTitle());
        
        float [] rngBrg = act.getRangeAndBearing(marker);
        
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(act);
        String units = sp.getString(UNITS, "m");
        String abr = UnitConvertor.getAbbr(units);
        double rng = UnitConvertor.convertDistance((double) rngBrg[0], units);
        
        String r = String.format(RANGE_FORMAT, rng);
        if (rngBrg[1] < 0 ) rngBrg[1]+=360;
        String br = String.format(BEARING_FORMAT, rngBrg[1]);
        
        r = "Range: "+r+" "+abr;
        br = "Bearing: "+br;
        
        range.setText(r);
        bearing.setText(br);
    }
    
   
}